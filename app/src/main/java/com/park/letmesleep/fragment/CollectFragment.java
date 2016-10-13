package com.park.letmesleep.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.park.letmesleep.R;
import com.park.letmesleep.beans.ParkInfo;
import com.park.letmesleep.db.ParkDatabaseUtil;
import com.park.letmesleep.ui.ParkInfoDetilActivity;
import com.park.letmesleep.ui.adapter.FragmentParkAdapter;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class CollectFragment extends Fragment {


    @InjectView(R.id.fragment_collect_listview)
    ListView fragmentCollectListview;

    private List<ParkInfo> parkInfos;
    private ParkDatabaseUtil pu;
    public CollectFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collect, container, false);
        ButterKnife.inject(this, view);

        pu = new ParkDatabaseUtil(view.getContext());
        parkInfos = pu.getAllParkInfo();

        FragmentParkAdapter adapter = new FragmentParkAdapter(view.getContext(),parkInfos);
        fragmentCollectListview.setAdapter(adapter);
        fragmentCollectListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), ParkInfoDetilActivity.class);
                intent.putExtra("parkid",parkInfos.get(position).getParkId());
                intent.putExtra("parkname",parkInfos.get(position).getParkName());
                intent.putExtra("parkphone",parkInfos.get(position).getParkPhone());
                view.getContext().startActivity(intent);

            }
        });//
        return view;
    }

    public void myListViewNotify(){
        parkInfos.clear();
        List<ParkInfo> newList = pu.getAllParkInfo();
        for (ParkInfo p:newList) {
            parkInfos.add(p);
        }
        fragmentCollectListview.deferNotifyDataSetChanged();
    }

    @Override
    public void onResume() {
        myListViewNotify();
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
