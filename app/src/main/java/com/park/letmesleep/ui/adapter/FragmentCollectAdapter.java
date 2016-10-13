package com.park.letmesleep.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.park.letmesleep.R;
import com.park.letmesleep.beans.ParkInfo;

import java.util.List;
import java.util.Random;

/**
 * Created by Mr.G on 2016/10/2.
 */

public class FragmentCollectAdapter extends BaseAdapter {
    private List<ParkInfo> mList;
    private LayoutInflater mInflater;

    public FragmentCollectAdapter(Context context, List<ParkInfo> mList) {
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
            convertView = mInflater.inflate(R.layout.adapter_fragment_park, null);
            viewHolder.fragmentParkAdapterImage = (ImageView) convertView.findViewById(R.id.fragment_park_adapter_image);
            viewHolder.fragmentParkAdapterParkname = (TextView) convertView.findViewById(R.id.fragment_park_adapter_parkname);
            viewHolder.fragmentParkAdapterParkphone = (TextView) convertView.findViewById(R.id.fragment_park_adapter_parkphone);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        int res[] = {R.drawable.parkinfo1,R.drawable.parkinfo2,R.drawable.parkinfo3,R.drawable.parkinfo4};
        viewHolder.fragmentParkAdapterImage.setImageResource(res[new Random().nextInt(3)]);
        viewHolder.fragmentParkAdapterParkname.setText(mList.get(position).getParkName());
        viewHolder.fragmentParkAdapterParkphone.setText(mList.get(position).getParkPhone());
        return convertView;

    }

    class ViewHolder {
        ImageView fragmentParkAdapterImage;
        TextView fragmentParkAdapterParkname;
        TextView fragmentParkAdapterParkphone;

    }
}
