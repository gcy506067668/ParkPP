package com.park.letmesleep.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.park.letmesleep.R;
import com.park.letmesleep.beans.ParkStatus;

import java.util.List;

/**
 * Created by Mr.G on 2016/10/2.
 */

public class ParkInfoAdapter extends BaseAdapter {
    private List<ParkStatus> mList;
    private LayoutInflater mInflater;

    public ParkInfoAdapter(Context context, List<ParkStatus> mList) {
        this.mList = mList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.adapter_parkinfo_spinner, null);
            viewHolder.adapterSpaceId = (TextView) convertView.findViewById(R.id.adapter_parkinfo_spaceid);
            viewHolder.adapterSpaceIsBlank = (TextView) convertView.findViewById(R.id.adapter_parkinfo_spaceid_isblack);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if(mList.get(position).getOrdered()==0){

            viewHolder.adapterSpaceId.setTextColor(Color.parseColor("#ff0099cc"));
            viewHolder.adapterSpaceIsBlank.setVisibility(View.INVISIBLE);
        }else{
            viewHolder.adapterSpaceId.setTextColor(Color.parseColor("#33999999"));
            viewHolder.adapterSpaceIsBlank.setVisibility(View.VISIBLE);
        }
        viewHolder.adapterSpaceId.setText(mList.get(position).getId()+"");
        return convertView;

    }

    class ViewHolder {
        TextView adapterSpaceId;
        TextView adapterSpaceIsBlank;

    }
}
