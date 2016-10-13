package com.park.letmesleep.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.park.letmesleep.beans.ParkInfo;
import com.park.letmesleep.beans.ParkStatus;
import com.park.letmesleep.vo.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr.G on 2016/9/15.
 */
public class MJsonUtil {

    public static Result getResult(String jsonString){

        JSONObject jo = JSON.parseObject(jsonString);
        Result r = new Result();
        r.setStatus(jo.getBoolean("status"));
        r.setStatusInfo(jo.getString("statusInfo"));
        r.setData(jo.getJSONArray("data"));

        return r;

    }
    public static Result<List<ParkInfo>> getParkInfoResult(String jsonString){
        List<ParkInfo> mList = new ArrayList<>();
        ParkInfo parkinfo = null;
        JSONObject jo = JSON.parseObject(jsonString);
        Result r = new Result();
        r.setStatus(jo.getBoolean("status"));
        r.setStatusInfo(jo.getString("statusInfo"));
        if(r.isStatus()){
            JSONArray jsonArray = jo.getJSONArray("data");
            for (int i = 0; i < jsonArray.size(); i++) {
                jo = jsonArray.getJSONObject(i);
                parkinfo = new ParkInfo();
                parkinfo.setParkName(jo.getString("name"));
                parkinfo.setParkPhone(jo.getString("telephone"));
                parkinfo.setParkId(jo.getString("parkid"));
                mList.add(parkinfo);
            }
        }
        r.setData(mList);
        return r;
    }
    public static Result<List<ParkStatus>> getParkStatus(String jsonString){
        ParkStatus parkStatus = null;
        List<ParkStatus> mList = new ArrayList<>();
        Result<List<ParkStatus>> r = new Result<>();
        JSONObject jo = JSON.parseObject(jsonString);
        r.setStatus(jo.getBoolean("status"));
        r.setStatusInfo(jo.getString("statusInfo"));
        if(r.isStatus()){
            JSONArray jsonArray = jo.getJSONArray("data");
            for (int i = 0; i < jsonArray.size(); i++) {
                jo = jsonArray.getJSONObject(i);
                parkStatus = new ParkStatus();
                parkStatus.setBlank(jo.getInteger("blank"));
                parkStatus.setOrdered(jo.getInteger("ordered"));
                parkStatus.setId(jo.getInteger("id"));
                mList.add(parkStatus);
            }
        }
        r.setData(mList);
        return r;
    }
    public static Result getOrderInfoResult(String jsonString){

        JSONObject jo = JSON.parseObject(jsonString);
        Result r = new Result();
        r.setStatus(jo.getBoolean("status"));
        r.setStatusInfo(jo.getString("statusInfo"));
        r.setParkName(jo.getString("parkName"));
        r.setLotId(jo.getInteger("lotId"));
        r.setPayNum(jo.getInteger("payNum"));
        r.setParkId(jo.getInteger("parkId"));
        r.setPhone(jo.getString("phone"));
        return r;

    }

}
