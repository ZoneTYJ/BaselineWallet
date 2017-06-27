package com.vfinworks.vfsdk.activity.core.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.activity.core.channel.BaseChannel;
import com.vfinworks.vfsdk.activity.core.channel.ChannelMaps;
import com.vfinworks.vfsdk.activity.core.channel.QpayChannel;
import com.vfinworks.vfsdk.model.BankCardModel;
import com.vfinworks.vfsdk.model.ChannelModel;
import com.vfinworks.vfsdk.model.PaySelectModel;

import java.util.ArrayList;

/**
 * Created by tangyijian on 2016/8/23.
 */
public class SelectPayAdapter extends BaseAdapter {
    public static final int BANK_TYPE =0;
    public static final int PAY_TYPE = 1;

    private Context mContext;
    private ListView mListView;
    private int showLength;
    private View mFootView;
    private ArrayList<PaySelectModel> listPaySel;
    private int checkPosition=0;
    private OnAdapterClickListenr mOnAdapterClickListenr;

    public SelectPayAdapter(Context context, ListView listView, View footView,
                            ArrayList<PaySelectModel> list) {
        mContext=context;
        mListView=listView;
        mFootView=footView;
        listPaySel=list;
        showLength = 0;
        addFootView();
    }

    public void setCheckPosition(int index){
        checkPosition=index;
    }

    public void setOnAdapterClickListenr(OnAdapterClickListenr adapterClickListenr){
        mOnAdapterClickListenr=adapterClickListenr;
    }

    public void setShowOne() {
        if (listPaySel.size() > 0) {
            showLength = 1;
        } else {
            showLength = 0;
        }
        notifyDataSetChanged();
    }

    public void addFootView(){
        mListView.addFooterView(mFootView);
    }

    public void setShowAll() {
        showLength = listPaySel.size();
        mListView.removeFooterView(mFootView);
        notifyDataSetChanged();
    }

    public void setListPaySel(ArrayList<PaySelectModel> list){
        listPaySel=list;
    }

    @Override
    public int getCount() {
        return showLength;
    }

    @Override
    public Object getItem(int position) {
        return listPaySel.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        PaySelectModel bean = listPaySel.get(position);
        if (type == BANK_TYPE) {
            BankCardModel bank = bean.getBankCardModel();
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.list_recharge_pay_card_item,null);
            }
            ImageView iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            ImageView iv_check = (ImageView) convertView.findViewById(R.id.iv_check);
            TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            TextView tv_amount = (TextView) convertView.findViewById(R.id.tv_amount);
            tv_name.setText(bank.getBankName() + "(尾号" + bank.getCardNo() + ")");
            //todo 限额tv_amount
            bank.setDrawableIcon(iv_icon);
            if (checkPosition == position) {
                iv_check.setImageResource(R.drawable.vf_ture);
            } else {
                iv_check.setImageResource(R.drawable.vf_flase);
            }
        }
        if (type == PAY_TYPE) {
            ChannelModel channelModel=bean.getChannelModel();
            BaseChannel baseChannel= ChannelMaps.getInstance().getChannel(channelModel);
            //快捷支付列表
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.list_recharge_pay_channel_item,null);
            }
            ImageView iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            ImageView iv_check = (ImageView) convertView.findViewById(R.id.iv_check);
            TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            if(baseChannel!=null) {
                tv_name.setText(baseChannel.getName(channelModel) + "");
                baseChannel.setDrawableIcon(iv_icon);
            }
            if (channelModel.equals(QpayChannel.getChannel())) {
                iv_check.setVisibility(View.GONE);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mOnAdapterClickListenr!=null){
                            mOnAdapterClickListenr.OnAddQpayCardClick();
                        }
                    }
                });
            } else {
                convertView.setClickable(false);
                iv_check.setVisibility(View.VISIBLE);
                if (checkPosition == position) {
                    iv_check.setImageResource(R.drawable.vf_ture);
                } else {
                    iv_check.setImageResource(R.drawable.vf_flase);
                }
            }
        }
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return listPaySel.get(position).mType;
    }

    public interface OnAdapterClickListenr{
        public void OnAddQpayCardClick();
    }
}
