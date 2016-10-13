package com.park.letmesleep.ui;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.dd.CircularProgressButton;
import com.park.letmesleep.R;
import com.park.letmesleep.beans.ParkInfo;
import com.park.letmesleep.beans.ParkStatus;
import com.park.letmesleep.config.Config;
import com.park.letmesleep.db.ParkDatabaseUtil;
import com.park.letmesleep.ui.adapter.ParkInfoAdapter;
import com.park.letmesleep.ui.widget.TitleBar;
import com.park.letmesleep.util.HttpUtil;
import com.park.letmesleep.util.MJsonUtil;
import com.park.letmesleep.util.MyApplication;
import com.park.letmesleep.util.PreferenceUtil;
import com.park.letmesleep.vo.GetLotInfoVo;
import com.park.letmesleep.vo.OrderParkVo;
import com.park.letmesleep.vo.Result;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ParkInfoDetilActivity extends Activity {


    @InjectView(R.id.activity_parkinfodetil_titlebar)
    TitleBar activityParkinfodetilTitlebar;
    @InjectView(R.id.activity_parkinfodetil_viewpager)
    ViewPager activityParkinfodetilViewpager;
    @InjectView(R.id.activity_parkinfodetil_parkname)
    TextView activityParkinfodetilParkname;
    @InjectView(R.id.activity_parkinfodetil_parkphone)
    TextView activityParkinfodetilParkphone;
    @InjectView(R.id.activity_parkinfodetil_blackspacespinner)
    Spinner activityParkinfodetilBlackspacespinner;
    @InjectView(R.id.activity_parkinfodetil_parkorderbutton)
    CircularProgressButton activityParkinfodetilParkorderbutton;
    @InjectView(R.id.activity_parkinfodetil_blackparkinfo)
    TextView activityParkinfodetilBlackparkinfo;
    private PagerAdapter mAdaapter;
    private List<View> mPagerList;
    private List<Integer> mImageList;
    private List<ParkInfo> mDataBaseList;
    private List<ParkStatus> mDataList;
    private String parkid, parkName, parkPhone;
    private Handler mHandler = new Handler();
    private ParkDatabaseUtil pu;
    private boolean isCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {        //全屏显示，通知栏背景设置
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_parkinfodetil);
        ButterKnife.inject(this);

        pu = new ParkDatabaseUtil(this);
        mDataBaseList = pu.getAllParkInfo();
        initView();


    }

    private void initView() {
        Bundle bundle = getIntent().getExtras();
        parkid = bundle.getString("parkid", "null");
        parkName = bundle.getString("parkname", "网络异常");
        parkPhone = bundle.getString("parkphone", "网络异常");

        activityParkinfodetilParkorderbutton.setIndeterminateProgressMode(true);
        activityParkinfodetilParkname.setText(parkName);
        activityParkinfodetilParkphone.setText("停车场电话：" + parkPhone);
        mPagerList = new ArrayList<>();
        mImageList = new ArrayList<>();
        mImageList.add(R.drawable.viewpager1);
        mImageList.add(R.drawable.viewpager2);
        mImageList.add(R.drawable.viewpager3);
        mImageList.add(R.drawable.viewpager4);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View v1 = mInflater.inflate(R.layout.viewpager_adapter, null);
        View v2 = mInflater.inflate(R.layout.viewpager_adapter, null);
        View v3 = mInflater.inflate(R.layout.viewpager_adapter, null);
        View v4 = mInflater.inflate(R.layout.viewpager_adapter, null);
        mPagerList.add(v1);
        mPagerList.add(v2);
        mPagerList.add(v3);
        mPagerList.add(v4);

        mAdaapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return mPagerList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(mPagerList.get(position));

            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View v = mPagerList.get(position);
                ImageView image = (ImageView) v.findViewById(R.id.viewpager_image);
                image.setImageResource(mImageList.get(position));
                container.addView(v);
                return v;
            }
        };
        activityParkinfodetilViewpager.setAdapter(mAdaapter);
        activityParkinfodetilTitlebar.initTitleBar("停车场");
        activityParkinfodetilTitlebar.setOnTitleBarClickListener(new TitleBar.OnTitleBarClickListener() {
            @Override
            public void onLeftButtonClick() {
                finish();
            }
        });
        isCollection = (pu.query(" where parkid="+parkid).size()==1);
        if(isCollection)
            activityParkinfodetilTitlebar.setRightButtonImage(R.drawable.parkinfo_detil_selected);
        else
            activityParkinfodetilTitlebar.setRightButtonImage(R.drawable.parkinfo_detil_unselected);
        activityParkinfodetilTitlebar.setOnRightButtonClick(new TitleBar.OnTitleBarRightButtonListener() {
            @Override
            public void onRightButtonClick() {
                if(pu.query(" where parkid="+parkid).size()>=1){
                    pu.delete(parkid);
                    activityParkinfodetilTitlebar.setRightButtonImage(R.drawable.parkinfo_detil_unselected);
                    Toast.makeText(ParkInfoDetilActivity.this, "取消收藏", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ParkInfoDetilActivity.this, "已收藏", Toast.LENGTH_SHORT).show();
                    activityParkinfodetilTitlebar.setRightButtonImage(R.drawable.parkinfo_detil_selected);
                    ParkInfo pi = new ParkInfo();
                    pi.setParkId(parkid);
                    pi.setParkName(parkName);
                    pi.setParkPhone(parkPhone);
                    pu.insert(pi);
                }

            }
        });


        GetLotInfoVo gv = new GetLotInfoVo();
        gv.setAction("getLotInfo");
        gv.setParkId(Integer.parseInt(parkid));
        HttpUtil.sendHttpRequest(Config.URL, JSON.toJSONString(gv), new HttpUtil.HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                final Result<List<ParkStatus>> result = MJsonUtil.getParkStatus(response);
                if (result.isStatus()) {
                    mDataList = result.getData();


                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ParkInfoAdapter adapter = new ParkInfoAdapter(ParkInfoDetilActivity.this, mDataList);
                            activityParkinfodetilBlackspacespinner.setAdapter(adapter);
                        }
                    });
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ParkInfoDetilActivity.this, result.getStatusInfo(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ParkInfoDetilActivity.this, "网络异常！", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @OnClick(R.id.activity_parkinfodetil_parkorderbutton)
    public void onClick() {
        if (activityParkinfodetilParkorderbutton.getProgress() == 0 || activityParkinfodetilParkorderbutton.getProgress() == -1) {
            activityParkinfodetilParkorderbutton.setProgress(50);
            if (activityParkinfodetilBlackspacespinner.getSelectedItemPosition()==-1) {
                Toast.makeText(this, "请选择车位以预定！", Toast.LENGTH_SHORT).show();
                activityParkinfodetilParkorderbutton.setProgress(0);
                return;
            }
            OrderParkVo ov = new OrderParkVo();
            ov.setAction("orderPark");
            ov.setParkId(Integer.parseInt(parkid));
            ov.setSpaceId(mDataList.get(activityParkinfodetilBlackspacespinner.getSelectedItemPosition()).getId());
            ov.setTelephone(PreferenceUtil.load(MyApplication.getmApplicationContext(), "username", "null"));
            HttpUtil.sendHttpRequest(Config.URL, JSON.toJSONString(ov), new HttpUtil.HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    Result r = MJsonUtil.getResult(response);
                    final Result fr = r;
                    if (r.isStatus()) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ParkInfoDetilActivity.this, "预定成功！", Toast.LENGTH_SHORT).show();
                            }
                        });
                        finish();
                    } else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                activityParkinfodetilParkorderbutton.setProgress(-1);
                                Toast.makeText(ParkInfoDetilActivity.this, fr.getStatusInfo(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onError(Exception e) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            activityParkinfodetilParkorderbutton.setProgress(-1);
                            Toast.makeText(ParkInfoDetilActivity.this, "网络异常！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

}
