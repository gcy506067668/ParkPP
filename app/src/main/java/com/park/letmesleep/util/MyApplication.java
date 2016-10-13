package com.park.letmesleep.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr.G on 2016/9/11.
 */
public class MyApplication extends Application {

    private static Context mContext;
    private static List<Activity> activityList;
    @Override
    public void onCreate() {

        super.onCreate();
        mContext = this;
        activityList = new ArrayList<>();
    }
    public static Context getmApplicationContext(){
        return mContext;
    }

    public static List<Activity> getActivityList(){
        return activityList;
    }
}
