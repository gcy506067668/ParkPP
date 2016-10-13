package com.park.letmesleep.ui;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.park.letmesleep.R;
import com.park.letmesleep.fragment.CollectFragment;
import com.park.letmesleep.fragment.ControlFragment;
import com.park.letmesleep.fragment.ParkFragment;
import com.park.letmesleep.ui.menu.ResideMenu;
import com.park.letmesleep.ui.menu.ResideMenuItem;
import com.park.letmesleep.ui.widget.TitleBar;
import com.park.letmesleep.util.ActivityManager;
import com.park.letmesleep.util.MyApplication;
import com.park.letmesleep.util.PreferenceUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Mr.G on 2016/9/11.
 *
 * 程序主界面
 *
 */
public class MainActivity extends FragmentActivity{

    @InjectView(R.id.id_content)
    FrameLayout idContent;
    @InjectView(R.id.bottom_park_image)
    ImageButton bottomParkImage;
    @InjectView(R.id.bottom_park)
    LinearLayout bottomPark;
    @InjectView(R.id.bottom_control_image)
    ImageButton bottomControlImage;
    @InjectView(R.id.bottom_control)
    LinearLayout bottomControl;
    @InjectView(R.id.bottom_collect_image)
    ImageButton bottomCollectImage;
    @InjectView(R.id.bottom_collect)
    LinearLayout bottomCollect;
    @InjectView(R.id.activity_main_titlebar)
    TitleBar activityMainTitlebar;

    private ResideMenu resideMenu;

    private Fragment parkFragment;
    private Fragment controlFragment;
    private Fragment collectFragment;

    private ResideMenuItem itemExit;
    private ResideMenuItem itemProfile;
    private ResideMenuItem itemSettings;
    private ResideMenuItem itemTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {        //全屏显示，通知栏背景设置
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        }
        ActivityManager.addActivity(this);
        initView();                         /**初始化组件**/


    }

    private void initView() {
        resideMenu = new ResideMenu(this);
        resideMenu.setUse3D(true);
        resideMenu.attachToActivity(this);

        resideMenu.setScaleValue(0.6f);

        itemExit = new ResideMenuItem(this, R.drawable.menu_info,         "退出");
        itemProfile  = new ResideMenuItem(this, R.drawable.menu_home,     "主页");
        itemSettings = new ResideMenuItem(this, R.drawable.menu_settings, "设置");
        itemTime = new ResideMenuItem(this, R.drawable.menu_exit,         "时间");

        itemExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceUtil.save(MyApplication.getmApplicationContext(),"autologin",false);
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        itemProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "主页", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(MainActivity.this,ParkInfoDetilActivity.class);
//                intent.putExtra("parkid","11111111");
//                intent.putExtra("parkname","11111111");
//                intent.putExtra("parkphone","11111111");
//                startActivity(intent);
            }
        });

        itemSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"设置", Toast.LENGTH_SHORT).show();
            }
        });

        itemTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()), Toast.LENGTH_SHORT).show();
            }
        });

        resideMenu.addMenuItem(itemProfile, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemTime, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemSettings, ResideMenu.DIRECTION_RIGHT);
        resideMenu.addMenuItem(itemExit, ResideMenu.DIRECTION_RIGHT);


        activityMainTitlebar.initTitleBar("停 车 预 定");
        activityMainTitlebar.setLeftButtonImage(R.drawable.title_open_menu);
        activityMainTitlebar.setOnTitleBarClickListener(new TitleBar.OnTitleBarClickListener() {
            @Override
            public void onLeftButtonClick() {
                resideMenu.openMenu(0);
            }
        });
        setSecletion(1);
    }


    /****
     *
     * tabview选取时fragment的切换
     *
     * ****/
    private void setSecletion(int i) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction begin = fm.beginTransaction();
        hideFragment(begin);

        switch (i) {
            case 1:
                if (parkFragment == null) {
                    parkFragment = new ParkFragment();
                    begin.add(R.id.id_content, parkFragment);
                } else
                    begin.show(parkFragment);
                break;
            case 2:
                if (controlFragment == null) {
                    controlFragment = new ControlFragment();
                    begin.add(R.id.id_content, controlFragment);
                } else
                    begin.show(controlFragment);
                break;
            case 3:
                if (collectFragment == null) {
                    collectFragment = new CollectFragment();
                    begin.add(R.id.id_content, collectFragment);
                } else{
                    collectFragment.onResume();
                    begin.show(collectFragment);

                }
                break;

        }
        begin.commit();
    }

    private void hideFragment(FragmentTransaction begin) {
        if (parkFragment != null)
            begin.hide(parkFragment);
        if (controlFragment != null)
            begin.hide(controlFragment);
        if (collectFragment != null)
            begin.hide(collectFragment);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    @Override
    protected void onResume() {
            setSecletion(1);
            resetImages();
            bottomParkImage.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.park_fragment_selected));
            super.onResume();
    }

    @Override
    protected void onDestroy() {
        ActivityManager.removeActivity(this);
        super.onDestroy();
    }

    @OnClick({R.id.bottom_park, R.id.bottom_control, R.id.bottom_collect})
    public void onClick(View view) {
        resetImages();
        switch (view.getId()) {
            case R.id.bottom_park:
                setSecletion(1);
                bottomParkImage.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.park_fragment_selected));
                break;
            case R.id.bottom_control:
                setSecletion(2);
                bottomControlImage.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.button_fragment_selected));
                break;
            case R.id.bottom_collect:
                setSecletion(3);
                bottomCollectImage.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.collect_fragment_selected));
                break;
        }
    }

    private void resetImages() {
        bottomParkImage.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.park_fragment_unselected));
        bottomControlImage.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.button_fragment_unselected));
        bottomCollectImage.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.collect_fragment_unselected));
    }
}
