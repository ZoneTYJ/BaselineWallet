package com.vfinworks.vfsdk.activity.core;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.activity.BaseListAdapter;
import com.vfinworks.vfsdk.activity.StepPage;
import com.vfinworks.vfsdk.common.Utils;
import com.vfinworks.vfsdk.context.BillContext;
import com.vfinworks.vfsdk.model.BillModel;
import com.vfinworks.vfsdk.enumtype.PayStateEnum;
import com.vfinworks.vfsdk.enumtype.TradeTypeEnum;
import com.vfinworks.vfsdk.enumtype.VFCallBackEnum;
import com.vfinworks.vfsdk.model.VFSDKResultModel;
import com.vfinworks.vfsdk.view.FilterDialog;
import com.vfinworks.vfsdk.view.XListView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.halfbit.pinnedsection.PinnedSectionListView;

/**
 * 账单
 * Created by xiaoshengke on 2016/4/7.
 */
public class BillActivity extends BaseActivity implements XListView.IXListViewListener, BaseListAdapter.LoadMoreListener {
    private final int REQUEST_DATE = 100;
    private TextView tv_title;
    private TextView btn_right;
    private View title_bar;
    private PinnedSectionListView lv_bill;
    private SwipeRefreshLayout srl_refresh;

    private BillContext billContext;
    private BillModel billEntity;
    private BaseActivity mContext=this;
    private StepPage mPage = new StepPage();

