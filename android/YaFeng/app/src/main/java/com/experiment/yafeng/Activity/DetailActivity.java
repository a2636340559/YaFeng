package com.experiment.yafeng.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.experiment.yafeng.Modal.PoetryDetail;
import com.experiment.yafeng.R;
import com.experiment.yafeng.Util.HttpUtil;
import com.experiment.yafeng.YaFeng;
import com.google.gson.Gson;
import com.xuexiang.xui.XUI;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

import static com.xuexiang.xui.XUI.getContext;

public class DetailActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView titleText;
    private String toolbarTitle;
    private TextView annotation;
    private TextView translation;
    private TextView reference;
    private TextView appreciation;
    private LinearLayout linearLayout;
    private Button  test_button;
    private TextView menu_line;
    private TextView poem_title;
    private TextView poem_author;
    private TextView poem_content;
    private TagFlowLayout tag_flow;
    private ScrollView scrollView;
    private ImageView author_img;
    private ImageView icon_collect;
    private TextView author_content;
    private Boolean isLoad=false;
    private JSONObject responseObject;
    private PoetryDetail poetryDetail;
    private final int COMPLETED = 1;
    private final int COLLECT = 2;
    private final int STORAGE=3;
    private final int TO_WEBVIEW=4;
    private List<String> tags;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            if (message.what == COMPLETED) {
                try {
                    poem_title.setText(poetryDetail.getName());
                    String author=poetryDetail.getDynasty()+"."+poetryDetail.getAuthor();
                    poem_author.setText(author);
                    poem_content.setText(poetryDetail.getContent());
                    Bitmap bitmap=getURLImage(poetryDetail.getImg());
                    author_img.setImageBitmap(bitmap);
                    author_content.setText(poetryDetail.getAuthorDetail());
                    reference.setText(poetryDetail.getReference());
                    annotation.setText(poetryDetail.getAnnotation());
                    appreciation.setText(poetryDetail.getAppreciation());
                    translation.setText(poetryDetail.getTranslation());
                    tags=spiltBySpace(poetryDetail.getTag());
                    initTagFlowLayout();
                    isLoad = true;
                    isStoraged(poetryDetail.getName(),poetryDetail.getAuthor());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(message.what==COLLECT)//收藏成功
            {
                icon_collect.setImageResource(R.drawable.ic_collect);
                Toast.makeText(getContext(),"收藏成功",Toast.LENGTH_LONG).show();
            }
            if(message.what==STORAGE)//设置收藏图标
            {
                icon_collect.setImageResource(R.drawable.ic_collect);
            }
            if(message.what==TO_WEBVIEW)//打开古诗文网网页
            {
                Toast.makeText(getContext(),"暂时没有数据哦,去网上看看",Toast.LENGTH_LONG).show();
                Intent intent=new Intent(DetailActivity.this, WebViewActivity.class);
                startActivity(intent);
                finish();
            }
        }

    };

    @Override
    public  boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                isLoad=false;
                tags=null;
                poetryDetail=null;
                responseObject=null;
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        XUI.initTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }//在主线程访问网络连接
        initView();
        setSupportActionBar(toolbar);//设置toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);//屏蔽toolbar默认标题显示
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }//设置返回图标显示

        toolbarTitle=getIntent().getStringExtra("toolbarTitle");
        String content=getIntent().getStringExtra("content");
        Log.d("toolBarTitle---------",toolbarTitle);
        titleText.setText(toolbarTitle);

        icon_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLogin();
            }
        }); //弹出子菜单

        getPoetryDetailByContent(content);

        tag_flow.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {

                Intent intent=new Intent(getContext(),PoemListActivity.class);
                intent.putExtra("from","detail");
                intent.putExtra("tag",tags.get(position));
                intent.putExtra("toolBarTitle",tags.get(position));
                startActivity(intent);
                return true;
            }
        });

        //将数据传递给测试页面
       test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getContext(), TestPoemActivity.class);
                try {
                    intent.putExtra("title",poetryDetail.getName());
                    intent.putExtra("dynasty",poetryDetail.getDynasty());
                    intent.putExtra("author",poetryDetail.getAuthor());
                    intent.putExtra("content",poetryDetail.getContent());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        });

    }


    private void initView()
    {
        toolbar=findViewById(R.id.poem_titleBar);
        titleText=findViewById(R.id.poem_titleText);
        menu_line=findViewById(R.id.menu_line);
        annotation=findViewById(R.id.annotation_text);
        translation=findViewById(R.id.translation_text);
        appreciation=findViewById(R.id.annotation_text);
        reference=findViewById(R.id.reference_text);
        test_button=findViewById(R.id.poem_test_button);
        poem_title=findViewById(R.id.detail_poem_title);
        poem_author=findViewById(R.id.detail_poem_author);
        poem_content=findViewById(R.id.detail_poem_content);
        tag_flow=findViewById(R.id.tag_flow);
        author_img=findViewById(R.id.author_img);
        author_content=findViewById(R.id.author_detail);
        scrollView=findViewById(R.id.detail_scroll);
        linearLayout=findViewById(R.id.poem_linnear);
        icon_collect=findViewById(R.id.poem_menu_icon);

    }

    private void initTagFlowLayout()
    {
        tag_flow=findViewById(R.id.tag_flow);
        final LayoutInflater mInflater =getLayoutInflater();

        tag_flow.setAdapter(new TagAdapter<String>(tags) {

            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) mInflater.inflate(R.layout.adapter_item_tag,
                        tag_flow, false);
                tv.setText(s);
                return tv;
            }
        });


    }

    //根据首句获取诗词详情
    public void getPoetryDetailByContent(String content)
    {
        String url="http://39.106.193.194:8080/yafeng-1.0/poetry/getPoetryDetailBy?content="+content;
        HttpUtil.sendOkHttpRequest(url, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                parseJSONWithGSON(responseData);
            }
        });
    }

    //解析返回数据
    private void parseJSONWithGSON(String jsonData)
    {
        Gson gson=new Gson();
        JSONObject jsonObject=null;
        try {
            responseObject=new JSONObject(jsonData);
            if(responseObject.getJSONObject("data")!=null) {
                jsonObject = responseObject.getJSONObject("data");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PoetryDetail detail=null;
        if(jsonObject!=null) {//成功获取数据
            detail = gson.fromJson(jsonObject.toString(), PoetryDetail.class);
            poetryDetail = detail;
            Message message = new Message();
            message.what = COMPLETED;
            handler.sendMessage(message);
        }
        else//没有数据
        {
            Message message = new Message();
            message.what = TO_WEBVIEW;
            handler.sendMessage(message);
        }
    }

    //加载图片
    public static Bitmap getURLImage(String url) {
        Bitmap bmp = null;
        try {
            URL myurl = new URL(url);
            // 获得连接
            HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
            conn.setConnectTimeout(6000);//设置超时
            conn.setDoInput(true);
            conn.setUseCaches(false);//不缓存
            conn.connect();
            InputStream is = conn.getInputStream();//获得图片的数据流
            bmp = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }

    //按空格划分字符串
    public List<String> spiltBySpace(String tag)
    {
        String[] temp=tag.split(" ");
        List<String> tags=new ArrayList<>();
        for(int i=0;i<temp.length;i++)
            if(i<3)
                tags.add(temp[i]);
            else
                break;

        for(int i=0;i<tags.size();i++)
            System.out.println("SSSSSSSSSSSSSSSSSSS----------------:"+tags.get(i));
        return tags;

    }

    //检查登录状态
    public void checkLogin ()
    {
        YaFeng yaFeng=(YaFeng)getApplication();
        boolean isLogin=yaFeng.isLogin();
        Integer userId=yaFeng.getUserId();
        if(isLogin)
        {
            String poetryName=null;
            poetryName=poem_title.getText().toString();
            String author=poetryDetail.getAuthor();
            collect(userId,poetryName,author);
        }
        else
        {
            Toast.makeText(getContext(),"请先登录",Toast.LENGTH_LONG);
            Intent intent=new Intent(DetailActivity.this,LoginActivity.class);
            startActivity(intent);
        }
    }


    //收藏
    private void collect(Integer userId,String poetryName,String author)
    {
        HttpUtil.sendOkHttpRequest("http://39.106.193.194:8080/yafeng-1.0/user/collect?" +
                "userId="+userId+"&poetryName="+poetryName+"&author="+author,
                new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                int temp=0;
                try {
                    JSONObject jsonObject=new JSONObject(responseData);
                     temp=jsonObject.getInt("data");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(temp==1)
                {
                    Message message=new Message();
                    message.what=COLLECT;
                    handler.sendMessage(message);
                }
            }
        });
    }

    //判断是否为收藏的诗词
    private void isStoraged(String poetryName,String author)
    {
        YaFeng yaFeng=(YaFeng)getApplication();
        boolean isLogin=yaFeng.isLogin();
        int userId=yaFeng.getUserId();
        if(!isLogin)//未登录时
            icon_collect.setImageResource(R.drawable.icon_collect1);
        else
        {
            HttpUtil.sendOkHttpRequest(
                    "http://39.106.193.194:8080/yafeng-1.0/user/isStoraged?userId="
                            +userId+"&poetryName="+poetryName+"&author="+author,
                    new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseData = response.body().string();
                   boolean temp=false;
                    try {
                        JSONObject jsonObject=new JSONObject(responseData);
                        temp=jsonObject.getBoolean("data");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(temp)
                    {
                        Message message=new Message();
                        message.what=STORAGE;
                        handler.sendMessage(message);
                    }
                }
            });
        }
    }


}
