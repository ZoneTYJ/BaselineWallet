package com.vfinworks.vfsdk.activity.core;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.activity.core.adapter.SelectPayAdapter;
import com.vfinworks.vfsdk.activity.core.channel.BaseChannel;
import com.vfinworks.vfsdk.activity.core.channel.ChannelMaps;
import com.vfinworks.vfsdk.activity.core.channel.QpayChannel;
import com.vfinworks.vfsdk.business.QueryTrade;
import com.vfinworks.vfsdk.business.QueryTradeRepeat;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.NoDoubleClickListener;
import com.vfinworks.vfsdk.common.Utils;
import com.vfinworks.vfsdk.context.LimitContext;
import com.vfinworks.vfsdk.context.RechargeContext;
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

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * 充值
 */
public class RechargeActivity extends BaseActivity implements OnClickListener {
    private BaseActivity mContext = this;

    private EditText etMoney;
    private Button btnNext;

    private boolean isNewCard = false;
    private QpayNewBankCardModel newBank = null;

    private RechargeContext rechargeContext;
    private ChannelModel currentChannel;
    private int currentRechargeType;

    private View layout_load_more;
    private int checkPosition;
    private ListView lv_list;
    private ArrayList<PaySelectModel> listPaySel;
    private SelectPayAdapter mAdapter;
    private String bankCardId;
    private boolean turnInFlag;

    private QueryTradeRepeat mQueryTradeRepeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.recharge_quick_activity, FLAG_TITLE_LINEARLAYOUT);
        rechargeContext = (RechargeContext) this.getIntent().getExtras().getSerializable("context");
        super.onCreate(savedInstanceState);
        this.getTitlebarView().setTitle("充值");
        this.getTitlebarView().initLeft(new OnClickListener() {
            @Override
            public void onClick(View v) {
                backOnClick();
            }
        });
        getTitlebarView().initRight("限额说明", new OnClickListener() {
            @Override
            public void onClick(View v) {
                LimitContext limitContext = new LimitContext(rechargeContext);
                Intent intent = new Intent(mContext, LimitActivity.class);
                intent.putExtra("context", limitContext);
                startActivity(intent);
            }
        });
    }

    @Override
    public void initWidget() {
        etMoney = (EditText) findViewById(R.id.et_money);
        btnNext = (Button) findViewById(R.id.btn_recharge);
        btnNext.setOnClickListener(new NoDoubleClickListener() {
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
        initEdit();
        getBankCardLst();
        queryMember();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void initEdit() {
        etMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnNext.setEnabled(etMoney.getText().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String charText = s.toString();
                // 第一位不允许输入空格
                if (charText != null && charText.startsWith(" ")
                        || charText.startsWith(".")) {
                    s.delete(0, 1);
                    return;
                }
                // 最后一位不允许输入空格 并且限制长度为12位
                if ((charText != null && charText.endsWith(" ")) || (charText.length() > 12)) {
                    s.delete(charText.length() - 1, charText.length());
                    return;
                }
                // 如果输入的是数字,只允许输入小数点后两位
                int posDot = charText.indexOf(".");
                if (posDot <= 0)
                    return;
                if (charText.length() - posDot - 1 > 2) {
                    s.delete(posDot + 3, posDot + 4);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {}
        });
    }


    private void queryMember() {
        RequestParams reqParam = new RequestParams();
        reqParam.putData("service", "query_member");
        reqParam.putData("token", SDKManager.token);
        HttpUtils.getInstance(mContext).excuteHttpRequest(HttpRequsetUri.getInstance()
                        .getMemberDoUri(),
                reqParam, new VFinResponseHandler<UserDetailEntity>(UserDetailEntity.class) {
                    @Override
                    public void onSuccess(UserDetailEntity responseBean, String responseString) {
                        hideProgress();
                        turnInFlag = responseBean.isTurnIn();
                    }

                    @Override
                    public void onError(String statusCode, String errorMsg) {
                        hideProgress();
                        mContext.showShortToast(errorMsg);
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


    private void getBankCardLst() {
        listPaySel.clear();
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


    private void btnAddQpayCardClick() {
        if (checkParams()) {
            rechargeContext.setAmount(etMoney.getText().toString().trim());
            Intent intent = new Intent();
            intent.putExtra("basecontext", rechargeContext);
            intent.setClass(this, AddQpayInputAccoutActivity.class);
            startActivity(intent);
        }

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
        if (checkParams()) {
            rechargeContext.setAmount(Utils.getInstance().formatMoney(etMoney.getText().toString
                    ()));
            if(listPaySel.size()==0){
                showShortToast("没有可用渠道");
                return;
            }
            PaySelectModel bean = listPaySel.get(checkPosition);
            if (bean.mType == SelectPayAdapter.BANK_TYPE) {
                bankCardId = bean.getBankCardModel().getBankcardId();
                currentChannel = QpayChannel.getChannel();
            } else {
                if (bean.getChannelModel().equals(QpayChannel.getChannel())){
                    showShortToast("请选择正确支付方式");
                    return;
                }
                bankCardId = null;
                currentChannel = bean.getChannelModel();
            }
            BaseChannel baseChannel = getCurrentPayment();
            if (baseChannel == null) {
                showShortToast("渠道不存在");
                return;
            }
            baseChannel.doRecharge(rechargeContext);
        }
    }


    private BaseChannel getCurrentPayment() {
        final BaseChannel channel = ChannelMaps.getInstance().getChannel(currentChannel);
        if (channel == null) {
            return null;
        }
        channel.setChannelPara(this, new BaseChannel.ChannelResponseHandler() {
                    @Override
                    public void OnSuccess(String innerOrderNo) {
                        creatQueryTrade(rechargeContext.getOutTradeNumber());
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
        if (!turnInFlag) {
            showShortToast("该账户已经冻结");
            return false;
        }
        if (TextUtils.isEmpty(etMoney.getText().toString())) {
            this.showShortToast("请输充值账金额！");
            return false;
        }
        String strMoney = TextUtils.isEmpty(etMoney.getText().toString()) ? "0.00" : etMoney
                .getText().toString();
        BigDecimal bdMoney = new BigDecimal(strMoney);
        if (bdMoney.compareTo(new BigDecimal("0.00")) == 0) {
            this.showShortToast("您输入的充值金额不对！");
            return false;
        }
        return true;
    }

    private void backOnClick() {
        this.finishAll();
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
        mQueryTradeRepeat = new QueryTradeRepeat(this, QueryTrade.MAX_COUNT,outNo,rechargeContext.getToken());
        mQueryTradeRepeat.setHandler(new QueryTradeRepeat.QueryTradeHandler() {
            @Override
            public void callBackSuccess() {
                hideProgress();
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("context", rechargeContext);
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
                bundle.putSerializable("context", rechargeContext);
                bundle.putString("statue",TradeResultActivity.PROCESS);
                intent.putExtras(bundle);
                intent.setClass(mContext, TradeResultActivity.class);
                finishAll();
                startActivity(intent);
            }

            @Override
            public void putData(RequestParams reqParam) {
                reqParam.putData("trade_type", TradeTypeEnum.DEPOSIT.toString());
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
            rechargeContext.sendMessage(result);
        }
        finishAll();
    }

}
