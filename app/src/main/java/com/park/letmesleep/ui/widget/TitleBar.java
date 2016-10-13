package com.park.letmesleep.ui.widget;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.park.letmesleep.R;



public class TitleBar extends FrameLayout {


    private TextView titleTv;
    private ImageView leftButton;
    private ImageView rightButton;
    private OnTitleBarClickListener onTitleBarClickListener;
    private OnTitleBarRightButtonListener onRightButtonClick;


    public void setOnTitleBarClickListener(OnTitleBarClickListener onTitleBarClickListener) {
        this.onTitleBarClickListener = onTitleBarClickListener;
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initUI(context);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUI(context);
    }

    public TitleBar(Context context) {
        super(context);
        initUI(context);
    }

    public void setLeftButtonImage(int id){
        leftButton.setImageBitmap(BitmapFactory.decodeResource(getResources(),id));
    }
    public void setRightButtonImage(int id){
        rightButton.setImageResource(id);
    }

    public void setOnRightButtonClick(OnTitleBarRightButtonListener onRightButtonClick){
        rightButton.setVisibility(VISIBLE);
        this.onRightButtonClick = onRightButtonClick;
    }

    private void initUI(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_titlebar, this, true);
        titleTv = (TextView)findViewById(R.id.title_tv);
        leftButton = (ImageView)findViewById(R.id.left_button);
        rightButton = (ImageView)findViewById(R.id.right_button);
        leftButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onTitleBarClickListener!=null)
                    onTitleBarClickListener.onLeftButtonClick();
            }
        });
        rightButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onRightButtonClick!=null)
                    onRightButtonClick.onRightButtonClick();
            }
        });
    }

    public void initTitleBar(String title){
        titleTv.setText(title);
    }





    public interface OnTitleBarClickListener {
        public void onLeftButtonClick();

    }
    public interface OnTitleBarRightButtonListener{
        public void onRightButtonClick();
    }


}
