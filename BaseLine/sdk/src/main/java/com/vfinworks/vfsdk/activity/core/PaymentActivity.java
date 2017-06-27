package com.vfinworks.vfsdk.activity.core;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.activity.core.adapter.SelectPayAdapter;
import com.vfinworks.vfsdk.activity.core.channel.BaseChannel;
import com.vfinworks.vfsdk.activity.core.channel.ChannelMaps;
import com.vfinworks.vfsdk.activity.core.channel.OverageChannel;
import com.vfinworks.vfsdk.activity.core.channel.QpayChannel;
import com.vfinworks.vfsdk.business.QueryTrade;
import com.vfinworks.vfsdk.business.QueryTradeRepeat;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.context.PaymentContext;
import com.vfinworks.vfsdk.model.UserDetailEntity;
import com.vfinworks.vfsdk.enumtype.QueryBankListTypeEnum;
import com.vfinworks.vfsdk.enumtype.TradeTypeEnum;
import com.vfinworks.vfsdk.enumtype.VFCallBackEnum;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.model.BankCardListModel;
import com.vfinworks.vfsdk.model.BankCardModel;
import com.vfinworks.vfsdk.model.ChannelListModel;
import com.vfinworks.vfsdk.model.ChannelModel;
import com.vfinworks.vfsdk.model.PaySelectModel;
import com.vfinworks.vfsdk.model.QpayNewBankCardModel;
import com.vfinworks.vfsdk.model.VFSDKResultModel;

import java.util.ArrayList;

/**
 * 功能修改
 * 2016.4.20
 */
public class PaymentActivity extends BaseActivity implements OnClickListener {

    private BaseActivity mContext = this;
    private LinearLayout layoutPayment;
    private PaymentContext mPaymentContext;
    private ChannelModel currentChannel;
    private boolean isNewCard = false;
    private String bankCardId;
    private QpayNewBankCardModel newBank = null;
    private TextView tvOrderInfo;
    private TextView tv_payee;


    private TextView tvPayAmount;
    private View layout_load_more;
    private ArrayList<PaySelectModel> listPaySel;
    private SelectPayAdapter mAdapter;
    private ListView lv_list;

    private int checkPosition;

