package com.park.letmesleep.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.dd.CircularProgressButton;
import com.park.letmesleep.R;
import com.park.letmesleep.config.Config;
import com.park.letmesleep.ui.widget.TitleBar;
import com.park.letmesleep.util.ActivityManager;
import com.park.letmesleep.util.BaseActivity;
import com.park.letmesleep.util.HttpUtil;
import com.park.letmesleep.util.MJsonUtil;
import com.park.letmesleep.util.MyApplication;
import com.park.letmesleep.util.PreferenceUtil;
import com.park.letmesleep.vo.LoginVo;
import com.park.letmesleep.vo.Result;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Mr.G on 2016/9/11.
 *
 * 登陆
 *
 */
public class LoginActivity extends BaseActivity {

    private final int ERROR_TOAST = -1;
    private final int UPDATE_UI = 1;
    private int UPDATE_UI_INFO;
    private String MSG = "";



    @InjectView(R.id.login_user_name)
    EditText loginUserName;
    @InjectView(R.id.login_user_password)
    EditText loginUserPassword;
    @InjectView(R.id.circularButton1)
    CircularProgressButton circularButton1;
    @InjectView(R.id.login_user_reg)
    TextView loginUserReg;
    @InjectView(R.id.activity_login_title)
    TitleBar activityLoginTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){        //全屏显示，通知栏背景设置
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        ActivityManager.addActivity(this);
        ButterKnife.inject(this);

        circularButton1.setIndeterminateProgressMode(true);
        activityLoginTitle.initTitleBar("用 户 登 陆");
        activityLoginTitle.setOnTitleBarClickListener(new TitleBar.OnTitleBarClickListener() {
            @Override
            public void onLeftButtonClick() {
                finish();
            }
        });

        if(PreferenceUtil.load(MyApplication.getmApplicationContext(),"autologin",false)&&
                !PreferenceUtil.load(MyApplication.getmApplicationContext(),"username","").equals("")&&
                !PreferenceUtil.load(MyApplication.getmApplicationContext(),"password","").equals("")){
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    @OnClick({R.id.circularButton1, R.id.login_user_reg})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.circularButton1:
                if(circularButton1.getProgress()==0||circularButton1.getProgress()==100||circularButton1.getProgress()==-1){
                    circularButton1.setProgress(50);

                LoginVo lv = new LoginVo();
                lv.setAction("login");
                lv.setTelephone(loginUserName.getText().toString().trim());
                lv.setPassword(loginUserPassword.getText().toString());

                /***发送请求  回调***/
                HttpUtil.sendHttpRequest(Config.URL, JSON.toJSONString(lv), new HttpUtil.HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        Result r = MJsonUtil.getResult(response);
                        if(r.isStatus()){
                            Message msg = new Message();
                            msg.what = UPDATE_UI_INFO;
                            UPDATE_UI_INFO = 0;
                            mHandler.sendMessage(msg);

                            PreferenceUtil.save(MyApplication.getmApplicationContext(),"username",loginUserName.getText().toString().trim());
                            PreferenceUtil.save(MyApplication.getmApplicationContext(),"password",loginUserPassword.getText().toString().trim());
                            PreferenceUtil.save(MyApplication.getmApplicationContext(),"autologin",true);
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Message msg = new Message();
                            msg.what=ERROR_TOAST;
                            MSG = r.getStatusInfo();
                            mHandler.sendMessage(msg);


                            Message msg1 = new Message();
                            msg1.what=UPDATE_UI;
                            UPDATE_UI_INFO = 0;
                            mHandler.sendMessage(msg1);

                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Message msg = new Message();
                        msg.what = UPDATE_UI;
                        UPDATE_UI_INFO = 0;
                        mHandler.sendMessage(msg);

                        Message msg1 = new Message();
                        msg1.what = ERROR_TOAST;
                        MSG = "网络连接失败！";
                        mHandler.sendMessage(msg1);


                       }
                });}
//                /***test***/
//
//                PreferenceUtil.save(MyApplication.getmApplicationContext(),"username","18920232586".trim());
//                PreferenceUtil.save(MyApplication.getmApplicationContext(),"password","admin".trim());
//                PreferenceUtil.save(MyApplication.getmApplicationContext(),"autologin",true);
//                Intent intent1 = new Intent(LoginActivity.this,MainActivity.class);/**临时段*/
//                startActivity(intent1);
//                /***test***/
//                finish();

                break;
            case R.id.login_user_reg:       /****账号注册*****/
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent,1);
                break;
        }
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case ERROR_TOAST:

                    Toast.makeText(LoginActivity.this, MSG, Toast.LENGTH_SHORT).show();
                    break;
                case UPDATE_UI:
                    circularButton1.setProgress(UPDATE_UI_INFO);
                    break;
            }
        }
    };



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1&&resultCode==1){
            loginUserName.setText(data.getStringExtra("telephone"));
            loginUserPassword.setText(data.getStringExtra("password"));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.removeActivity(this);
    }
}
