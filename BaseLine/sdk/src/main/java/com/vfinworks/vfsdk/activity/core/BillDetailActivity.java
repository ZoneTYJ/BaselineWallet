package com.vfinworks.vfsdk.activity.core;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.business.DealAccept;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.NoDoubleClickListener;
import com.vfinworks.vfsdk.common.Utils;
import com.vfinworks.vfsdk.context.DealAcceptContext;
import com.vfinworks.vfsdk.context.PaymentContext;
import com.vfinworks.vfsdk.model.BillModel;
import com.vfinworks.vfsdk.enumtype.PayStateEnum;
import com.vfinworks.vfsdk.enumtype.TradeTypeEnum;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;

/**
 * Created by tangyijian on 2016/9/26.
 */
public class BillDetailActivity extends BaseActivity {
    private BaseActivity mContext = this;
    private TextView tv_trade_type;
    private TextView tv_money;
    private TextView tv_state;
    private LinearLayout lv_shop_describe;
    private TextView tv_shop_describe;
    private TextView tv_creat_time;
    private TextView tv_tradeaccount_no;
    private LinearLayout lv_merchant_no;
    private TextView tv_merchant_no;
    private TextView tv_remark;
    private Button btn_comfirm;

    private BillModel.TradeInfoListBean item;
    private TradeTypeEnum typeEnum;
    private TextView tvs_payee;
    private TextView tv_payee;
    private LinearLayout ll_payee;
    private String token;
    private DealAcceptContext mDealAcceptContext;
    private long lastClickTime=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.bill_detail, FLAG_TITLE_LINEARLAYOUT);
        super.onCreate(savedInstanceState);
        if (getIntent() != null) {
            item = (BillModel.TradeInfoListBean) getIntent().getSerializableExtra("item");
            typeEnum = (TradeTypeEnum) getIntent().getSerializableExtra("typeEnum");
            token = getIntent().getStringExtra("token");
        }
        getTitlebarView().setTitle("交易详情");
        initView();
        initData();
    }

    private void initView() {
        tv_trade_type = (TextView) findViewById(R.id.tv_trade_type);
        tv_money = (TextView) findViewById(R.id.tv_money);
        tv_state = (TextView) findViewById(R.id.tv_state);
        lv_shop_describe = (LinearLayout) findViewById(R.id.lv_shop_describe);
        tv_shop_describe = (TextView) findViewById(R.id.tv_shop_describe);
        tv_creat_time = (TextView) findViewById(R.id.tv_creat_time);
        tv_tradeaccount_no = (TextView) findViewById(R.id.tv_tradeaccount_no);
        lv_merchant_no = (LinearLayout) findViewById(R.id.lv_merchant_no);
        tv_merchant_no = (TextView) findViewById(R.id.tv_merchant_no);
        tv_remark = (TextView) findViewById(R.id.tv_remark);
        tvs_payee = (TextView) findViewById(R.id.tvs_payee);
        tv_payee = (TextView) findViewById(R.id.tv_payee);
        btn_comfirm = (Button) findViewById(R.id.btn_comfirm);
        ll_payee = (LinearLayout) findViewById(R.id.ll_payee);
    }

    private void initData() {
        String plus = "";//金额正负
        String number = item.getPayee_name();

        tv_trade_type.setText(typeEnum.getDesc() + "");
        tv_creat_time.setText(Utils.stringFormat(item.getTrade_time()));
        tv_tradeaccount_no.setText(item.getInner_trade_no() + "");
        tv_remark.setText(item.getMemo() != null ? item.getMemo() : "" + "");
        if (typeEnum == TradeTypeEnum.ENSURE || typeEnum == TradeTypeEnum.INSTANT) {
            tvs_payee.setVisibility(View.VISIBLE);
            lv_merchant_no.setVisibility(View.VISIBLE);
            lv_shop_describe.setVisibility(View.VISIBLE);
            tv_merchant_no.setText(item.getOuter_trade_no());
            tv_shop_describe.setText(item.getTrade_desc()==null?"":item.getTrade_desc());
            if (item.isPayee_is()) {
                plus = "+";
                tvs_payee.setText("付款方");
                tv_payee.setText(item.getPayer_name() + "");
            } else {
                plus = "-";
                tvs_payee.setText("收款方");
                tv_payee.setText(item.getPayee_name() + "");
            }
        } else if (typeEnum == TradeTypeEnum.DEPOSIT) {
            ll_payee.setVisibility(View.GONE);
            lv_merchant_no.setVisibility(View.GONE);
            tv_merchant_no.setText(item.getOuter_trade_no());
            plus = "+";
        } else if (typeEnum == TradeTypeEnum.REFUND) {
            tv_payee.setText("原路退回到银行卡");
            plus = "+";
        } else if (typeEnum == TradeTypeEnum.WITHDRAWAL || typeEnum == TradeTypeEnum
                .TRANSFER_CARD) {
            number = number.substring(number.length() - 4);
            tv_payee.setText(item.getPayee() + "(尾号" + number + ")" + item.getPayee_auth_name());
            if (item.isPayee_is()) {
                plus = "+";
            } else {
                plus = "-";
            }
        } else if (typeEnum == TradeTypeEnum.TRANSFER) {
            networkSetPhone();
            if (item.isPayee_is()) {
                plus = "+";
            } else {
                plus = "-";
            }
        }
        tv_money.setText(plus+item.getAmount() + "");
        if (item.getStateDesc().contains("SUCCESS") || item.getStateDesc().contains("SUBMIT")) {
            tv_state.setText("成功");
            tv_state.setTextColor(getResources().getColor(R.color.text_color_green));
        } else {
            tv_state.setText(item.getStateDesc() + "");
        }
        setPayBill();
    }
    //是交易并且待支付的情况
    private void setPayBill() {
        if(PayStateEnum.isWaitPay(item.getStateDesc()) &&
                TradeTypeEnum.isNeedPay(typeEnum)){
            btn_comfirm.setOnClickListener(new NoDoubleClickListener() {
                @Override
                protected void onNoDoubleClick(View view) {
                    lastClickTime = System.currentTimeMillis();
                    PaymentContext paymentContext = new PaymentContext();
                    paymentContext.setSignFlag(true);
                    paymentContext.setToken(token);
                    paymentContext.setCallBackMessageId(WalletActivity.paycallbackMessageID);
                    paymentContext.setPayee_name(item.getPayee_name());
                    paymentContext.setOrderNums(item.getOuter_trade_no());
                    if (typeEnum == TradeTypeEnum.INSTANT || typeEnum == TradeTypeEnum.ENSURE) {
                        paymentContext.setOrderInfo(item.getTrade_desc());
                    } else if (typeEnum == TradeTypeEnum.TRANSFER || typeEnum == TradeTypeEnum.TRANSFER_CARD) {

                        paymentContext.setOrderInfo("钱包转账");
                    }
                    paymentContext.setOrderAmount(item.getAmount());
                    paymentContext.setOrder_trade_type(typeEnum.toString());
                    //                    Intent intent = new Intent(mContext,
                    // PaymentActivityOld.class);
                    Intent intent = new Intent(mContext, PaymentActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("context", paymentContext);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            });
            btn_comfirm.setVisibility(View.VISIBLE);
        }
        if(PayStateEnum.isDealAccept(item.getStateDesc())
                && TradeTypeEnum.ENSURE.equals(typeEnum)
                && !item.isPayee_is()){
            btn_comfirm.setOnClickListener(new NoDoubleClickListener() {
                @Override
                protected void onNoDoubleClick(View view) {
                    mDealAcceptContext = new DealAcceptContext();
                    mDealAcceptContext.setRequestNo(Utils.getRandom());
                    mDealAcceptContext.setOutTradeNumber(item.getOuter_trade_no());
                    mDealAcceptContext.setToken(SDKManager.token);
                    DealAccept dealAccept=new DealAccept(mContext, mDealAcceptContext,new DealAccept.AcceeptResponseHandler() {
                        @Override
                        public void onSuccess(Object responseBean, String responseString) {
                            startSuccessAct();
                        }

                        @Override
                        public void onError(String statusCode, String errorMsg) {
                            mContext.showShortToast(errorMsg);
                        }
                    });
                    dealAccept.doDealAccept();
                }
            });
            btn_comfirm.setText("确认收货");
            btn_comfirm.setVisibility(View.VISIBLE);
        }

    }

    public void startSuccessAct() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("context", mDealAcceptContext);
        intent.putExtras(bundle);
        intent.setClass(mContext, TradeResultActivity.class);
        startActivity(intent);
        finishAll();
    }

    private void networkSetPhone() {
        RequestParams reqParam = new RequestParams();
        reqParam.putData("service", "query_member_by_id");
        reqParam.putData("member", item.getPayee());
        HttpUtils.getInstance(mContext).excuteHttpRequest(HttpRequsetUri.getInstance()
                        .getMemberDoUri(), reqParam,
                new VFinResponseHandler<UserMobileInfo>(UserMobileInfo.class) {
                    @Override
                    public void onSuccess(UserMobileInfo responseBean, String responseString) {
                        String number=responseBean.mobile_star;
                        StringBuffer sb = new StringBuffer();
                        for (int i = 0; i < number.length(); i++) {
                            if (i < 3 || i >= number.length() - 4) {
                                sb.append(number.charAt(i));
                            } else {
                                sb.append("*");
                            }
                        }
                        tv_payee.setText("钱包账户 " + sb.toString()+"(" + responseBean.real_name+")");
                        tv_remark.setText(item.getTrade_desc()==null?"":item.getTrade_desc() + "");
                    }

                    @Override
                    public void onError(String statusCode, String errorMsg) {
                        showShortToast(errorMsg+"");
                    }
                }, this);
    }


    class UserMobileInfo {
        public String mobile_star;
        public String real_name;
    }
}
