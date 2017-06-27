package com.vfinworks.vfsdk.activity.core.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.model.ActionModel;

import java.util.ArrayList;

/**
 * Created by xiaoshengke on 2017/2/22.
 */

public class ActionAdapter extends BaseAdapter {
    ArrayList<ActionModel> datas = new ArrayList<>();

    public ActionAdapter(ArrayList<ActionModel> datas) {
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public ActionModel getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_action,parent,false);
        ImageView iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
        TextView tv_title = (TextView) convertView.findViewById(R.id.tv_title);
        ActionModel item = getItem(position);
        iv_icon.setImageResource(item.resId);
        tv_title.setText(item.title);
        return convertView;
    }
}
