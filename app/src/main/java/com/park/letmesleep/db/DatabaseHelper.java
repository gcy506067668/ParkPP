package com.park.letmesleep.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 创建便签的数据库
 * 
 * @author kymjs
 * 
 *         update:2014-01-12 updateor: fireant 内容：修改为全应用数据库
 * 
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String PARK_DATABASE_NAME = "collectparkinfo";

    public static final String PARK_TABLE_NAME = "collectparkinfo";

    public static final String CREATE_PARK_TABLE = "create table "
            + PARK_TABLE_NAME + " ("
            + "parkid text, "
            + "parkname text, "
            + "parkphone text)";




    public DatabaseHelper(Context context) {
        super(context, PARK_DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PARK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

}