package com.vfinworks.vfsdk.activity.core.channel;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.common.Config;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.SHA256Encrypt;
import com.vfinworks.vfsdk.context.BaseContext;
import com.vfinworks.vfsdk.context.PaymentContext;
import com.vfinworks.vfsdk.context.RechargeContext;
import com.vfinworks.vfsdk.context.TransferContext;
import com.vfinworks.vfsdk.enumtype.VFCallBackEnum;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.interfacemanager.SignInterface;
import com.vfinworks.vfsdk.interfacemanager.SignInterfaceManager;
import com.vfinworks.vfsdk.model.ChannelModel;
import com.vfinworks.vfsdk.model.QpayNewBankCardModel;
import com.vfinworks.vfsdk.model.VFSDKResultModel;
import com.vfinworks.vfsdk.view.paypwd.PayPwdDialog;
import com.vfinworks.vfsdk.view.paypwd.PayPwdView;
import com.vfinworks.vfsdk.view.paypwd.VFEncryptData;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tangyijian on 2016/5/13.
 */
public abstract class BaseChannel {
    protected boolean isNewCard;
    protected QpayNewBankCardModel newBank;
    protected String amount;//余额支付使用
    protected String bankCardId;
    protected BaseActivity mContext;

    protected ChannelResponseHandler channelResponseHandler;
    protected DoCallBackHandler doCallBackHandler;
    protected BaseContext currentContext;
    protected PaymentContext mPaymentContext;
    protected RechargeContext mRechargeContext;
    protected TransferContext mTransferContext;

    private boolean callBackFlag;

    public BaseChannel(){

    }

    public void clear(){
        mContext = null;
        channelResponseHandler = null;
    }

    public void setChannelPara(BaseActivity context, ChannelResponseHandler
            channelResponseHandler, boolean isNewCard, QpayNewBankCardModel newBank,
                               String  bankCardId){
        callBackFlag =false;
        mContext=context;
        this.channelResponseHandler=channelResponseHandler;
        this.isNewCard=isNewCard;
        this.newBank=newBank;
        this.bankCardId=bankCardId;
    }

