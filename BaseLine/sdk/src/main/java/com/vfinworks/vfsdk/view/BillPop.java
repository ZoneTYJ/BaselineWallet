package com.vfinworks.vfsdk.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;

/**
 * Created by xiaoshengke on 2016/4/11.
 */
public class BillPop extends PopupWindow{
    private TextView tv1;
    private TextView tv2;
    private TextView tv3;
    private TextView tv4;
    private TextView tv5;
    private TextView tv6;
    private TextView tv7;

    public BillPop(Context context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.pop_bill,null);
        bindViews(view);
        setContentView(view);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setTouchable(true);
        setOutsideTouchable(false);
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());

    }

    private void bindViews(View view) {
        tv1 = (TextView)view.findViewById(R.id.tv1);
        tv2 = (TextView) view.findViewById(R.id.tv2);
        tv3 = (TextView) view.findViewById(R.id.tv3);
        tv4 = (TextView) view.findViewById(R.id.tv4);
        tv5 = (TextView) view.findViewById(R.id.tv5);
        tv6 = (TextView) view.findViewById(R.id.tv6);
        tv7 = (TextView) view.findViewById(R.id.tv7);

        tv1.setTag(1);
        tv2.setTag(2);
        tv3.setTag(3);
        tv4.setTag(4);
        tv5.setTag(5);
        tv6.setTag(6);
        tv7.setTag(7);
    }


    private View.OnClickListener clickListener;

    public void setClickListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
        if(clickListener != null) {
            tv1.setOnClickListener(clickListener);
            tv2.setOnClickListener(clickListener);
            tv3.setOnClickListener(clickListener);
            tv4.setOnClickListener(clickListener);
            tv5.setOnClickListener(clickListener);
            tv6.setOnClickListener(clickListener);
            tv7.setOnClickListener(clickListener);
        }
    }
}