    private int billcallbackMessageID = 1;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == billcallbackMessageID) {
                handleBillCallback((VFSDKResultModel) msg.obj);
            }
        }
    };
    private TextView tv_none;

    private void handleBillCallback(VFSDKResultModel obj) {
        hideProgress();
        if (obj.getResultCode().equalsIgnoreCase(VFCallBackEnum.OK.getCode()) == true) {
            if(obj.getJsonData()!=null) {
                srl_refresh.setRefreshing(false);
                Gson gson = new Gson();
                billEntity = gson.fromJson(obj.getJsonData(), BillModel.class);
                if(billEntity.getTrade_info_list() != null) {
                    mPage.setResponseCount(billEntity.getTrade_info_list().size());

                    List<BillModel.TradeInfoListBean> trade_info_list = billEntity.getTrade_info_list();
                    if(trade_info_list.size() > 0){
                        if(mPage.mPageIndex > 1){
                            String month = adapter.getItem(adapter.getCount() - 2).getTrade_time().substring(0,6);
                            String one = trade_info_list.get(0).getTrade_time().substring(0,6);
                            if(month.compareTo(one) != 0){
                                adapter.getItem(adapter.getCount() - 2).hide_line = true;
                                BillModel.TradeInfoListBean section1 = new BillModel.TradeInfoListBean();
                                section1.isSection = true;
                                section1.section_desc = one.substring(0,4)+"年"+one.substring(4,6)+"月";
                                trade_info_list.add(0,section1);
                            }
                        }else{
                            try {
                                BillModel.TradeInfoListBean section1 = new BillModel.TradeInfoListBean();
                                Date now = new Date();
                                String month = trade_info_list.get(0).getTrade_time().substring(0,6);
                                Date parse = arguDateFormat.parse(trade_info_list.get(0).getTrade_time());
                                if(now.getYear() == parse.getYear() && now.getMonth() == parse.getMonth()){
                                    section1.section_desc = "本月";
                                }else{
                                    section1.section_desc = month.substring(0,4)+"年"+month.substring(4,6)+"月";
                                }
                                section1.isSection = true;
                                trade_info_list.add(0,section1);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }
                        insertSection(1,trade_info_list);
                    }
                }
                adapter.addData(billEntity.getTrade_info_list());
            }else {
                Toast.makeText(BillActivity.this, obj.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(BillActivity.this, obj.getMessage(), Toast.LENGTH_SHORT).show();
            srl_refresh.setRefreshing(false);
        }
    }

    /**
     * 插入章节
     * @param index
     *      必须大于1
     * @param trade_info_list
     */
    private void insertSection(int index,List<BillModel.TradeInfoListBean> trade_info_list){
        for(int i = index;i < trade_info_list.size();i++){
            if(trade_info_list.get(i - 1).isSection || trade_info_list.get(i).isSection ){
                continue;
            }
            String before = trade_info_list.get(i - 1).getTrade_time().substring(0,6);
            String now = trade_info_list.get(i).getTrade_time().substring(0,6);
            if(before.compareTo(now) != 0){
                trade_info_list.get(i - 1).hide_line = true;
                BillModel.TradeInfoListBean section2 = new BillModel.TradeInfoListBean();
                section2.isSection = true;
                section2.section_desc = now.substring(0,4)+"年"+now.substring(4,6)+"月";
                trade_info_list.add(i,section2);
                insertSection(i,trade_info_list);
                break;
            }
        }
    }

    private BillAdapter adapter = new BillAdapter();

    /**
     * true为重新获取数据
     */
    private boolean afreshGetData;

    private SimpleDateFormat arguDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    private SimpleDateFormat dayFormat = new SimpleDateFormat("MM-dd");
    private Calendar rightCalendar = Calendar.getInstance();
    private Calendar leftCalendar = Calendar.getInstance();

    private TradeTypeEnum typeEnum;
    private FilterDialog filterDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_bill);
        super.onCreate(savedInstanceState);
        billContext = (BillContext) getIntent().getExtras().getSerializable("context");
        typeEnum = TradeTypeEnum.ALL;
        filterDialog = new FilterDialog(this);
        bindViews();
        leftCalendar.add(Calendar.YEAR,-1);
        rightCalendar.add(Calendar.DATE,1);
        getDate(typeEnum.toString(),arguDateFormat.format(leftCalendar.getTime()),arguDateFormat.format(rightCalendar.getTime()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        rightCalendar = Calendar.getInstance();
        rightCalendar.add(Calendar.DATE,1);
    }

    private void bindViews() {
        tv_none = (TextView) findViewById(R.id.tv_none);
        srl_refresh = (SwipeRefreshLayout) findViewById(R.id.srl_refresh);
        lv_bill = (PinnedSectionListView) findViewById(R.id.lv_bill);
        btn_right = (TextView) findViewById(R.id.btn_right);
        tv_title = (TextView) findViewById(R.id.tv_title);
        title_bar =  findViewById(R.id.title_bar);

        tv_title.setText("交易记录");

        srl_refresh.setColorSchemeResources(R.color.title_bg);
        srl_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                afreshGetData = true;
                adapter.setState(BaseListAdapter.STATE_PULL_REFRESH);
                getDate(typeEnum.toString(),arguDateFormat.format(leftCalendar.getTime()),arguDateFormat.format(rightCalendar.getTime()));
            }
        });
        lv_bill.setAdapter(adapter);
        adapter.setLoadMoreListener(this);
        adapter.mPage = mPage;
        lv_bill.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BillModel.TradeInfoListBean item = adapter.getItem(position);
                if(item != null && !item.isSection) {
                    Intent intent = new Intent(mContext, BillDetailActivity.class);
                    intent.putExtra("item", item);
                    intent.putExtra("typeEnum", TradeTypeEnum.getTypeByCode(item.trade_type));
                    intent.putExtra("token", billContext.getToken());
                    startActivity(intent);
                }
            }
        });

        findViewById(R.id.layout_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAll();
            }
        });
        btn_right.setText("筛选");
        btn_right.setVisibility(View.VISIBLE);
        btn_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog.show();
            }
        });

        filterDialog.setListener(new FilterDialog.SelectClickListener() {
            @Override
            public void selectClick(int index) {
                switch (index){
                    case 0:
                        typeEnum = TradeTypeEnum.ALL;
                        break;
                    case 1:
                        typeEnum = TradeTypeEnum.INSTANT;
                        break;
                    case 2:
                        typeEnum = TradeTypeEnum.ENSURE;
                        break;
                    case 3:
                        typeEnum = TradeTypeEnum.TRANSFER;
                        break;
                    case 4:
                        typeEnum = TradeTypeEnum.TRANSFER_CARD;
                        break;
                    case 5:
                        typeEnum = TradeTypeEnum.REFUND;
                        break;
                    case 6:
                        typeEnum = TradeTypeEnum.DEPOSIT;
                        break;
                    case 7:
                        typeEnum = TradeTypeEnum.WITHDRAWAL;
                        break;

                }
                afreshGetData = true;
                getDate(typeEnum.toString(),arguDateFormat.format(leftCalendar.getTime()),arguDateFormat.format(rightCalendar.getTime()));
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_DATE){
                leftCalendar.setTime((Date) data.getSerializableExtra("start_date"));
                rightCalendar.setTime((Date) data.getSerializableExtra("end_date"));
                afreshGetData = true;
                getDate(typeEnum.toString(),arguDateFormat.format(leftCalendar.getTime()),arguDateFormat.format(rightCalendar.getTime()));
            }
        }
    }

    private void getDate(String tradeType, String startDate, String endDate) {
        if(afreshGetData){
            mPage.reset();
            afreshGetData = false;
            adapter.setState(BaseListAdapter.STATE_PULL_REFRESH);
            adapter.clear();
            adapter.notifyDataSetChanged();
        }

        billContext.setCallBackMessageId(billcallbackMessageID);
        billContext.setTradeType(tradeType);
        billContext.setStartTime(startDate);
        billContext.setEndTime(endDate);
        billContext.setPageNo(mPage.mPageIndex);
        billContext.setPageSize(mPage.mPageSize);
//        this.showProgress();
        SDKManager.getInstance().GetBillList(this, billContext, mHandler);
    }

    @Override
    protected void onDestroy() {
        SDKManager.getInstance().clear();
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        afreshGetData = true;
        adapter.setState(BaseListAdapter.STATE_PULL_REFRESH);
        getDate(typeEnum.toString(),arguDateFormat.format(leftCalendar.getTime()),arguDateFormat.format(rightCalendar.getTime()));
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void loadMore() {
        getDate(typeEnum.toString(),arguDateFormat.format(leftCalendar.getTime()),arguDateFormat.format(rightCalendar.getTime()));
    }

    private class BillAdapter extends BaseListAdapter<BillModel.TradeInfoListBean> implements PinnedSectionListView.PinnedSectionListAdapter {
        @Override
        public View getRealView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if(convertView == null) {
                holder = new Holder();
                convertView = getLayoutInflater(BillActivity.this).inflate(R.layout.item_bill_list, parent, false);
                holder.tv_week = (TextView) convertView.findViewById(R.id.tv_week);
                holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
                holder.tv_state = (TextView) convertView.findViewById(R.id.tv_state);
                holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
                holder.v_line = convertView.findViewById(R.id.v_line);
                holder.ll_label = (LinearLayout) convertView.findViewById(R.id.ll_label);
                holder.rl_content = (RelativeLayout) convertView.findViewById(R.id.rl_content);
                holder.tv_label = (TextView) convertView.findViewById(R.id.tv_label);
                holder.tv_desc = (TextView) convertView.findViewById(R.id.tv_desc);
                holder.iv_mark = (ImageView) convertView.findViewById(R.id.iv_mark);
                convertView.setTag(holder);
            }else{
                holder = (Holder) convertView.getTag();
            }

            BillModel.TradeInfoListBean item = getItem(position);
            if(item.hide_line){
                holder.v_line.setVisibility(View.GONE);
            }else{
                holder.v_line.setVisibility(View.VISIBLE);
            }
            if(item.isSection){
                holder.ll_label.setVisibility(View.VISIBLE);
                holder.rl_content.setVisibility(View.GONE);
                holder.tv_label.setText(item.section_desc);
            }else {
                holder.ll_label.setVisibility(View.GONE);
                holder.rl_content.setVisibility(View.VISIBLE);
                try {
                    Date parse = arguDateFormat.parse(item.getTrade_time());
                    holder.tv_week.setText(Utils.getWeekOfDate(parse));
                    holder.tv_date.setText(dayFormat.format(parse));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                TradeTypeEnum typeEnum = TradeTypeEnum.getTypeByCode(item.trade_type);
//                String plus = item.isPayee_is() ? "+" : "-";
//                if (typeEnum == TradeTypeEnum.DEPOSIT || typeEnum == TradeTypeEnum.REFUND) {
//                    plus = "+";
//                }else if(typeEnum == TradeTypeEnum.WITHDRAWAL || typeEnum==TradeTypeEnum.TRANSFER_CARD){
//                    plus = "-";
//                }
//                holder.tv_money.setText(plus + item.getAmount());
                if(typeEnum==TradeTypeEnum.DEPOSIT){
                    holder.iv_mark.setImageResource(R.drawable.vf_ico_chong);
                }else if(typeEnum==TradeTypeEnum.REFUND){
                    holder.iv_mark.setImageResource(R.drawable.vf_ico_tui);
                }else if(typeEnum==TradeTypeEnum.WITHDRAWAL){
                    holder.iv_mark.setImageResource(R.drawable.vf_ico_ti);
                }else if(typeEnum==TradeTypeEnum.TRANSFER_CARD || typeEnum==TradeTypeEnum.TRANSFER){
                    holder.iv_mark.setImageResource(R.drawable.vf_ico_zhuan);
                }else if(typeEnum==TradeTypeEnum.INSTANT){
                    holder.iv_mark.setImageResource(R.drawable.vf_ico_jishi);
                }else if(typeEnum==TradeTypeEnum.ENSURE){
                    holder.iv_mark.setImageResource(R.drawable.vf_ico_danbao);
                }

                if(typeEnum==TradeTypeEnum.INSTANT || typeEnum==TradeTypeEnum.ENSURE) {
                    if(item.getTrade_desc()!=null) {
                        holder.tv_desc.setText(item.getTrade_desc() + "");
                    }else {
                        holder.tv_desc.setText("");
                    }
                }else {
                    holder.tv_desc.setText(typeEnum.getDesc());
                }
                String plus=item.isPayee_is()?"+":"-";
                if(typeEnum==TradeTypeEnum.DEPOSIT || typeEnum==TradeTypeEnum.REFUND){
                    plus="+";
                }
                holder.tv_money.setText(plus+item.getAmount());
                String stateDesc = "";
                for(PayStateEnum bean:PayStateEnum.values()) {
                    if (bean.toString().equals(item.getTrade_status())) {
                        stateDesc = bean.getDesc();
                        if (typeEnum == TradeTypeEnum.ENSURE && bean.equals(PayStateEnum.PAY_FINISHED)) {
                            stateDesc = stateDesc + "(待收货确认)";
                        }
                        break;
                    }
                }
                item.setStateDesc(stateDesc);
                holder.tv_state.setText(stateDesc);
            }
            return convertView;
        }

        @Override
        public int getViewTypeCount() {
            if(hasFooterView())
                return 3;
            else
                return super.getViewTypeCount();
        }

        @Override
        public int getItemViewType(int position) {
            if(hasFooterView()){
                if(position < getCount() - 1) {
                    if(getItem(position).isSection)
                        return 2;
                    else
                        return 0;
                }else
                    return 1;
            }else
                return super.getItemViewType(position);

        }

        @Override
        public boolean isItemViewTypePinned(int viewType) {
            return viewType == 2;
        }

        class Holder{
            TextView tv_week,tv_date,tv_state,tv_money;
            View v_line;
            LinearLayout ll_label;
            RelativeLayout rl_content;
            TextView tv_label;
            TextView tv_desc;
            ImageView iv_mark;
        }
    }


}
