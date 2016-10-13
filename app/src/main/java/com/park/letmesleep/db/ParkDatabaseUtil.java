package com.park.letmesleep.db;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.park.letmesleep.beans.ParkInfo;

import java.util.ArrayList;
import java.util.List;

public class ParkDatabaseUtil {
    private final DatabaseHelper dbHelper;

    public ParkDatabaseUtil(Context context) {
        super();
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * 增
     * 
     * @param data
     */
    public void insert(ParkInfo data) {
        String sql = "insert into " + DatabaseHelper.PARK_TABLE_NAME;

        sql += "(parkid, parkname, parkphone) values(?, ?, ?)";

        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        sqlite.execSQL(sql, new String[] { data.getParkId(),data.getParkName(),data.getParkPhone()});
        sqlite.close();
    }

    /**
     * 删
     * 
     * @param id
     */
    public void delete(String id) {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("delete from " + DatabaseHelper.PARK_TABLE_NAME + " where parkid=?");
        sqlite.execSQL(sql, new String[] { id });
        sqlite.close();
    }

    /**
     * 改
     * 
     * @param data
     */
    public void update(ParkInfo data) {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("update " + DatabaseHelper.PARK_TABLE_NAME + " set parkid=?, parkname=?, parkphone=? where parkid=?");
        sqlite.execSQL(sql, new String[] { data.getParkId(),data.getParkName(),data.getParkPhone(),data.getParkId()});
        sqlite.close();
    }


    /**
     * 查
     * 
     * @param where
     * @return
     */
    public List<ParkInfo> query(String where) {
        SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
        ArrayList<ParkInfo> data = null;
        data = new ArrayList<ParkInfo>();
        Cursor cursor = sqlite.rawQuery("select * from "
                + DatabaseHelper.PARK_TABLE_NAME + where, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            ParkInfo parkinfo = new ParkInfo();
            parkinfo.setParkId(cursor.getString(0));
            parkinfo.setParkName(cursor.getString(1));
            parkinfo.setParkPhone(cursor.getString(2));
            data.add(parkinfo);
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        sqlite.close();

        return data;
    }

    /**
     *
     * 获取所有数据
     *
     * ***/
    public List<ParkInfo> getAllParkInfo(){
        List<ParkInfo> parkInfos = new ArrayList<>();
        parkInfos = query(" ");
        return parkInfos;
    }

    /**
     * 重置
     * 
     * @param datas
     */
    public void reset(List<ParkInfo> datas) {
        if (datas != null) {
            SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
            // 删除全部
            sqlite.execSQL("delete from " + DatabaseHelper.PARK_TABLE_NAME);
            // 重新添加
            for (ParkInfo data : datas) {
                insert(data);
            }
            sqlite.close();
        }
    }

    /**
     * 保存一条数据到本地(若已存在则直接覆盖)
     * 
     * @param data
     */
    public void save(ParkInfo data) {
        List<ParkInfo> datas = query(" where parkid=" + data.getParkId());
        if (datas != null && !datas.isEmpty()) {
            update(data);
        } else {
            insert(data);
        }
    }

    public void destroy() {
        dbHelper.close();
    }
}