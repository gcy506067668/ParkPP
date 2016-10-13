package com.park.letmesleep.fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.dd.CircularProgressButton;
import com.park.letmesleep.R;
import com.park.letmesleep.config.Config;
import com.park.letmesleep.ui.dialog.SweetAlertDialog;
import com.park.letmesleep.ui.refreshlayout.WaveSwipeRefreshLayout;
import com.park.letmesleep.util.HttpUtil;
import com.park.letmesleep.util.MJsonUtil;
import com.park.letmesleep.util.MyApplication;
import com.park.letmesleep.util.PreferenceUtil;
import com.park.letmesleep.vo.CommonVo;
import com.park.letmesleep.vo.EndParkVo;
import com.park.letmesleep.vo.GetLotInfoVo;
import com.park.letmesleep.vo.GetOrderInfoVo;
import com.park.letmesleep.vo.LockRelayVo;
import com.park.letmesleep.vo.Result;
import com.park.letmesleep.vo.UnlockRelayVo;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.park.letmesleep.R.id.fragment_control_finishall;

/**
 * A simple {@link Fragment} subclass.
 */
public class ControlFragment extends Fragment implements WaveSwipeRefreshLayout.OnRefreshListener {


    @InjectView(R.id.fragment_collect_statuetext)
    TextView fragmentCollectStatuetext;
    @InjectView(R.id.fragment_control_parkname)
    TextView fragmentControlParkname;
    @InjectView(R.id.fragment_control_parkspace)
    TextView fragmentControlParkspace;
    @InjectView(R.id.fragment_control_parkphone)
    TextView fragmentControlParkphone;
    @InjectView(R.id.fragment_control_parksatue_layout)
    LinearLayout fragmentControlParksatueLayout;
    @InjectView(R.id.fragment_control_mainrelay)
    CircularProgressButton fragmentControlMainrelay;
    @InjectView(R.id.fragment_control_calladmin)
    CircularProgressButton fragmentControlCalladmin;
    @InjectView(R.id.fragment_control_openlock)
    CircularProgressButton fragmentControlOpenlock;
    @InjectView(R.id.fragment_control_lock)
    CircularProgressButton fragmentControlLock;
    @InjectView(fragment_control_finishall)
    CircularProgressButton fragmentControlFinishall;
    @InjectView(R.id.fragment_control_refreshlayout)
    WaveSwipeRefreshLayout fragmentControlRefreshlayout;

