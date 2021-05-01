package com.experiment.yafeng.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.experiment.yafeng.Adapter.PoemListAdapter;
import com.experiment.yafeng.Constant.SysConstant;
import com.experiment.yafeng.Modal.Poetry;
import com.experiment.yafeng.R;
import com.experiment.yafeng.Util.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

import static com.xuexiang.xui.XUI.getContext;

public class PoemListActivity extends AppCompatActivity {

    private PoemListAdapter poemListAdapter;
    private ListView poemListView;
    private Toolbar toolbar;
    private TextView titleText;
    private String toolbarTitle;
    private final int COMPLETED = 1;
    private final int TO_WEBVIEW = 2;
    private RefreshLayout refreshLayout;
    private Integer size = 20;
    private Integer page = 0;
    private List<Poetry> poetryList = new ArrayList<>();
    private boolean isLoad = false;
    private JSONObject responseObject;
    private String from;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            if (message.what == COMPLETED) { //绑定数据
                try {
                    poemListAdapter = new PoemListAdapter(poetryList, getContext());
                    poemListView.setAdapter(poemListAdapter);
                    refreshLayout.finishRefresh();//结束刷新
                    refreshLayout.finishLoadmore(2000);//结束加载
                    isLoad = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (message.what == TO_WEBVIEW)//跳转至网站
            {
                Toast.makeText(getContext(), "暂时没有数据哦,去网上看看", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(PoemListActivity.this, WebViewActivity.class);
                startActivity(intent);
            }
        }

    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                isLoad = false;
                poetryList.clear();
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poem_list);
        from = getIntent().getStringExtra("from");

        toolbar = findViewById(R.id.titleBar);
        poemListView = findViewById(R.id.poemList);
        titleText = findViewById(R.id.titleText);
        setSupportActionBar(findViewById(R.id.titleBar));//设置toolbar
        refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setEnableScrollContentWhenLoaded(true);


        poemListAdapter = new PoemListAdapter(poetryList, getContext());
        poemListView.setAdapter(poemListAdapter);
//        poemListView.setEmptyView(findViewById(R.id.empty_imageview_iv));

        getSupportActionBar().setDisplayShowTitleEnabled(false);//屏蔽toolbar默认标题显示
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }//设置返回图标显示

        toolbarTitle = getIntent().getStringExtra("toolBarTitle");
        titleText.setText(toolbarTitle);
        refreshLayout.autoRefresh();

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                if (from.equals("collect")) {
                    String temp = getIntent().getStringExtra("collect");
                    Log.d("TEMPPPPPPPPP", temp);
                    Gson gson = new Gson();
                    List<Poetry> poetries = gson.fromJson(temp, new TypeToken<List<Poetry>>() {
                    }.getType());
                    poetryList = poetries;
                    poemListAdapter = new PoemListAdapter(poetryList, getContext());
                    poemListView.setAdapter(poemListAdapter);
                    refreshlayout.finishRefresh();
                } else if (from.equals("filter")) {
                    String tag = getIntent().getStringExtra("tag");
                    String dynasty = getIntent().getStringExtra("dynasty");
                    getPoemListByTag(tag, dynasty, page, size);
                } else if (from.equals("search")) {
                    String key = getIntent().getStringExtra("key");
                    getPoemListBySearch(key, page, size);
                } else if (from.equals("detail")) {
                    String tag = getIntent().getStringExtra("tag");
                    getPoemListByTag(tag, "null", page, size);
                }
            }
        });
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                if (from.equals("collect")) {
                    String temp = getIntent().getStringExtra("collect");
                    Log.d("TEMPPPPPPPPP", temp);
                    Gson gson = new Gson();
                    List<Poetry> poetries = gson.fromJson(temp, new TypeToken<List<Poetry>>() {
                    }.getType());
                    poetryList = poetries;
                    poemListAdapter = new PoemListAdapter(poetryList, getContext());
                    poemListView.setAdapter(poemListAdapter);
                    refreshlayout.finishRefresh();
                    refreshlayout.finishLoadmore(2000);
                } else if (from.equals("filter")) {
                    page++;
                    String tag = getIntent().getStringExtra("tag");
                    String dynasty = getIntent().getStringExtra("dynasty");
                    getPoemListByTag(tag, dynasty, page, size);
                } else if (from.equals("search")) {
                    page++;
                    String key = getIntent().getStringExtra("key");
                    getPoemListBySearch(key, page, size);
                } else if (from.equals("detail")) {
                    page++;
                    String tag = getIntent().getStringExtra("tag");
                    getPoemListByTag(tag, "null", page, size);
                }

            }
        });
        poemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PoemListActivity.this, DetailActivity.class);
                String poemText = poetryList.get(position).getContent();
                String sentence = spiltPoemTextByEnter(poemText)[0];
                intent.putExtra("content", sentence);
                intent.putExtra("toolbarTitle", poetryList.get(position).getName());
                startActivity(intent);
            }
        });


    }

    //根据标签获取诗词
    private void getPoemListByTag(String tag, String dynasty, Integer page, Integer size) {
        isLoad = false;
        String url = SysConstant.YA_FENG_SERVER+ "/yafeng-1.0/poetry/getPoetryByTag?tag=" + tag
                + "&dynasty=" + dynasty + "&page=" + page + "&size=" + size;
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

    //根据搜索关键字获取诗词
    private void getPoemListBySearch(String key, Integer page, Integer size) {
        String url = SysConstant.YA_FENG_SERVER+"/yafeng-1.0/poetry/getPoetryBySearch?key="
                + key + "&page=" + page + "&size=" + size;
        HttpUtil.sendOkHttpRequest(url, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                isLoad = false;
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                parseJSONWithGSON(responseData);
            }
        });
    }

    private void parseJSONWithGSON(String jsonData) {
        Gson gson = new Gson();
        JSONArray jsonArray = null;
        try {
            responseObject = new JSONObject(jsonData);
            if (responseObject.getJSONArray("data") != null)
                jsonArray = responseObject.getJSONArray("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        List<Poetry> tempList = new ArrayList<>();
        if (jsonArray != null)//数据获取成功
        {
            tempList = gson.fromJson(jsonArray.toString(), new TypeToken<List<Poetry>>() {
            }.getType());
            poetryList.addAll(tempList);
            Message message = new Message();
            message.what = COMPLETED;
            handler.sendMessage(message);
        } else//没有数据
        {
            Message message = new Message();
            message.what = TO_WEBVIEW;
            handler.sendMessage(message);
        }
    }

    private String[] spiltPoemTextByEnter(String poemText)//按换行分割字符串
    {
        String result[];
        result = poemText.split("\n");
        return result;
    }


}
