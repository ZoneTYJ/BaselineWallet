package com.vfinworks.vfsdk.business;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.core.PaymentActivity;
import com.vfinworks.vfsdk.common.Config;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.context.PaymentContext;
import com.vfinworks.vfsdk.context.PlaceOrderContext;
import com.vfinworks.vfsdk.enumtype.VFOrderTypeEnum;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.interfacemanager.SignInterface;
import com.vfinworks.vfsdk.interfacemanager.SignInterfaceManager;
import com.vfinworks.vfsdk.model.VFSDKResultModel;
import com.vfinworks.vfsdk.view.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xiaoshengke on 2016/4/18.
 */
public class OrderAndPay {
    private Context mContext;
    private PlaceOrderContext placeOrderContext;
    private PaymentContext paymentContext;
    private LoadingDialog lsLoadingDialog;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what== SignInterface.SIGNOK){
                doOrderAndPay();
            }
        }
    };

    public OrderAndPay(Context activity, PlaceOrderContext placeOrderContext) {
        this.mContext = activity;
        this.placeOrderContext = placeOrderContext;
        paymentContext = new PaymentContext();
        paymentContext.setSign(placeOrderContext.paymentSign);
        paymentContext.setSignType(placeOrderContext.getSignType());
        paymentContext.setAppExtension(placeOrderContext.getAppExtension());
        lsLoadingDialog = new LoadingDialog(activity);
    }

    public void orderAndPay(){
        doSignOrder();
    }
    private void doSignOrder() {
        String source="tradeList="+placeOrderContext.getTradeList();
        final SignInterface signInterface = SignInterfaceManager.getInstance().getSignInterface();
        if (signInterface != null) {
            signInterface.sign(mContext,placeOrderContext, source,mHandler);
            placeOrderContext.setSignFlag(false);
            return;
        }
        if (placeOrderContext.isSignFlag()) {
            //需要内部验签
            RequestParams reqParam = new RequestParams(placeOrderContext);
            reqParam.putData("token",SDKManager.token);
            reqParam.putData("alg_type", "RSA");
            reqParam.putData("biz_type", "sign");
            reqParam.putData("service", "sign");
            reqParam.putData("key", Config.getInstance().getKey());
            reqParam.putData("source",source);


            HttpUtils.getInstance(mContext).excuteHttpRequest(HttpRequsetUri.getInstance()
                    .getAcquirerDoUri(), reqParam, new VFinResponseHandler() {

                @Override
                public void onSuccess(Object responseBean, String responseString) {
                    try {
                        JSONObject jsonObject=new JSONObject(responseString);
                        if(jsonObject.getString("is_success").equalsIgnoreCase("T")){
                            placeOrderContext.setSign(jsonObject.getString("result_message"));
                            placeOrderContext.setSignType("RSA");
                            mHandler.obtainMessage(signInterface.SIGNOK,placeOrderContext).sendToTarget();
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
                        placeOrderContext.sendMessage(result);
                    }
                }

            }, this);
        } else {
            mHandler.obtainMessage(signInterface.SIGNOK,placeOrderContext).sendToTarget();
        }
    }

    private void doOrderAndPay(){
        lsLoadingDialog.show();
        RequestParams reqParam = new RequestParams(placeOrderContext);
        String service = "";
        if(placeOrderContext.getTradeType().getCode() == VFOrderTypeEnum.TRADE_INSTANT.getCode())
            service = VFOrderTypeEnum.TRADE_INSTANT.getTradeService();
        else
            service = VFOrderTypeEnum.TRADE_ENSURE.getTradeService();
        reqParam.putData("service", service);
        reqParam.putData("token", SDKManager.token);
        reqParam.putData("alg_type", "RSA");
        reqParam.putData("biz_type", "sign");
        reqParam.putData("request_no", placeOrderContext.getRequestNo());

        reqParam.putData("trade_list", placeOrderContext.getTradeList());
        if(TextUtils.isEmpty(placeOrderContext.getPayMethod()) == false)
            reqParam.putData("pay_method", placeOrderContext.getPayMethod());
        HttpUtils.getInstance(mContext).excuteHttpRequest(HttpRequsetUri.getInstance().getAcquirerDoUri(), reqParam, new VFinResponseHandler(){

            @Override
            public void onSuccess(Object responseBean, String responseString) {
                lsLoadingDialog.dismiss();
                paymentContext.setSignFlag(placeOrderContext.isSignFlag());
                paymentContext.setToken(placeOrderContext.getToken());
                paymentContext.setCallBackMessageId(placeOrderContext.getCallBackMessageId());
                paymentContext.setRequestNo(String.valueOf(Math.round(Math.random()*10000000)));
                paymentContext.setOrderNums(placeOrderContext.tradeNo);
                paymentContext.setOrderAmount(placeOrderContext.orderAmount);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("context", paymentContext);
                intent.putExtras(bundle);
                intent.setClass(mContext,PaymentActivity.class);
                mContext.startActivity(intent);
            }

            @Override
            public void onError(String statusCode, String errorMsg) {
                lsLoadingDialog.dismiss();
//                Toast toast = Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.CENTER,0,0);
//                toast.show();
                if(SDKManager.getInstance().getCallbackHandler() != null) {
                    VFSDKResultModel result = new VFSDKResultModel();
                    result.setResultCode(statusCode);
                    result.setMessage(errorMsg);
                    placeOrderContext.sendMessage(result);
                }
            }
        }, this);
    }


}