    public BaseChannel setDoCallBackHandler(DoCallBackHandler doCallBackHandler){
        this.doCallBackHandler=doCallBackHandler;
        callBackFlag=true;
        return this;
    }

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what== SignInterface.SIGNOK){
                httpDoPay();
            }
        }
    };
    public void parentDopay(PaymentContext paymentContext){
        mPaymentContext=paymentContext;
        String source = "amount=" + mPaymentContext.getOrderAmount() + "&outer_trade_no_list=" +
                mPaymentContext.getOrderNums();
        SignInterface signInterface = SignInterfaceManager.getInstance().getSignInterface();
        if (signInterface != null) {
            signInterface.sign(mContext,mPaymentContext, source,mHandler);
            mPaymentContext.setSignFlag(false);
            return;
        }
        if (mPaymentContext.isSignFlag()) {
            //需要内部验签
            RequestParams reqParam = new RequestParams(mPaymentContext);
            reqParam.putData("token",SDKManager.token);
            reqParam.putData("alg_type", "RSA");
            reqParam.putData("biz_type", "sign");
            reqParam.putData("service", "sign");
            reqParam.putData("key", Config.getInstance().getKey());
            reqParam.putData("source", source);

            HttpUtils.getInstance(mContext).excuteHttpRequest(HttpRequsetUri.getInstance()
                    .getAcquirerDoUri(), reqParam, new VFinResponseHandler() {
                @Override
                public void onSuccess(Object responseBean, String responseString) {
                    try {
                        JSONObject jsonObject = new JSONObject(responseString);
                        if (jsonObject.getString("is_success").equalsIgnoreCase("T")) {
                            mPaymentContext.setSign(jsonObject.getString("result_message"));
                            mPaymentContext.setSignType("RSA");
                            mHandler.obtainMessage(SignInterface.SIGNOK, mPaymentContext)
                                    .sendToTarget();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("json", "json非法:" + responseString);
                    }
                }
                @Override
                public void onError(String statusCode, String errorMsg) {
                    if (SDKManager.getInstance().getCallbackHandler() != null) {
                        VFSDKResultModel result = new VFSDKResultModel();
                        result.setResultCode(statusCode);
                        result.setMessage(errorMsg);
                        mPaymentContext.sendMessage(result);
                    }
                }

            }, this);
        } else {
            mHandler.obtainMessage(SignInterface.SIGNOK, mPaymentContext).sendToTarget();
        }
    }
    public void doPay(PaymentContext paymentContext) {
      parentDopay(paymentContext);
    }

    private void httpDoPay() {
        mContext.showProgress();
        networkdoPay(new VFinResponseHandler() {

            @Override
            public void onSuccess(Object responseBean, String responseString) {
//                mContext.hideProgress();
                if (callBackFlag && doCallBackHandler != null) {
                    doCallBackHandler.goPay(responseBean, responseString);
                } else {
                    JSONObject json;
                    try {
                        json = new JSONObject(responseString);
                        String isSuccess = json.getString("is_success");
                        if (isSuccess.equalsIgnoreCase("T")) {
                            String status = json.getString("pay_status");
                            String stringResult = "";
                            if (!json.isNull("pay_result")) {
                                stringResult = json.getString("pay_result");
                            }
                            if (!json.isNull("inner_payment_no")) {
                                mPaymentContext.setInnerOrderNo(json.getString("inner_payment_no"));
                            }
                            goPays(status, stringResult, json);
                        } else {
                            mContext.showShortToast("支付失败！");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        mContext.showShortToast(e.getMessage());
                    }
                }

            }

            @Override
            public void onError(String statusCode, String errorMsg) {
                mContext.hideProgress();
                mContext.showShortToast(errorMsg);
            }

        });
    }

    protected void networkdoPay(VFinResponseHandler vfinResponseHand) {
        RequestParams reqParam = new RequestParams(mPaymentContext);
        reqParam.putData("service", "create_pay");
        reqParam.putData("token", SDKManager.token);
        reqParam.putData("request_no", mPaymentContext.getRequestNo());
        reqParam.putData("outer_trade_no_list", mPaymentContext.getOrderNums());
//        reqParam.putData("access_channel", "WEB");
        reqParam.putData("channel", "SDK");

        initRequestParams(reqParam, mPaymentContext.getOrderAmount());
        HttpUtils.getInstance(mContext).excuteHttpRequest(HttpRequsetUri.getInstance()
                .getAcquirerDoUri(), reqParam, vfinResponseHand, mContext);
    }

    protected void networkRecharge(VFinResponseHandler vfinResponseHand) {
        RequestParams reqParam = new RequestParams(mRechargeContext);
        reqParam.putData("service", "do_deposit");
        reqParam.putData("token", SDKManager.token);
        reqParam.putData("outer_trade_no", mRechargeContext.getOutTradeNumber());
        reqParam.putData("amount", mRechargeContext.getAmount());
        reqParam.putData("operator_id", "");
        reqParam.putData("notify_url", mRechargeContext.getNotifyUrl());
        initRechargeRequestParams(reqParam, mRechargeContext.getAmount());
        HttpUtils.getInstance(mContext).excuteHttpRequest(HttpRequsetUri.getInstance()
                .getAcquirerDoUri(), reqParam, vfinResponseHand, mContext);
    }

    protected void networkTranferToAccount(VFinResponseHandler vfinResponseHand){
        RequestParams reqParam = new RequestParams(mTransferContext);
        reqParam.putData("service", "balance_transfer");
        reqParam.putData("token", SDKManager.token);
        //        reqParam.putData("pay_pwd", SHA256Encrypt.bin2hex("111111"));
        reqParam.putData("pay_pwd", "111111");
        reqParam.putData("outer_trade_no", mTransferContext.getOutTradeNumber());
        reqParam.putData("amount", mTransferContext.getAmount());
        reqParam.putData("fundin_identity_no", mTransferContext.getAccountNum());
        String strIdentityType = "MOBILE";
        if (mTransferContext.getAccountNum().contains("@") == true) {
            strIdentityType = "EMAIL";
        }
        reqParam.putData("fundin_identity_type", strIdentityType);
        reqParam.putData("notify_url", mTransferContext.getNotifyUrl());
        if(!TextUtils.isEmpty(mTransferContext.getMemo())){
            reqParam.putData("memo", mTransferContext.getMemo());
        }
        initRequestParams(reqParam, mTransferContext.getAmount());
        HttpUtils.getInstance(mContext).excuteHttpRequest(HttpRequsetUri.getInstance()
                .getAcquirerDoUri(), reqParam, vfinResponseHand, mContext);
    }

    protected void goPays(String status, String stringResult, JSONObject json){
        if(status.equalsIgnoreCase("S") || status.equalsIgnoreCase("P")){
                goPay(status, stringResult, json);
        }else {
            mContext.showShortToast(stringResult);
        }
    }
    public void parentDoRecharge(RechargeContext rechargeContext) {
        mRechargeContext=rechargeContext;
        mContext.showProgress();
        networkRecharge(new VFinResponseHandler() {

            @Override
            public void onSuccess(Object responseBean, String responseString) {
//                mContext.hideProgress();
                if (callBackFlag && doCallBackHandler != null) {
                    doCallBackHandler.goRecharge(responseBean, responseString);
                } else {
                    JSONObject json;
                    try {
                        json = new JSONObject(responseString);
                        String isSuccess = json.getString("is_success");
                        if (isSuccess.equalsIgnoreCase("T")) {
                            String status = json.getString("pay_status");
                            String stringResult = "";
                            if (!json.isNull("pay_result")) {
                                stringResult = json.getString("pay_result");
                            }
                            if (!json.isNull("inner_payment_no")) {
                                mRechargeContext.setInnerOrderNo(json.getString
                                        ("inner_payment_no"));
                            }
                            goRecharge(status, stringResult, json);
                        } else {
                            mContext.showShortToast("充值失败！");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        mContext.showShortToast(e.getMessage());
                    }
                }

            }

            @Override
            public void onError(String statusCode, String errorMsg) {
                mContext.hideProgress();
                mContext.showShortToast(errorMsg);
            }

        });
    }
    public void doRecharge(RechargeContext rechargeContext) {
        parentDoRecharge(rechargeContext);
    }

    public void parentDoTranferToAccount(TransferContext transferContext){
        mTransferContext=transferContext;
        mContext.showProgress();
        networkTranferToAccount(new VFinResponseHandler() {

            @Override
            public void onSuccess(Object responseBean, String responseString) {
//                mContext.hideProgress();
                if (callBackFlag && doCallBackHandler != null) {
                    doCallBackHandler.goTransferToAccount(responseBean, responseString);
                } else {
                    JSONObject json;
                    try {
                        json = new JSONObject(responseString);
                        String isSuccess = json.getString("is_success");
                        if (isSuccess.equalsIgnoreCase("T")) {
                            String status = json.getString("pay_status");
                            String stringResult = "";
                            if (!json.isNull("pay_result")) {
                                stringResult = json.getString("pay_result");
                            }
                            if (!json.isNull("inner_payment_no")) {
                                mTransferContext.setInnerOrderNo(json.getString
                                        ("inner_payment_no"));
                            }

                            goTransferToAccount(status, stringResult, json);
                        } else {
                            mContext.showShortToast("转账失败！");
                        }
                    } catch (JSONException e) {
                        if (SDKManager.getInstance().getCallbackHandler() != null) {
                            VFSDKResultModel result = new VFSDKResultModel();
                            result.setResultCode(VFCallBackEnum.ERROR_CODE_BUSINESS.getCode());
                            result.setMessage(e.getMessage());
                            mTransferContext.sendMessage(result);
                        }
                        mContext.finishActivity();
                    }
                }

            }

            @Override
            public void onError(String statusCode, String errorMsg) {
                mContext.hideProgress();
                if (SDKManager.getInstance().getCallbackHandler() != null) {
                    VFSDKResultModel result = new VFSDKResultModel();
                    result.setResultCode(VFCallBackEnum.ERROR_CODE_BUSINESS.getCode());
                    result.setMessage(errorMsg);
                    mTransferContext.sendMessage(result);
                }
                mContext.finishActivity();
            }

        });
    }

    public void doTranferToAccount(TransferContext transferContext) {
        parentDoTranferToAccount(transferContext);
    }

    protected void checkPayPassword(final String token, final String outTradeNo, String money,
                                    final PayPasswordHandler payPasswordHandler) {
        final PayPwdDialog mDialog = new PayPwdDialog(mContext,mPaymentContext);
        mDialog.setPayMoney(money);
        mDialog.setOnStatusChangeListener(new PayPwdView.OnStatusChangeListener() {
            @Override
            public void onInputComplete(VFEncryptData result) {
                withdrawPro(token,outTradeNo,result.getCiphertext(), payPasswordHandler);
                mDialog.dismiss();
            }

            @Override
            public void onUserCanel() {
                mDialog.dismiss();
            }

        });
        mDialog.show();
    }


    /**
     * 验证支付密码，若成功就继续支付交易
     *
     * @param payPwd
     */
    private void withdrawPro(String token,String outTradeNo,String payPwd, final PayPasswordHandler payPasswordHandler) {
        mContext.showProgress();
        RequestParams reqParam = new RequestParams();
        reqParam.putData("service", "verify_paypwd");
        reqParam.putData("token", SDKManager.token);
        reqParam.putData("out_trade_no", outTradeNo);
        reqParam.putData("pay_pwd", SHA256Encrypt.bin2hex(payPwd));

        HttpUtils.getInstance(mContext).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(),
                reqParam, new VFinResponseHandler() {

                    @Override
                    public void onSuccess(Object responseBean, String responseString) {
                        mContext.hideProgress();
                        JSONObject json;
                        try {
                            json = new JSONObject(responseString);
                            String isSuccess = json.getString("is_success");
                            //T代表验证成功
                            if (isSuccess.equalsIgnoreCase("T")) {
                                payPasswordHandler.OnSuccess();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            mContext.showShortToast(e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String statusCode, String errorMsg) {
                        mContext.hideProgress();
                        mContext.showShortToast(errorMsg);
                        payPasswordHandler.OnError(errorMsg);
                    }
                }, this);
    }

    public abstract void setDrawableIcon(ImageView iv_icon);

    public void setAmount(String amount) {
        this.amount = amount;
    }


    public interface ChannelResponseHandler{
        public void OnSuccess(String innerOrderNo);
    }

    public interface PayPasswordHandler{
        void OnSuccess();
        void OnError(String errorMsg);
    }
    public interface DoCallBackHandler{
        void goPay(Object responseBean, String responseString);
        void goRecharge(Object responseBean, String responseString);
        void goTransferToAccount(Object responseBean, String responseString);
    }

    protected abstract void initRequestParams(RequestParams reqParam,String amount);
    protected abstract void initRechargeRequestParams(RequestParams reqParam, String amount);
    protected abstract void goPay(String status, String payResult, JSONObject jsonObject);
    protected abstract void goRecharge(String status, String stringResult, JSONObject json);
    protected abstract void goTransferToAccount(String status, String stringResult, JSONObject json);

    /**
     * 获取渠道名称
      * @param channel
     */
    public abstract String getName(ChannelModel channel);
    /**
     * 获取渠道
     */
    public abstract ChannelModel getChannelModel();
}
