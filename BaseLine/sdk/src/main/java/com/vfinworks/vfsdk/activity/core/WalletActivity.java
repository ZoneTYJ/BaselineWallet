package com.vfinworks.vfsdk.activity.core;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.activity.PeopleInfo.UserInfoActivity;
import com.vfinworks.vfsdk.activity.core.adapter.ActionAdapter;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.Utils;
import com.vfinworks.vfsdk.context.BaseContext;
import com.vfinworks.vfsdk.context.BillContext;
import com.vfinworks.vfsdk.context.PaymentContext;
import com.vfinworks.vfsdk.context.RechargeContext;
import com.vfinworks.vfsdk.context.TransferContext;
import com.vfinworks.vfsdk.context.WithdrawContext;
import com.vfinworks.vfsdk.enumtype.VFCallBackEnum;
import com.vfinworks.vfsdk.enumtype.VFQueryBankTypeEnum;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.model.ActionModel;
import com.vfinworks.vfsdk.model.CertificationModel;
import com.vfinworks.vfsdk.model.MemberModel;
import com.vfinworks.vfsdk.model.UserInfoModel;
import com.vfinworks.vfsdk.model.VFSDKResultModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class WalletActivity extends BaseActivity implements OnClickListener {
    private static final String TAG = WalletActivity.class.getSimpleName();
    public static final String MONEY_CHANGE = "money_change";

    private LinearLayout lyCheques;

    public static int paycallbackMessageID = 1;
    public static int transferCallbackMessageID = 3;
    public static int rechargeCallbackMessageID = 4;
    public static int queryRealNameInfoMessageID = 5;
    public static int realNameInfoMessageID = 6;

    private String orderNum;
    private String orderAmount;
    private static final int PLACE_ORDER_REQUSET = 101;

    /**
     * 余额
     */
    public String availableBalance;

    private TextView tvBalanceBalance, tv_transcation_account,tv_card_count;

    private LinearLayout lyCard;
    private LinearLayout layout_scanning;
    private GridView gv_action_list;

    private BaseActivity mContext = this;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == transferCallbackMessageID) {
                handleTransferCallback((VFSDKResultModel) msg.obj);
            } else if (msg.what == rechargeCallbackMessageID) {
                handleRechargeCallback((VFSDKResultModel) msg.obj);
            } else if (msg.what == paycallbackMessageID) {
                handlePayCallback((VFSDKResultModel) msg.obj);
            } else if (msg.what == queryRealNameInfoMessageID) {
                handleQueryRealNameCallback((VFSDKResultModel) msg.obj);
            } else if (msg.what == realNameInfoMessageID) {
                handleRealNameCallback((VFSDKResultModel) msg.obj);
            }
        }

        ;
    };
    private String token;
    private String mNickname;
    private String mobile;
    private RelativeLayout layout_red;
    private RelativeLayout layout_ticket;

    private RelativeLayout rlQrcodeScan;

    private void handleRealNameCallback(VFSDKResultModel resultModel) {
        if (resultModel.getResultCode().equalsIgnoreCase(VFCallBackEnum.OK.getCode())) {
            Toast.makeText(mContext, "认证成功", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mContext, resultModel.getMessage(), Toast
                    .LENGTH_SHORT).show();
        }
    }
    private void handleRechargeCallback(VFSDKResultModel resultModel) {
        if (resultModel.getResultCode().equalsIgnoreCase(VFCallBackEnum.OK.getCode())) {
            Toast.makeText(mContext, resultModel.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mContext, resultModel.getMessage(), Toast
                    .LENGTH_SHORT).show();
        }
//        getData();
    }

    private void handleQueryRealNameCallback(VFSDKResultModel resultModel) {
        if (resultModel.getResultCode().equalsIgnoreCase(VFCallBackEnum.OK.getCode())) {
            Toast.makeText(mContext, resultModel.getJsonData(), Toast
                    .LENGTH_LONG).show();
        } else {
            Toast.makeText(mContext, resultModel.getMessage(), Toast
                    .LENGTH_SHORT).show();
        }
    }

    private void handlePayCallback(VFSDKResultModel resultModel) {
        if (resultModel.getResultCode().equalsIgnoreCase(VFCallBackEnum.OK.getCode())) {
            Toast.makeText(mContext, resultModel.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mContext, resultModel.getMessage(), Toast.LENGTH_SHORT).show();
        }
        //重新刷新界面
//        getData();
    }

    private void handleTransferCallback(VFSDKResultModel resultModel) {
        if (resultModel.getResultCode().equalsIgnoreCase(VFCallBackEnum.OK.getCode()) == true) {
            Toast.makeText(mContext, resultModel.getMessage(), Toast
                    .LENGTH_LONG).show();
        } else {
            Toast.makeText(mContext, resultModel.getMessage(), Toast
                    .LENGTH_SHORT).show();
        }
    }

    private ActionAdapter actionAdapter;
    private ArrayList<ActionModel> actionModels = new ArrayList<>();
    private HashMap<String,ActionModel> actionMaps = new HashMap<>();

    {
        actionMaps.put("recharge",new ActionModel("recharge","充值",R.drawable.vf_rechargeable));
        actionMaps.put("withdraw",new ActionModel("withdraw","提现",R.drawable.vf_card_icon02));
        actionMaps.put("transfer",new ActionModel("transfer","转账",R.drawable.vf_transfer));
        actionMaps.put("transactionlist",new ActionModel("transactionlist","交易明细",R.drawable.vf_trade_list));
        actionMaps.put("paycode",new ActionModel("paycode","付款",R.drawable.vf_com_pay));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setIsPush(false);
        super.onCreate(savedInstanceState);
//        L.e("Wallet-oncreate", SystemClock.currentThreadTimeMillis()+"");
        initView();
        getNameData();
    }

    private void initView() {
        setContentView(R.layout.activity_wallet);
        layout_red = (RelativeLayout) findViewById(R.id.layout_red);
        layout_red.setOnClickListener(this);
        layout_ticket = (RelativeLayout) findViewById(R.id.layout_ticket);
        layout_ticket.setOnClickListener(this);

        tv_card_count = (TextView) findViewById(R.id.tv_card_count);
        tv_transcation_account = (TextView) findViewById(R.id.tv_transcation_account);
        tv_transcation_account.setOnClickListener(this);

        gv_action_list = (GridView) findViewById(R.id.gv_action_list);
        tvBalanceBalance = (TextView) findViewById(R.id.tv_balance_balance);
        tvBalanceBalance.setText(availableBalance);
        lyCheques = (LinearLayout) findViewById(R.id.layout_cheques);
        lyCheques.setOnClickListener(this);
        lyCard = (LinearLayout) findViewById(R.id.layout_card);
        lyCard.setOnClickListener(this);
        layout_scanning = (LinearLayout) findViewById(R.id.layout_scanning);
        layout_scanning.setOnClickListener(this);

        gv_action_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ActionModel item = actionAdapter.getItem(position);
                if("recharge".equals(item.operationName)){
                    rechargeClick();
                }else if("withdraw".equals(item.operationName)){
                    withdrawClick();
                }else if("transfer".equals(item.operationName)){
                    transferClick();
                }else if("transactionlist".equals(item.operationName)){
                    billClick();
                }else if("paycode".equals(item.operationName)){
                    paycodeClick();
                }
            }
        });

        rlQrcodeScan = (RelativeLayout) findViewById(R.id.rl_qrcode_scan);
        rlQrcodeScan.setOnClickListener(this);
    }

    private void placeCodeGatheringClick() {
        // FIXME:修改卖家的手机号
        String phone = mobile;
        SDKManager.getInstance().QRCodeGathering(mContext, phone, mHandler);

    }

    private void placeCodePaymentClick() {
        SDKManager.getInstance().QRScanPayment(mContext, tv_transcation_account.getText().toString().trim(),
                mHandler);
        //        SDKManager.getInstance().QRCodePayment(mContext, token, mNickname,
        //                mHandler, null);
    }

    public void getData() {
        //		showProgress();
        RequestParams reqParam = new RequestParams();
        reqParam.putData("service", "query_member");
        reqParam.putData("token", SDKManager.token);
        HttpUtils.getInstance(mContext).excuteHttpRequest(HttpRequsetUri.getInstance()
                        .getMemberDoUri(),
                reqParam, new VFinResponseHandler<MemberModel>(MemberModel.class) {
                    @Override
                    public void onSuccess(MemberModel responseBean, String responseString) {
                        hideProgress();
                        if(actionModels.size() == 0) {
                            for (String s : responseBean.getCommon_operation()) {
                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = new JSONObject(s);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                String operation_name = jsonObject.optString("operation_name");
                                actionModels.add(actionMaps.get(operation_name));
                            }
                            actionAdapter = new ActionAdapter(actionModels);
                            gv_action_list.setAdapter(actionAdapter);
                        }
                        updateUI(responseBean);
                    }

                    @Override
                    public void onError(String statusCode, String errorMsg) {
                        hideProgress();
                        mContext.showShortToast(errorMsg);
                    }
                }, this);

    }


    private void updateUI(MemberModel responseBean) {
        tv_card_count.setText(responseBean.cardNumber);
        tv_transcation_account.setText(responseBean.getNick_name());
        tvBalanceBalance.setText(responseBean.getAvaliable_balance());
        availableBalance = responseBean.getAvaliable_balance();
        mNickname = responseBean.getNick_name();
        mobile = responseBean.getMobile_star();

        UserInfoModel userInfoModel = new UserInfoModel();
        userInfoModel.token = token;
        userInfoModel.nickname = mNickname;
        SDKManager.getInstance().setUserInfoModel(userInfoModel);
    }

    @Override
    public void onClick(View arg0) {
        if (arg0.getId() == R.id.layout_cheques) {
            //收款
            placeCodeGatheringClick();
        } else if (arg0.getId() == R.id.layout_scanning) {
            //扫一扫
            //跳转到账号信息
            placeCodePaymentClick();
        } else if (arg0.getId() == R.id.tv_transcation_account) {
            Intent intent = new Intent(mContext, UserInfoActivity.class);
            intent.putExtra("certificationModel",certificationModel);
            intent.putExtra("token",token);
            startActivity(intent);
        } else if (arg0 == lyCard) {
            myBankClick();
            //queryRealNameInfo();
        } else if (arg0 == layout_red) {
            placeOrderClick();
        }else if(arg0 ==layout_ticket){

        }else if(arg0==rlQrcodeScan){
            SDKManager.getInstance().QRScanGathering(this,mobile,mHandler,null);
        }
    }

    private void paycodeClick(){
        SDKManager.getInstance().QRCodePayment(this,mobile,mHandler,null);
    }

    private void billClick() {
        BillContext billContext = new BillContext();
        billContext.setToken(token);
        billContext.setMobile(mobile);
        Bundle bundle = new Bundle();
        bundle.putSerializable("context", billContext);
        Intent intent = new Intent(mContext, BillActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void queryRealNameInfo() {
        BaseContext baseContext = new BaseContext();
        baseContext.setToken(token);
        baseContext.setCallBackMessageId(queryRealNameInfoMessageID);
        SDKManager.getInstance().QueryRealNameInfo(mContext, baseContext,
                mHandler);
    }


    private void myBankClick() {
        SDKManager.getInstance().QueryMyBankCard(mContext, token, mobile, VFQueryBankTypeEnum
                .ALL, null);
    }

    private void placeOrderClick() {
//        Intent intent = new Intent();
//        Bundle bundle = new Bundle();
//        bundle.putString("token", token);
//        intent.putExtras(bundle);
//        intent.setClass(mContext, PlaceOrderPayActivity.class);
//        this.startActivity(intent);

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("token", token);
        intent.putExtras(bundle);
        intent.setClass(mContext, PlaceOrderActivity.class);
        this.startActivityForResult(intent, PLACE_ORDER_REQUSET);

        /*PlaceOrderContext placeOrderContext = new PlaceOrderContext();
        placeOrderContext.setToken(token);
		placeOrderContext.setCallBackMessageId(placeOrdercallbackMessageID);
		placeOrderContext.setRequestNo(String.valueOf(Math.round(Math.random()*10000000)));
		String tradeNo = String.valueOf(Math.round(Math.random()*10000000));
		orderNum = tradeNo;
		orderAmount = "0.01";
		//即时到账ISNTANT、担保交易ENSURE
		//placeOrderContext.setTradeType(VFOrderTypeEnum.TRADE_ENSURE);
		//placeOrderContext.setTradeList("[{\"out_trade_no\":\""+ tradeNo + "\",
		\"subject\":\"牛奶\",\"total_amount\":\"0.01\",\"ensure_amount\":\"1\",
		\"seller\":\"13621722085\",\"seller_type\":\"MOBILE\",\"price\":\"0.01\",
		\"quantity\":1}]");
		placeOrderContext.setTradeType(VFOrderTypeEnum.TRADE_INSTANT);
		placeOrderContext.setTradeList("[{\"out_trade_no\":\""+ tradeNo + "\",\"subject\":\"牛奶\",
		\"total_amount\":\"0.01\",\"seller\":\"13621722085\",\"seller_type\":\"MOBILE\",
		\"price\":\"0.01\",\"quantity\":1}]");
		//placeOrderContext.setPayMethod("online");
		this.showProgress();
		SDKManager.getInstance().PlaceOrder(this,placeOrderContext,mHandler);*/
    }

    private void rechargeClick() {
        RechargeContext rechargeContext = new RechargeContext();
        rechargeContext.setToken(token);
        rechargeContext.setCallBackMessageId(rechargeCallbackMessageID);
        rechargeContext.setOutTradeNumber(Utils.getOnlyValue());
        rechargeContext.setMobile(mobile);
        rechargeContext.setNotifyUrl("");
        SDKManager.getInstance().Recharge(mContext, rechargeContext, mHandler);
    }

    private void transferClick() {
        TransferContext transferContext = new TransferContext();
        transferContext.setMobile(mobile);
        transferContext.setToken(token);
        transferContext.setCallBackMessageId(transferCallbackMessageID);
        transferContext.setAvailableAmount(availableBalance);
        transferContext.setOutTradeNumber(Utils.getOnlyValue());
        transferContext.setNotifyUrl("");
        SDKManager.getInstance().Transfer(mContext, transferContext, mHandler);
    }

    private void withdrawClick() {
        Log.d(TAG, token);
        WithdrawContext withdrawContext = new WithdrawContext();
        withdrawContext.setToken(token);
        withdrawContext.setMobile(mobile);
        withdrawContext.setAvailableAmount(availableBalance);
        withdrawContext.setOutTradeNumber(Utils.getOnlyValue());
        withdrawContext.setNotifyUrl("http://10.5.20.4:8080/appserver-web-core/ok");
        SDKManager.getInstance().Withdraw(mContext, withdrawContext, null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == mContext.RESULT_OK && requestCode == PLACE_ORDER_REQUSET) {
            orderNum = data.getExtras().getString("orderNum");
            orderAmount = data.getExtras().getString("orderAmount");
            PaymentContext paymentContext = new PaymentContext();
            paymentContext.setSignFlag(true);
            paymentContext.setToken(token);
            paymentContext.setCallBackMessageId(paycallbackMessageID);
            paymentContext.setRequestNo(Utils.getOnlyValue());
            paymentContext.setOrderNums(orderNum);
            paymentContext.setOrderInfo("购买地球");
            paymentContext.setOrderAmount(orderAmount);
            SDKManager.getInstance().Payment(mContext, paymentContext, mHandler);
        }
    }


    @Override
    protected void onDestroy() {
        mHandler = null;
        mContext = null;
        SDKManager.getInstance().clear();
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        token = SDKManager.token;
        showProgress();
        getData();
    }

    private CertificationModel certificationModel;
    private void getNameData(){
        RequestParams reqParam2 = new RequestParams();
        reqParam2.putData("service", "query_certification");
        reqParam2.putData("token", SDKManager.token);
        HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(), reqParam2, new VFinResponseHandler<CertificationModel>(CertificationModel.class){

            @Override
            public void onSuccess(CertificationModel responseBean, String responseString) {
                hideProgress();
                certificationModel = responseBean;
            }

            @Override
            public void onError(String statusCode, String errorMsg) {
                hideProgress();
                showShortToast(errorMsg);
            }

        }, this);
    }

}
