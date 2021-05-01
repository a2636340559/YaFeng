package com.experiment.yafeng.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.experiment.yafeng.R;
import com.experiment.yafeng.Util.HttpUtil;
import com.experiment.yafeng.Util.PoetryUtil;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.widget.edittext.materialedittext.MaterialEditText;
import com.xuexiang.xui.widget.spinner.materialspinner.MaterialSpinner;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.xuexiang.xui.XUI.getContext;

public class AIPoetryActivity extends AppCompatActivity {

    private String userId;
    private Toolbar toolbar;
    private TextView titleText;
    private String toolbarTitle;
    private Button startButton;
    private MaterialEditText inputText;
    private TextView createdText;
    private TagFlowLayout poem_tag_flow;
    private List<String> poem_tags;
    private TagFlowLayout poem_style_flow;
    // private MaterialSpinner ciPaiSpinner;
    private HashMap<String, String> poem_type;
    private HashMap<String, Integer> poem_style;
    private HashMap<String, String> selectedTag = new HashMap<>();
    private List<String> styles;
    private List<String> ciPai;
    private String createdPoem = "";
    private final int GET_POEM_FAIL = 0;
    private final int GET_POEM_SUCCESS = 1;
    private boolean isCreated = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            if (message.what == GET_POEM_SUCCESS) {
                System.out.println("CREATEDPOEMMMM:" + createdPoem);
                createdText.setText(createdPoem);
            }
            if (message.what == GET_POEM_FAIL) {
                createdText.setText(" ");
                Toast.makeText(getContext(), "服务错误，请重试", Toast.LENGTH_LONG).show();
            }
        }

    };

    private void initView() {
        poem_tags = PoetryUtil.getPoemTags();
        poem_type = PoetryUtil.getPoemType();
        poem_style = PoetryUtil.getPoemStyle();
        styles = PoetryUtil.getStyles();
        ciPai = PoetryUtil.getCiPai();
        userId = randomString(30);
        selectedTag.put("user_id", userId);
        System.out.println("THEFISTAPPEARUSERIDDDDD:" + selectedTag.get("user_id"));
        toolbar = findViewById(R.id.ai_titleBar);
        titleText = findViewById(R.id.ai_titleText);
        poem_tag_flow = findViewById(R.id.ai_poem_tag_flow);
        poem_style_flow = findViewById(R.id.ai_style_tag_flow);
        inputText = findViewById(R.id.input_keyword);
        startButton = findViewById(R.id.creating_button);
        createdText = findViewById(R.id.created_content);
        // ciPaiSpinner=findViewById(R.id.ai_ci_pai);
        final LayoutInflater mInflater = getLayoutInflater();
        poem_tag_flow.setAdapter(new TagAdapter<String>(poem_tags) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {

                TextView tv = (TextView) mInflater.inflate(R.layout.adapter_item_tag,
                        poem_tag_flow, false);
                tv.setText(s);
                return tv;
            }

            @Override
            public void onSelected(int position, View view) {
                super.onSelected(position, view);
                // ciPaiSpinner.setVisibility(View.INVISIBLE);
                //if(poem_tags.get(position).equals("词"))
                //ciPaiSpinner.setVisibility(View.VISIBLE);
                selectedTag.put("type", poem_type.get(poem_tags.get(position)));
                //showSelected(selectedTag);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.WHITE);
            }

            @Override
            public void unSelected(int position, View view) {
                super.unSelected(position, view);
                TextView textView = (TextView) view;
                selectedTag.remove("type");
                showSelected(selectedTag);
                textView.setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        });
        poem_style_flow.setAdapter(new TagAdapter<String>(styles) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {

                TextView tv = (TextView) mInflater.inflate(R.layout.adapter_item_tag,
                        poem_style_flow, false);
                tv.setText(s);
                return tv;
            }

            @Override
            public void onSelected(int position, View view) {
                super.onSelected(position, view);
                selectedTag.put("yan", poem_style.get(styles.get(position)).toString());
                //showSelected(selectedTag);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.WHITE);
            }

            @Override
            public void unSelected(int position, View view) {
                super.unSelected(position, view);
                TextView textView = (TextView) view;
                selectedTag.remove("yan");
                // showSelected(selectedTag);
                textView.setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        });
        // ciPaiSpinner.setItems(ciPai);
//        ciPaiSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
//                System.out.println("INDEXOFCIPAIIIIIII:"+(position+1));
//                selectedTag.put("yan",String.valueOf(position+1));
//            }
//        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        XUI.initTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aipoetry);
        initView();//初始化视图
        setSupportActionBar(findViewById(R.id.ai_titleBar));//设置toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);//屏蔽toolbar默认标题显示
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }//设置返回图标显示


        toolbarTitle = getIntent().getStringExtra("toolBarTitle");
        titleText.setText(toolbarTitle);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("YANNNNNNN:" + selectedTag.get("yan"));
                System.out.println("TYPEEEEEEE:" + selectedTag.get("type"));
                System.out.println("USERIDDDDD:" + selectedTag.get("user_id"));
                System.out.println("KEYWORDDDDD:" + inputText.getEditValue());
                createdText.setText("加载中...");
                isCreated = false;
                String type = selectedTag.get("type");
                String keyword = inputText.getEditValue();
                String address1 = "http://118.190.162.99:8080/sendPoem";
                String address2 = "http://118.190.162.99:8080/getPoem";
                RequestBody requestBody = new FormBody.Builder().add("type", type).add("yan", selectedTag.get("yan")).add("keyword", keyword).add("user_id", selectedTag.get("user_id")).build();
                System.out.println("requestBody11111:" + requestBody.toString());
                HttpUtil.sendPostOkHttpRequest(address1, requestBody, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        Message message = new Message();
                        message.what = GET_POEM_FAIL;
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseData = response.body().string();
                        System.out.println("SENDPOEMRESPONSEEE:" + responseData);
                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            if (jsonObject.get("code").equals("0000")) {
                                System.out.println("requestBody:" + requestBody.toString());
                                getPoem(address2, requestBody);
                            } else {
                                userId = randomString(30);
                                selectedTag.put("user_id", userId);
                                Message message = new Message();
                                message.what = GET_POEM_FAIL;
                                handler.sendMessage(message);

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });


    }


    private void showSelected(HashMap<String, String> temp) {
        if (temp.size() > 0) {
            for (String tempString : temp.keySet())
                System.out.println(tempString + ": " + temp.get(tempString));
        } else
            System.out.println("没有选择");
    }

    //随机生成userId
    public String randomString(int length) {
        int len = length;
        String chars = "ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678";
        int maxPos = chars.length();
        String pwd = "";
        for (int i = 0; i < len; i++) {
            pwd += chars.charAt((int) Math.floor(Math.random() * maxPos));
        }
        return pwd;
    }

    private void parseCreatedPoem(String temp) {
        StringBuilder stringBuilder = new StringBuilder(temp);
        System.out.println("TEMP:" + temp);
        String result = "";
        stringBuilder.setCharAt(0, ' ');
        stringBuilder.setCharAt(temp.length() - 1, ' ');
        temp = stringBuilder.toString().trim();
        System.out.println("TEMP111:" + temp);
        String[] temps = temp.split(",");
        for (String sentence : temps) {
            result += sentence;
            result += "\n";
        }
        System.out.println("RESULT:" + result);
        createdPoem = result;

    }

    private void getPoem(String address, RequestBody requestBody) {
        HttpUtil.sendPostOkHttpRequest(address, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Message message = new Message();
                message.what = GET_POEM_FAIL;
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // System.out.println("GETPOEMRESPONSE:"+response.body().string());
                createdPoem = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(createdPoem);
                    if (jsonObject.get("code").equals("1")) {
                        parseCreatedPoem(jsonObject.get("content").toString());
                        Message message = new Message();
                        isCreated = true;
                        message.what = GET_POEM_SUCCESS;
                        handler.sendMessage(message);
                    } else {
                        System.out.println("_______________________________isCreated:" + isCreated);
                        getPoem(address, requestBody);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
