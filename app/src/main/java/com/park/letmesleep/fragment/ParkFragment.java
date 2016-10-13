package com.park.letmesleep.fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.park.letmesleep.R;
import com.park.letmesleep.beans.ParkInfo;
import com.park.letmesleep.config.Config;
import com.park.letmesleep.ui.ParkInfoDetilActivity;
import com.park.letmesleep.ui.adapter.FragmentParkAdapter;
import com.park.letmesleep.ui.refreshlayout.WaveSwipeRefreshLayout;
import com.park.letmesleep.util.MJsonUtil;
import com.park.letmesleep.vo.GetAllParkInfoVo;
import com.park.letmesleep.vo.Result;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ParkFragment extends Fragment implements WaveSwipeRefreshLayout.OnRefreshListener {
    private Context mContext;
    private List<ParkInfo> mList;
    @InjectView(R.id.fragment_park_listview)
    ListView fragmentParkListview;
    @InjectView(R.id.fragment_park_refreshlayout)
    WaveSwipeRefreshLayout fragmentParkRefreshlayout;

    public ParkFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_park, container, false);

        ButterKnife.inject(this, view);
        mContext = view.getContext();

        fragmentParkRefreshlayout.setOnRefreshListener(this);
        fragmentParkRefreshlayout.setWaveColor(Color.parseColor("#ff0099cc"));

        GetAllParkInfoVo gv = new GetAllParkInfoVo();
        gv.setAction("getParksInfo");

        new ParkInfoAsyncTask().execute(Config.URL, JSON.toJSONString(gv));


        fragmentParkListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext,ParkInfoDetilActivity.class);
                intent.putExtra("parkid",mList.get(position).getParkId());
                intent.putExtra("parkname",mList.get(position).getParkName());
                intent.putExtra("parkphone",mList.get(position).getParkPhone());
                startActivity(intent);
            }
        });


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onRefresh() {
        GetAllParkInfoVo gv = new GetAllParkInfoVo();
        gv.setAction("getParksInfo");
        new ParkInfoAsyncTask().execute(Config.URL, JSON.toJSONString(gv));
    }

    class ParkInfoAsyncTask extends AsyncTask<String, Void, List<ParkInfo>> {
        private String URLString;
        private String message;

        public ParkInfoAsyncTask() {

        }

        @Override
        protected List<ParkInfo> doInBackground(String... params) {
            List<ParkInfo> mDataList = null;
            URLString = params[0];
            message = params[1];
            String jsonString = httpPost(URLString,message);
            if(jsonString!=null){
                Result<List<ParkInfo>> mResult = MJsonUtil.getParkInfoResult(jsonString);
                if(mResult.isStatus())
                    mDataList = mResult.getData();
                else
                    mDataList = null;
            }
            return mDataList;
        }

        private String httpPost(String URLString,String message){
            String result = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(URLString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setConnectTimeout(8000);
                connection.setReadTimeout(8000);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                connection.connect();
                if(message!=null&&!message.equals("")) {
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                    bw.write(message);
                    bw.flush();
                    bw.close();
                }

                InputStream in = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                result = response.toString();

            } catch (Exception e) {
                result = null;
            } finally {
                if (connection != null)
                    connection.disconnect();

            }
            return result;
        }

        @Override
        protected void onPostExecute(List<ParkInfo> parkInfos) {
            if(parkInfos!=null){
                mList = parkInfos;
                FragmentParkAdapter adapter = new FragmentParkAdapter(mContext,parkInfos);
                fragmentParkListview.setAdapter(adapter);
                fragmentParkRefreshlayout.setRefreshing(false);}
            else {
                Toast.makeText(mContext, "刷新失败，请检查网络！", Toast.LENGTH_SHORT).show();
                fragmentParkRefreshlayout.setRefreshing(false);
            }
        }
    }
}