package com.experiment.yafeng.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.experiment.yafeng.Modal.Poetry;
import com.experiment.yafeng.R;
import com.experiment.yafeng.Util.HttpUtil;
import com.experiment.yafeng.Util.PoetryUtil;
import com.experiment.yafeng.YaFeng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jinrishici.sdk.android.JinrishiciClient;
import com.jinrishici.sdk.android.factory.JinrishiciFactory;
import com.jinrishici.sdk.android.listener.JinrishiciCallback;
import com.jinrishici.sdk.android.model.JinrishiciRuntimeException;
import com.jinrishici.sdk.android.model.PoetySentence;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.widget.edittext.materialedittext.MaterialEditText;
import com.xuexiang.xui.widget.textview.label.LabelButtonView;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

import static com.xuexiang.xui.XUI.getContext;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TextView title;
    private TextView author;
    private TextView content;
    private TextView sentence;
    private TextView tag1;
    private LabelButtonView detail;
    private ImageView ic_menu;
    private TextView menu_line;
    private TextView tag2;
    private MaterialEditText searchText;
    private final int COMPLETED = 1;
    private final int RESPONSE_SUCCESS = 2;
    private final int RESPONSE_FAIL = 0;
    private final int TO_WEBVIEW = 3;
    private JSONObject poetryObject;
    private JSONArray collectObject;
    private Button testButton;
    private Button sureButton;
    private ImageView search;
    private DrawerLayout drawerLayout;
    private TagFlowLayout typeTagLayout;
    private TagFlowLayout dynastyTagLayout;
    private TagFlowLayout styleTagLayout;
    private RefreshLayout refreshLayout;
    private String[] typeList = null;
    private String[] dynastyList = null;
    private String[] styleList = null;
    private List<String> selectedType = new ArrayList<String>();
    private List<String> selectedStyle = new ArrayList<String>();
    private List<String> selectedDynasty = new ArrayList<String>();
    private boolean isLoad = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            if (message.what == COMPLETED) { //获取每日诗词成功
                try {
                    title.setText(poetryObject.getJSONObject("data").get("name").toString());
                    String dynasty_author = poetryObject.getJSONObject("data").
                            get("dynasty").toString() + " . "
                            + poetryObject.getJSONObject("data").get("author").toString();
                    author.setText(dynasty_author);
                    String contentText = poetryObject.getJSONObject("data").get("content").toString();
                    contentText.replaceAll("\\n", "");
                    content.setText(contentText);
                    refreshLayout.finishRefresh();
                    isLoad = true;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (message.what == RESPONSE_SUCCESS) { //获取用户收藏数据成功
                if (collectObject == null)
                    Toast.makeText(getContext(), "你还没有收藏哦", Toast.LENGTH_LONG).show();
                else {
                    Gson gson = new Gson();
                    List<Poetry> poetryList = gson.fromJson(collectObject.toString(),
                            new TypeToken<List<Poetry>>() {
                    }.getType());
                    Intent intent = new Intent(MainActivity.this,
                            PoemListActivity.class);
                    intent.putExtra("from", "collect");
                    intent.putExtra("toolBarTitle", "我的收藏");
                    intent.putExtra("collect", collectObject.toString());
                    startActivity(intent);
                }

            }
            if (message.what == RESPONSE_FAIL) { //没有收藏数据
                Toast.makeText(getContext(), "你还没有收藏哦", Toast.LENGTH_LONG).show();
            }
        }

    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tool_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return true;
    }

    private void initTagFlowLayout() {
        typeTagLayout = findViewById(R.id.type_float);
        dynastyTagLayout = findViewById(R.id.dynasty_float);
        styleTagLayout = findViewById(R.id.style_float);

        typeList = PoetryUtil.getType();//获取诗词类型标签
        dynastyList = PoetryUtil.getDynasty();//获取诗词朝代标签
        styleList = PoetryUtil.getStyle();//获取诗词风格标签

        final LayoutInflater mInflater = getLayoutInflater();

        typeTagLayout.setAdapter(new TagAdapter<String>(typeList) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {

                TextView tv = (TextView) mInflater.inflate(R.layout.adapter_item_tag,
                        typeTagLayout, false);
                tv.setText(s);
                return tv;
            }

            @Override
            public void onSelected(int position, View view) {
                super.onSelected(position, view);
                selectedType.add(typeList[position]);
                showSelected(selectedType);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.WHITE);
            }

            @Override
            public void unSelected(int position, View view) {
                super.unSelected(position, view);
                TextView textView = (TextView) view;
                selectedType.remove(textView.getText());
                showSelected(selectedType);
                textView.setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        });
        dynastyTagLayout.setAdapter(new TagAdapter<String>(dynastyList) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {

                TextView tv = (TextView) mInflater.inflate(R.layout.adapter_item_tag,
                        dynastyTagLayout, false);
                tv.setText(s);
                return tv;
            }

            @Override
            public void onSelected(int position, View view) {
                super.onSelected(position, view);
                selectedDynasty.add(dynastyList[position]);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.WHITE);
            }

            @Override
            public void unSelected(int position, View view) {
                super.unSelected(position, view);
                TextView textView = (TextView) view;
                selectedDynasty.remove(textView.getText());
                textView.setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        });
        styleTagLayout.setAdapter(new TagAdapter<String>(styleList) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {

                TextView tv = (TextView) mInflater.inflate(R.layout.adapter_item_tag,
                        styleTagLayout, false);
                tv.setText(s);
                return tv;
            }

            @Override
            public void onSelected(int position, View view) {
                super.onSelected(position, view);
                selectedStyle.add(styleList[position]);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.WHITE);
            }

            @Override
            public void unSelected(int position, View view) {
                super.unSelected(position, view);
                TextView textView = (TextView) view;
                selectedStyle.remove(textView.getText());
                textView.setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        });

    }

    //随机获取一首古诗
    private void getOnePoetry() {
        HttpUtil.sendOkHttpRequest(
                "http://39.106.193.194:8080/yafeng-1.0/poetry/getOnePoetry",
                new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                isLoad = false;
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                try {
                    poetryObject = new JSONObject(responseData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Message message = new Message();
                message.what = COMPLETED;
                isLoad = false;
                handler.sendMessage(message);
            }
        });
    }

    private void initView() {
        title = findViewById(R.id.title);
        author = findViewById(R.id.author);
        content = findViewById(R.id.content);
        sentence = findViewById(R.id.sentence);
        drawerLayout = findViewById(R.id.drawerlayout);
        tag1 = findViewById(R.id.tag1);
        tag2 = findViewById(R.id.tag2);
        testButton = findViewById(R.id.test_button);
        ic_menu = findViewById(R.id.menu_icon);
        menu_line = findViewById(R.id.menu_line);
        sureButton = findViewById(R.id.sure_button);
        search = findViewById(R.id.search_button);
        searchText = findViewById(R.id.search);
        detail = findViewById(R.id.detail);
        refreshLayout = findViewById(R.id.main_refreshLayout);
        initTagFlowLayout();
    }


    //弹出子菜单
    public void showPopupWindow() {
        PopupWindow popupWindow = new PopupWindow(getContext());
        View view = LayoutInflater.from(this).inflate(R.layout.layout_menu, null);
        popupWindow.setContentView(view);
        popupWindow.setHeight(450);
        popupWindow.setWidth(450);
        TextView account = view.findViewById(R.id.account1);
        account.setOnClickListener(new View.OnClickListener() { //点击账号
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        TextView collect = view.findViewById(R.id.collect1);
        collect.setOnClickListener(new View.OnClickListener() { //点击我的收藏
            @Override
            public void onClick(View v) {
                getCollect();
            }
        });
        //设置点击外部消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_style));
        popupWindow.showAsDropDown(menu_line);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        XUI.initTheme(this);
        super.onCreate(savedInstanceState);
        JinrishiciFactory.init(getContext());
        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.titleBar));//设置toolbar
        initView();//初始化视图
        getSupportActionBar().setDisplayShowTitleEnabled(false);//屏蔽toolbar默认标题显示
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_navigate1);
        }//设置返回图标显示

        ic_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow();
            }
        }); //弹出侧边栏
        getOnePoetry();//随机获取一首古诗

        //调用今日诗词api显示今日诗句
        JinrishiciClient client = new JinrishiciClient();
        client.getOneSentenceBackground(new JinrishiciCallback() {
            @Override
            public void done(PoetySentence poetySentence) {
                List<String> tempList = poetySentence.getData().getMatchTags();
                if (!tempList.isEmpty())
                    tag1.setText(tempList.get(0));
                if (tempList.size() > 2) {
                    tag2.setText(tempList.get(1));
                }
                sentence.setText(poetySentence.getData().getContent());

            }

            @Override
            public void error(JinrishiciRuntimeException e) {
                Log.w("TAG", "error: code = " + e.getCode() + " message = " + e.getMessage());
                //TODO do something else
            }
        });


        //将数据传递给测试页面
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getContext(), TestPoemActivity.class);
                try {
                    intent.putExtra("title", poetryObject.getJSONObject("data").get("name").toString());
                    intent.putExtra("dynasty", poetryObject.getJSONObject("data").get("dynasty").toString());
                    intent.putExtra("author", poetryObject.getJSONObject("data").get("author").toString());
                    intent.putExtra("content", poetryObject.getJSONObject("data").get("content").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        });

        //将标签数据传递给下一页面
        sureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getContext(), PoemListActivity.class);
                String tag = "";
                String dynasty = "";
                String toolBarTitle = "";
                if (selectedDynasty.size() > 0)
                    toolBarTitle += selectedDynasty.get(0) + ".";
                if (selectedType.size() > 0)
                    toolBarTitle += selectedType.get(0) + ".";
                if (selectedStyle.size() > 0)
                    toolBarTitle += selectedStyle.get(0);
                if (selectedType.size() == 0 && selectedStyle.size() == 0)
                    toolBarTitle = selectedDynasty.get(0);

                for (String temp : selectedType)
                    tag += temp + " ";
                for (String temp1 : selectedStyle)
                    tag += temp1 + " ";
                for (String temp2 : selectedDynasty)
                    dynasty += temp2;
                intent.putExtra("from", "filter");
                intent.putExtra("tag", tag);
                intent.putExtra("dynasty", dynasty);
                intent.putExtra("toolBarTitle", toolBarTitle);
                startActivity(intent);
            }
        });

        //搜索
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PoemListActivity.class);
                intent.putExtra("from", "search");
                intent.putExtra("toolBarTitle", searchText.getEditValue());
                intent.putExtra("key", searchText.getEditValue());
                startActivity(intent);

            }
        });

        //查看详情
        detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DetailActivity.class);
                String poemText = content.getText().toString();
                String sentence = spiltPoemTextByEnter(poemText)[0];
                intent.putExtra("content", sentence);
                intent.putExtra("toolbarTitle", title.getText().toString());
                startActivity(intent);

            }
        });

        //刷新
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getOnePoetry();
            }
        });
    }

    private void showSelected(List<String> temp) {
        if (temp.size() > 0) {
            for (String tempString : temp)
                System.out.println("selectedTag:" + tempString);
        } else
            System.out.println("没有选择");
    }


    private void getCollect()//获取用户收藏
    {
        YaFeng yaFeng = (YaFeng) getApplication();
        Boolean isLogin = yaFeng.isLogin();//登录状态标识
        int userId = 0;//用户标识
        if (isLogin)//判断是否登录
        {
            userId = yaFeng.getUserId();//获取用户标识
            HttpUtil.sendOkHttpRequest(
                    "http://39.106.193.194:8080/yafeng-1.0/user/getCollect?userId=" + userId,
                    new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONArray jsonArray = new JSONArray();
                        if (jsonObject.getJSONArray("data") == null) {
                            Log.d("getCollect:", "无数据");
                        } else
                            jsonArray = jsonObject.getJSONArray("data");
                        if (jsonArray.length() == 0)//没有收藏数据
                        {
                            Message message = new Message();
                            message.what = RESPONSE_FAIL;
                            handler.sendMessage(message);
                        } else { //成功获取收藏数据
                            collectObject = jsonArray;
                            Message message = new Message();
                            message.what = RESPONSE_SUCCESS;
                            handler.sendMessage(message);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        else {
            Toast.makeText(getContext(), "请先登录", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);//跳转至登录页
        }
    }

    private String[] spiltPoemTextByEnter(String poemText)//按换行分割字符串
    {
        String result[];
        result = poemText.split("\n");
        return result;
    }


}
