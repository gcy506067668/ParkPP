package com.park.letmesleep.util;

import android.app.Activity;

import java.util.List;

/**
 * Created by Mr.G on 2016/9/11.
 */
public class ActivityManager {

    private static List<Activity> activityList = MyApplication.getActivityList();

    public static void addActivity(Activity activity){
        activityList.add(activity);
    }

    public static void finishAllActivity(){
        for(Activity activity : activityList){
            if(activity!=null)
                activity.finish();
        }
    }
    public static void removeActivity(Activity activity){
        activityList.remove(activity);
    }


}
