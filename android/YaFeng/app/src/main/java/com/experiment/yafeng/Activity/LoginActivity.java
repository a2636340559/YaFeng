package com.experiment.yafeng.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.experiment.yafeng.R;
import com.experiment.yafeng.Util.HttpUtil;
import com.experiment.yafeng.YaFeng;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.widget.edittext.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private Button login_button;
    private MaterialEditText account_input;
    private MaterialEditText password_input;
    private final int COMPLETED = 1;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private CheckBox rememberPass;
    private JSONObject responseObject=null;
    private String account="";
    private String password="";
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            if (message.what == COMPLETED) {
                String result= null;
                int userId=0;
                try {
                    result = responseObject.getString("msg");
                    userId=responseObject.getInt("data");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(result.equals("register"))
                    Toast.makeText(LoginActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                else if(result.equals("login"))
                    Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                if(result.equals("密码错误"))
                    Toast.makeText(LoginActivity.this,"密码错误",Toast.LENGTH_SHORT).show();
                else {
                    editor = pref.edit();
                    if (rememberPass.isChecked()) {
                        editor.putBoolean("remember_password", true);
                        editor.putString("account", account);
                        editor.putString("password", password);
                    } else
                        editor.clear();
                    editor.apply();
//                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    YaFeng yaFeng=(YaFeng)getApplication();
                    yaFeng.setLogin(true);
                    yaFeng.setUserId(userId);
//                    startActivity(intent);
                    finish();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        XUI.initTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login_button = findViewById(R.id.login_button);
        account_input = findViewById(R.id.account);
        password_input = findViewById(R.id.password);
        rememberPass=findViewById(R.id.remember);
        pref= PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRemember =pref.getBoolean("remember_password",false);
        if(isRemember)
        {
            account_input.setText(pref.getString("account",""));
            password_input.setText(pref.getString("password",""));
            rememberPass.setChecked(true);
        }
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account=account_input.getEditValue();
                password=password_input.getEditValue();
                login(account,password);
            }
        });
    }

    private void login(String account,String password)
    {
        HttpUtil.sendOkHttpRequest("http://39.106.193.194:8080/yafeng-1.0/user/login?account="+account+"&password="+password, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                try {
                    JSONObject jsonObject=new JSONObject(responseData);
                    if(jsonObject!=null)
                         Log.d("LOGINIIIIIIIIIIIIIIII",jsonObject.toString());
                    responseObject=jsonObject;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Message message = new Message();
                message.what = COMPLETED;
                handler.sendMessage(message);
            }
        });
    }
}
