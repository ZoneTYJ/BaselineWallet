package com.vfinworks.vfsdk.activity.core;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.vfinworks.vfsdk.common.NoDoubleClickListener;
import com.vfinworks.vfsdk.context.TransferContext;
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
import com.vfinworks.vfsdk.model.VFSDKResultModel;

import java.util.ArrayList;

/**
 * 转账到户
 */
public class TransferAccountConfirmActivity extends BaseActivity implements OnClickListener {
    private Button btnConfirm;

    private TransferContext mTransferContext;
    private String accountNum;
    private String money;
    private String remark;

    private BaseActivity mContext = this;
    private View layout_load_more;
    private ArrayList<PaySelectModel> listPaySel;
    private SelectPayAdapter mAdapter;
    private ListView lv_list;
    private int checkPosition;

    private ChannelModel currentChannel;
    private boolean isNewCard = false;
    private String bankCardId;
    private String nickname;
    private QueryTradeRepeat mQueryTradeRepeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.transfer_account_confirm_activity, FLAG_TITLE_LINEARLAYOUT);
        mTransferContext = (TransferContext) this.getIntent().getExtras().getSerializable("context");
        accountNum = (String) this.getIntent().getExtras().getSerializable("account_number");
        money = (String) this.getIntent().getExtras().getSerializable("amount");
        remark = (String) this.getIntent().getExtras().getSerializable("remark");
        nickname = (String) this.getIntent().getExtras().getSerializable("nickname");
        mTransferContext.setAmount(money);
        mTransferContext.setAccountNum(accountNum);
        super.onCreate(savedInstanceState);
        this.getTitlebarView().setTitle("转账到户");
        this.getTitlebarView().initLeft(new OnClickListener() {
            @Override
            public void onClick(View v) {
                backOnClick();
            }
        });
    }

    @Override
    public void initWidget() {
        TextView tvAccountName = (TextView) findViewById(R.id.tv_account_name);
        tvAccountName.setText(nickname);
        TextView tvMoney = (TextView) findViewById(R.id.tv_money);
        tvMoney.setText("¥ " + money);
        TextView tv_bank_info = (TextView) findViewById(R.id.tv_bank_info);
        if (mTransferContext.getMethod().equals("account")) {
            tv_bank_info.setVisibility(View.GONE);
        } else if (mTransferContext.getMethod().equals("card")) {

        }
        TextView etRemartk = (TextView) findViewById(R.id.et_remark);
        if (!TextUtils.isEmpty(remark)) {
            etRemartk.setText(remark);
            mTransferContext.setMemo(remark);
        }
        btnConfirm = (Button) findViewById(R.id.btn_comfirm);
        btnConfirm.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                nextBtnClick();
            }
        });

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
        intent.putExtra("basecontext", mTransferContext);
        intent.setClass(this, AddQpayInputAccoutActivity.class);
        startActivity(intent);
    }

    private void getAccountData() {
        showProgress();
        listPaySel.clear();
        RequestParams reqParam = new RequestParams();
        reqParam.putData("service", "query_member");
        reqParam.putData("token", SDKManager.token);
        HttpUtils.getInstance(mContext).excuteHttpRequest(HttpRequsetUri.getInstance()
                        .getMemberDoUri(),
                reqParam, new VFinResponseHandler<UserDetailEntity>(UserDetailEntity.class) {
                    @Override
                    public void onSuccess(UserDetailEntity responseBean, String responseString) {
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
        RequestParams reqParam = new RequestParams();
        reqParam.putData("service", "query_bank_list");
        reqParam.putData("token", SDKManager.token);
        reqParam.putData("pay_attribute", QueryBankListTypeEnum.QPAY.getCode());
        HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(),
                reqParam, new VFinResponseHandler<BankCardListModel>(BankCardListModel.class) {

                    @Override
                    public void onSuccess(BankCardListModel responseBean, String responseString) {
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
        HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getAcquirerDoUri(),
                reqParam, new VFinResponseHandler<ChannelListModel>(ChannelListModel.class) {

                    @Override
                    public void onSuccess(ChannelListModel responseBean, String responseString) {
                        hideProgress();
                        for (ChannelModel channelModel : responseBean.channelList) {
                            PaySelectModel paySelectModel = new PaySelectModel(SelectPayAdapter
                                    .PAY_TYPE,
                                    channelModel);
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


    @Override
    public void onClick(View arg0) {
        if (arg0 == layout_load_more) {
            if (mAdapter != null) {
                mAdapter.setShowAll();
            }
        }
    }

    private void nextBtnClick() {
        PaySelectModel bean = listPaySel.get(checkPosition);
        if (bean.mType == SelectPayAdapter.BANK_TYPE) {
            currentChannel = QpayChannel.getChannel();
            bankCardId = bean.getBankCardModel().getBankcardId();
        } else {
            currentChannel = bean.getChannelModel();
            bankCardId = null;
        }
        BaseChannel baseChannel = getCurrentPayment();
        if(baseChannel==null){
            showShortToast("渠道不存在");
            return;
        }
        baseChannel.doTranferToAccount(mTransferContext);
    }


    private BaseChannel getCurrentPayment() {
        BaseChannel channel = ChannelMaps.getInstance().getChannel(currentChannel);
        if(channel==null){
            return null;
        }
        channel.setChannelPara(this, new BaseChannel.ChannelResponseHandler() {
                    @Override
                    public void OnSuccess(String innerOrderNo) {
                        creatQueryTrade(mTransferContext.getOutTradeNumber());
                    }
                }, false,
                null, bankCardId);
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


    private void backOnClick() {
        this.finishActivity();
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
        mQueryTradeRepeat = new QueryTradeRepeat(this, QueryTrade.MAX_COUNT,outNo,mTransferContext.getToken());
        mQueryTradeRepeat.setHandler(new QueryTradeRepeat.QueryTradeHandler() {
            @Override
            public void callBackSuccess() {
                hideProgress();
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("context", mTransferContext);
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
                bundle.putSerializable("context", mTransferContext);
                bundle.putString("statue",TradeResultActivity.PROCESS);
                intent.putExtras(bundle);
                intent.setClass(mContext, TradeResultActivity.class);
                finishAll();
                startActivity(intent);
            }

            @Override
            public void putData(RequestParams reqParam) {
                reqParam.putData("trade_type", TradeTypeEnum.TRANSFER.toString());
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
            mTransferContext.sendMessage(result);
        }
        finishAll();
    }
}