    private boolean turnOutFlag;
    private QueryTradeRepeat mQueryTradeRepeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.payment, FLAG_TITLE_LINEARLAYOUT);
        mPaymentContext = (PaymentContext) this.getIntent().getExtras().getSerializable("context");
        super.onCreate(savedInstanceState);
        this.getTitlebarView().setTitle("钱包收银台");
        this.getTitlebarView().initLeft(new OnClickListener() {
            @Override
            public void onClick(View v) {
                backOnClick();
            }
        });
    }

    @Override
    public void initWidget() {
        Button btnDone = (Button) findViewById(R.id.btn_done);
        btnDone.setOnClickListener(this);
        layoutPayment = (LinearLayout) this.findViewById(R.id.layout_payment);
        tvOrderInfo = (TextView) findViewById(R.id.tv_order_detail);
        tvOrderInfo.setText(mPaymentContext.getOrderInfo());
        tvPayAmount = (TextView) findViewById(R.id.tv_pay_amount);
        tvPayAmount.setText("￥"+ mPaymentContext.getOrderAmount());
        tv_payee=(TextView)findViewById(R.id.tv_payee);
        tv_payee.setText(mPaymentContext.getPayee_name());

        layout_load_more = View.inflate(mContext, R.layout.list_footer_item, null);
        layout_load_more.setOnClickListener(this);

        lv_list = (ListView) findViewById(R.id.lv_list);
        listPaySel = new ArrayList<PaySelectModel>();
        mAdapter = new SelectPayAdapter(this, lv_list, layout_load_more, listPaySel);
        mAdapter.setOnAdapterClickListenr(new SelectPayAdapter.OnAdapterClickListenr() {
            @Override
            public void OnAddQpayCardClick() {
                btnAddQpayCardClick();
            }
        });
        lv_list.setAdapter(mAdapter);
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                checkPosition = position;
                mAdapter.setCheckPosition(checkPosition);
                mAdapter.notifyDataSetChanged();
            }
        });
        getAccountData();
    }

    private void btnAddQpayCardClick() {
        Intent intent = new Intent();
        intent.putExtra("basecontext", mPaymentContext);
        intent.setClass(this, AddQpayInputAccoutActivity.class);
        startActivity(intent);
    }

    private void getAccountData() {
        listPaySel.clear();
        RequestParams reqParam = new RequestParams();
        reqParam.putData("service", "query_member");
        reqParam.putData("token", SDKManager.token);
        HttpUtils.getInstance(mContext).excuteHttpRequest(HttpRequsetUri.getInstance()
                        .getMemberDoUri(),
                reqParam, new VFinResponseHandler<UserDetailEntity>(UserDetailEntity.class) {
                    @Override
                    public void onSuccess(UserDetailEntity responseBean, String responseString) {
                        hideProgress();
                        turnOutFlag = responseBean.isTurnOut();
                        ChannelModel channelModel = OverageChannel.getChannel();
                        channelModel.amount = responseBean.getAvaliable_balance();
                        PaySelectModel paySelectModel = new PaySelectModel(SelectPayAdapter
                                .PAY_TYPE, channelModel);
                        listPaySel.add(paySelectModel);
                        getBankCardLst();
                    }

                    @Override
                    public void onError(String statusCode, String errorMsg) {
                        hideProgress();
                        showShortToast(errorMsg);
                    }
                }, this);
    }

    private void getBankCardLst() {
        this.showProgress();
        RequestParams reqParam = new RequestParams();
        reqParam.putData("service", "query_bank_list");
        reqParam.putData("token", SDKManager.token);
        reqParam.putData("pay_attribute", QueryBankListTypeEnum.QPAY.getCode());
        HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(),
                reqParam, new VFinResponseHandler<BankCardListModel>(BankCardListModel.class) {

                    @Override
                    public void onSuccess(BankCardListModel responseBean, String responseString) {
                        hideProgress();
                        for (BankCardModel bankCardModel : responseBean.getCardList()) {
                            PaySelectModel paySelectModel = new PaySelectModel(SelectPayAdapter
                                    .BANK_TYPE,
                                    bankCardModel);
                            listPaySel.add(paySelectModel);
                        }
                        getqueryChannel();
                    }

                    @Override
                    public void onError(String statusCode, String errorMsg) {
                        hideProgress();
                        showShortToast(errorMsg);
                    }

                }, this);
    }

    private void getqueryChannel() {
        RequestParams reqParam = new RequestParams();
        reqParam.putData("service", "query_channel");
        reqParam.putData("access_channel", "SDK");
        reqParam.putData("token", SDKManager.token);
        reqParam.putData("biz_type", "I");
        reqParam.putData("status", "VALID");
        showProgress();
        HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getAcquirerDoUri(),
                reqParam, new VFinResponseHandler<ChannelListModel>(ChannelListModel.class) {

                    @Override
                    public void onSuccess(ChannelListModel responseBean, String responseString) {
                        hideProgress();
                        for (ChannelModel channelModel : responseBean.channelList) {
                            PaySelectModel paySelectModel = new PaySelectModel(SelectPayAdapter
                                    .PAY_TYPE, channelModel);
                            if(ChannelMaps.getInstance().getChannel(channelModel)!=null){
                                listPaySel.add(paySelectModel);
                            }
                        }
                        mAdapter.setListPaySel(listPaySel);
                        mAdapter.setShowOne();
                    }

                    @Override
                    public void onError(String statusCode, String errorMsg) {
                        hideProgress();
                        showShortToast(errorMsg);
                    }

                }, this);
    }

    private void startAnimation() {
        ViewTreeObserver vto = layoutPayment.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                layoutPayment.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                Animation anim = new TranslateAnimation(0, 0, layoutPayment.getHeight(), 0);
                anim.setDuration(800);
                layoutPayment.startAnimation(anim);
            }
        });
    }


    @Override
    public void onClick(View arg0) {
        if (arg0.getId() == R.id.btn_done) {
            if(checkParams()) {
                btnPayClick();

            }
        } else if (arg0 == layout_load_more) {
            if (mAdapter != null) {
                mAdapter.setShowAll();
            }
        }

    }

    private void btnPayClick() {
        PaySelectModel bean = listPaySel.get(checkPosition);
        if (bean.mType == SelectPayAdapter.BANK_TYPE) {
            bankCardId = bean.getBankCardModel().getBankcardId();
            currentChannel = QpayChannel.getChannel();
        } else {
            bankCardId = null;
            currentChannel = bean.getChannelModel();
        }
        BaseChannel baseChannel = getCurrentPayment();
        if(baseChannel==null){
            showShortToast("渠道不存在");
            return;
        }
        baseChannel.doPay(mPaymentContext);
    }


    private BaseChannel getCurrentPayment() {
            final BaseChannel channel = ChannelMaps.getInstance().getChannel(currentChannel);
            if(channel==null){
                return null;
            }
            channel.setChannelPara(this, new BaseChannel.ChannelResponseHandler() {
                        @Override
                        public void OnSuccess(String innerOrderNo) {
                            creatQueryTrade(mPaymentContext.getOutTradeNumber());
                        }
                    }, isNewCard,
                    newBank, bankCardId);
        channel.setAmount(currentChannel.amount);
            return channel;
    }

    @Override
    protected void onDestroy() {
//        if(ChannelMaps.getInstance().getChannel(currentChannel) != null)
//            ChannelMaps.getInstance().getChannel(currentChannel).clear();
        ChannelMaps.getInstance().clear();
        super.onDestroy();
    }

    private boolean checkParams() {
        if(!turnOutFlag){
            showShortToast("该账户已经冻结");
            return false;
        }
        return true;
    }

    private void backOnClick() {
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backOnClick();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onIntentForResult(Intent intent) {
    }

    private void creatQueryTrade(String outNo){
        mQueryTradeRepeat = new QueryTradeRepeat(this, QueryTrade.MAX_COUNT,outNo,mPaymentContext.getToken());
        mQueryTradeRepeat.setHandler(new QueryTradeRepeat.QueryTradeHandler() {
            @Override
            public void callBackSuccess() {
                hideProgress();
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("context", mPaymentContext);
                bundle.putString("statue", TradeResultActivity.SUCCESS);
                intent.putExtras(bundle);
                intent.setClass(mContext, TradeResultActivity.class);
                finishAll();
                startActivity(intent);
            }

            @Override
            public void callBackFault() {
                callFault();
            }

            @Override
            public void callBackRequery() {
                hideProgress();
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("context", mPaymentContext);
                bundle.putString("statue",TradeResultActivity.PROCESS);
                intent.putExtras(bundle);
                intent.setClass(mContext, TradeResultActivity.class);
                finishAll();
                startActivity(intent);
            }

            @Override
            public void putData(RequestParams reqParam) {
                if(TextUtils.isEmpty(mPaymentContext.getOrder_trade_type())){
                    reqParam.putData("trade_type", TradeTypeEnum.ALL.toString());
                }else {
                    reqParam.putData("trade_type", mPaymentContext.getOrder_trade_type());
                }
            }
        });
        mQueryTradeRepeat.startQueryTrade();
    }

    private void callFault(){
        hideProgress();
        if (SDKManager.getInstance().getCallbackHandler() != null) {
            VFSDKResultModel result = new VFSDKResultModel();
            result.setResultCode(VFCallBackEnum.ERROR_CODE_BUSINESS
                    .getCode());
            result.setMessage("支付失败");
            mPaymentContext.sendMessage(result);
        }
        finishAll();
    }
}
