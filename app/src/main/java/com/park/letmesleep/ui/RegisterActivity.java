package com.park.letmesleep.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
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
import com.park.letmesleep.vo.RegisterVo;
import com.park.letmesleep.vo.Result;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Mr.G on 2016/9/11.
 */
public class RegisterActivity extends BaseActivity {


    private final int TOAST = -1;
    @InjectView(R.id.activity_register_titlebar)
    TitleBar activityRegisterTitlebar;
    @InjectView(R.id.activity_register_telephone)
    EditText activityRegisterTelephone;
    @InjectView(R.id.activity_register_msm)
    CircularProgressButton activityRegisterMsm;
    @InjectView(R.id.activity_register_password)
    EditText activityRegisterPassword;
    @InjectView(R.id.activity_register_username)
    EditText activityRegisterUsername;
    @InjectView(R.id.activity_register_palte)
    EditText activityRegisterPalte;
    @InjectView(R.id.activity_register_msminfo)
    EditText activityRegisterMsminfo;
    @InjectView(R.id.activity_register_reg)
    CircularProgressButton activityRegisterReg;
    private String MSG = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {        //全屏显示，通知栏背景设置
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        ButterKnife.inject(this);
        ActivityManager.addActivity(this);
        activityRegisterMsm.setIndeterminateProgressMode(true);
        activityRegisterReg.setIndeterminateProgressMode(true);

        activityRegisterTitlebar.initTitleBar("注 册 账 户");
        activityRegisterTitlebar.setOnTitleBarClickListener(new TitleBar.OnTitleBarClickListener() {
            @Override
            public void onLeftButtonClick() {
                Intent intent = new Intent();
                setResult(0, intent);
                finish();
            }
        });

    }

    @Override
    protected void onDestroy() {
        ActivityManager.removeActivity(this);
        super.onDestroy();

    }


    @OnClick({R.id.activity_register_msm, R.id.activity_register_reg})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_register_msm:
                if (activityRegisterMsm.getProgress() == 0)
                    activityRegisterMsm.setProgress(50);
                /**向号码发送短信***/


                if (activityRegisterMsm.getProgress() == -1 || activityRegisterMsm.getProgress() == 100)
                    Toast.makeText(RegisterActivity.this, "短信已发送！", Toast.LENGTH_SHORT).show();
                break;
            case R.id.activity_register_reg:


                /***短信验证**/

                activityRegisterReg.setProgress(50);
                /*****短信验证成功之后向服务器注册账户等待回调****/
                RegisterVo rv = new RegisterVo();
                rv.setTelephone(activityRegisterTelephone.getText().toString().trim());
                rv.setAction("reg");
                rv.setPalte(activityRegisterPalte.getText().toString());
                rv.setPassword(activityRegisterPassword.getText().toString());
                rv.setUsername(activityRegisterUsername.getText().toString());


                HttpUtil.sendHttpRequest(Config.URL, JSON.toJSONString(rv), new HttpUtil.HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        Result r = MJsonUtil.getResult(response);
                        if (r.isStatus()) {
                            Message msg = new Message();
                            msg.what = TOAST;
                            MSG = r.getStatusInfo();
                            mHandler.sendMessage(msg);

                            Intent intent = new Intent();
                            intent.putExtra("telephone", activityRegisterTelephone.getText().toString());
                            intent.putExtra("password", activityRegisterPassword.getText().toString());
                            setResult(1, intent);
                            finish();
                        } else {

                            Message msg = new Message();
                            msg.what = TOAST;
                            MSG = r.getStatusInfo();
                            mHandler.sendMessage(msg);

                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Message msg = new Message();
                        msg.what = TOAST;
                        MSG = "网络连接失败！";
                        mHandler.sendMessage(msg);
                    }
                });


                break;
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TOAST:
                    activityRegisterReg.setProgress(0);
                    Toast.makeText(RegisterActivity.this, MSG, Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };
}