    private String MESSAGE = "";
    private boolean isResponse = false;
    private CircularProgressButton cpb = null;
    private Context mContext;
    private String parkPhone = "下拉刷新";
    private String parkId = "111";
    private String parkName = "asd";
    private String spaceId = "111";

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1) {
                Toast.makeText(MyApplication.getmApplicationContext(), MESSAGE, Toast.LENGTH_SHORT).show();
                if(isResponse)
                    cpb.setProgress(100);
                else
                    cpb.setProgress(-1);

            }
        }
    };


    public ControlFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_control, container, false);
        ButterKnife.inject(this, view);
        mContext = view.getContext();
        initView();
        refresh();
        return view;
    }

    private void initView() {
        fragmentControlRefreshlayout.setOnRefreshListener(this);
        fragmentControlRefreshlayout.setWaveColor(Color.parseColor("#ff0099cc"));
        fragmentControlMainrelay.setIndeterminateProgressMode(true);
        fragmentControlCalladmin.setIndeterminateProgressMode(true);
        fragmentControlOpenlock.setIndeterminateProgressMode(true);
        fragmentControlLock.setIndeterminateProgressMode(true);
        fragmentControlFinishall.setIndeterminateProgressMode(true);

    }

    public void getResponse(Object jsonData){

        HttpUtil.sendHttpRequest(Config.URL, JSON.toJSONString(jsonData), new HttpUtil.HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Result r = MJsonUtil.getResult(response);
                if(r.isStatus()){
                    Message msg = new Message();
                    msg.what=1;
                    handler.sendMessage(msg);
                    isResponse = true;
                }else{
                    Message msg = new Message();
                    msg.what=1;
                    MESSAGE = r.getStatusInfo();
                    handler.sendMessage(msg);
                    isResponse = false;
                }
            }

            @Override
            public void onError(Exception e) {
                Message msg = new Message();
                msg.what=1;
                MESSAGE = "网络连接失败！";
                handler.sendMessage(msg);
                isResponse = false;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick({R.id.fragment_control_mainrelay, R.id.fragment_control_calladmin, R.id.fragment_control_openlock, R.id.fragment_control_lock, fragment_control_finishall})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragment_control_mainrelay:        //打开道闸栏杆
                if(fragmentControlOpenlock.getProgress()==50||fragmentControlOpenlock.getProgress()==100)
                    return;
                fragmentControlMainrelay.setProgress(50);
                cpb = fragmentControlMainrelay;
                GetLotInfoVo cv = new GetLotInfoVo();
                MESSAGE = "道闸栏杆已打开！";
                cv.setAction("openMainRelay");
                cv.setParkId(Integer.parseInt(parkId));
                getResponse(cv);
                break;
            case R.id.fragment_control_calladmin:               //联系停车场管理员
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+parkPhone));
                startActivity(intent);
                break;
            case R.id.fragment_control_openlock:                //解锁车位
                if(fragmentControlOpenlock.getProgress()==50||fragmentControlOpenlock.getProgress()==100)
                    return ;
                cpb = fragmentControlOpenlock;
                fragmentControlOpenlock.setProgress(50);
                UnlockRelayVo urv = new UnlockRelayVo();
                urv.setAction("unlockRelay");
                urv.setParkId(Integer.parseInt(parkId));
                urv.setSpaceId(Integer.parseInt(spaceId));
                getResponse(urv);
                break;
            case R.id.fragment_control_lock:                    //锁定车位
                if(fragmentControlLock.getProgress()==50||fragmentControlLock.getProgress()==100)
                    return ;
                cpb = fragmentControlLock;
                fragmentControlLock.setProgress(50);
                LockRelayVo lrv = new LockRelayVo();
                lrv.setAction("lockRelay");
                lrv.setParkId(Integer.parseInt(parkId));
                lrv.setSpaceId(Integer.parseInt(spaceId));
                getResponse(lrv);
                break;
            case fragment_control_finishall:                       //完成支付

                if(fragmentControlFinishall.getProgress()==50||fragmentControlFinishall.getProgress()==100)
                    return ;
                fragmentControlFinishall.setProgress(50);
                EndParkVo epv = new EndParkVo();
                epv.setAction("endPark");
                epv.setTelephone(PreferenceUtil.load(MyApplication.getmApplicationContext(),"username","1111111111"));
                epv.setParkId(Integer.parseInt(parkId));
                epv.setSpaceId(Integer.parseInt(spaceId));
                HttpUtil.sendHttpRequest(Config.URL, JSON.toJSONString(epv), new HttpUtil.HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        final Result r = MJsonUtil.getResult(response);
                        if(r.isStatus())
                            handler.post(new Runnable() {
                            @Override
                            public void run() {
                                new SweetAlertDialog(mContext,SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("支付")
                                        .setContentText("您共需支付费用 "+r.getPayNum()+" 元。")
                                        .setConfirmText("线上支付")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.dismiss();
                                                CommonVo cv = new CommonVo();
                                                cv.setAction("payMoney");
                                                HttpUtil.sendHttpRequest(Config.URL, JSON.toJSONString(cv), new HttpUtil.HttpCallbackListener() {
                                                    @Override
                                                    public void onFinish(String response) {
                                                        final Result r = MJsonUtil.getResult(response);
                                                        if(r.isStatus())
                                                        handler.post(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                fragmentControlFinishall.setProgress(0);
                                                                new SweetAlertDialog(mContext,SweetAlertDialog.SUCCESS_TYPE)
                                                                        .setTitleText("支付")
                                                                        .setContentText("支付完成！")
                                                                        .setConfirmText("确定")
                                                                        .show();
                                                                refresh();
                                                            }
                                                        });
                                                        else{handler.post(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                refresh();
                                                                fragmentControlFinishall.setProgress(0);
                                                                Toast.makeText(MyApplication.getmApplicationContext(), r.getStatusInfo(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                        }
                                                    }

                                                    @Override
                                                    public void onError(Exception e) {
                                                        refresh();
                                                    }
                                                });
                                            }
                                        })
                                        .showCancelButton(true)
                                        .setCancelText("线下支付")
                                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.dismiss();
                                                handler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        fragmentControlFinishall.setProgress(0);
                                                        Toast.makeText(mContext, "线下支付", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        })
                                        .show();
                            }
                        });
                        else{
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    refresh();
                                    fragmentControlFinishall.setProgress(0);
                                    Toast.makeText(mContext, r.getStatusInfo(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                fragmentControlFinishall.setProgress(0);
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        fragmentControlFinishall.setProgress(0);
                                        Toast.makeText(mContext, "网络连接失败，请线下支付车费！", Toast.LENGTH_SHORT).show();
//                                    /****************************************tttttttttttttttttttt*******************************************/
//                                        new SweetAlertDialog(mContext,SweetAlertDialog.SUCCESS_TYPE)
//                                                .setTitleText("支付")
//                                                .setContentText("您共需支付费用 100 元。")
//                                                .setConfirmText("线上支付")
//                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                                    @Override
//                                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
//
//                                                        sweetAlertDialog.dismiss();
//                                                        new SweetAlertDialog(mContext,SweetAlertDialog.SUCCESS_TYPE)
//                                                                .setTitleText("支付")
//                                                                .setContentText("支付完成！")
//                                                                .setConfirmText("确定")
//                                                                .show();
//                                                        refresh();
//                                                    }
//                                                })
//                                                .showCancelButton(true)
//                                                .setCancelText("线下支付")
//                                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                                    @Override
//                                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                                        sweetAlertDialog.dismiss();
//                                                        handler.post(new Runnable() {
//                                                            @Override
//                                                            public void run() {
//                                                                Toast.makeText(mContext, "线下支付", Toast.LENGTH_SHORT).show();
//                                                            }
//                                                        });
//                                                    }
//                                                })
//                                                .show();
//                                    /**************************************ttttttttttttttttttttttt***********************************************/
                                    }
                                });
                            }
                        });
                    }
                });
                refresh();
                break;
        }
    }

    public void refresh(){
        GetOrderInfoVo gov = new GetOrderInfoVo();
        gov.setAction("getOrderInfo");
        gov.setTelephone(PreferenceUtil.load(MyApplication.getmApplicationContext(),"username","11111111111"));
        HttpUtil.sendHttpRequest(Config.URL, JSON.toJSONString(gov), new HttpUtil.HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                final Result result = MJsonUtil.getOrderInfoResult(response);
                if(result.isStatus()){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            fragmentControlParksatueLayout.setVisibility(View.VISIBLE);
                            fragmentControlMainrelay.setEnabled(true);
                            fragmentControlCalladmin.setEnabled(true);
                            fragmentControlOpenlock.setEnabled(true);
                            fragmentControlLock.setEnabled(true);
                            fragmentControlMainrelay.setProgress(0);
                            fragmentControlCalladmin.setProgress(0);
                            fragmentControlOpenlock.setProgress(0);
                            fragmentControlLock.setProgress(0);
                            fragmentCollectStatuetext.setText("已停车");



                            parkName = result.getParkName();
                            spaceId = String.valueOf(result.getLotId());
                            parkPhone = result.getPhone();
                            parkId = String .valueOf(result.getParkId());

                            fragmentControlParkname.setText(parkName);
                            fragmentControlParkphone.setText(parkPhone);
                            fragmentControlParkspace.setText(spaceId);
                            fragmentControlRefreshlayout.setRefreshing(false);
                            fragmentControlFinishall.setVisibility(View.VISIBLE);

                        }
                    });
                }else{
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            fragmentControlParksatueLayout.setVisibility(View.INVISIBLE);
                            fragmentControlMainrelay.setEnabled(false);
                            fragmentControlCalladmin.setEnabled(false);
                            fragmentControlOpenlock.setEnabled(false);
                            fragmentControlLock.setEnabled(false);
                            fragmentCollectStatuetext.setText("未停车");
                            fragmentControlRefreshlayout.setRefreshing(false);
                            fragmentControlFinishall.setVisibility(View.INVISIBLE);
                        }
                    });

                }
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //fragmentControlParksatueLayout.setVisibility(View.INVISIBLE);
                        fragmentControlMainrelay.setEnabled(false);
                        fragmentControlCalladmin.setEnabled(false);
                        fragmentControlOpenlock.setEnabled(false);
                        fragmentControlLock.setEnabled(false);
                        fragmentControlRefreshlayout.setRefreshing(false);
                        Toast.makeText(mContext, "网络连接失败！", Toast.LENGTH_SHORT).show();
                        fragmentControlFinishall.setProgress(0);
                        //fragmentControlFinishall.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }
}
