package com.vfinworks.vfsdk.business;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.common.Config;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.context.PlaceOrderContext;
import com.vfinworks.vfsdk.enumtype.VFCallBackEnum;
import com.vfinworks.vfsdk.enumtype.VFOrderTypeEnum;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.interfacemanager.SignInterface;
import com.vfinworks.vfsdk.interfacemanager.SignInterfaceManager;
import com.vfinworks.vfsdk.model.VFSDKResultModel;

import org.json.JSONException;
import org.json.JSONObject;

public class PlaceOrder {
    private Context mContext;
    private PlaceOrderContext placeOrderContext;

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==SignInterface.SIGNOK){
                httpPalceOrder();
            }
        }
    };

    public PlaceOrder(Context context, PlaceOrderContext Ordercontext) {
        mContext = context;
        placeOrderContext = Ordercontext;
    }

    public void doPlaceOrder() {
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

    /**
     * 下订单请求,若不需要内部加签，参数就为空
     */
    private void httpPalceOrder() {
        RequestParams reqParam = new RequestParams(placeOrderContext);
        String service = "";
        if (placeOrderContext.getTradeType().getCode() == VFOrderTypeEnum.TRADE_INSTANT.getCode())
            service = VFOrderTypeEnum.TRADE_INSTANT.getTradeService();
        else
            service = VFOrderTypeEnum.TRADE_ENSURE.getTradeService();
        reqParam.putData("service", service);
        reqParam.putData("token", SDKManager.token);
        reqParam.putData("request_no", placeOrderContext.getRequestNo());

        reqParam.putData("trade_list", placeOrderContext.getTradeList());
        if (TextUtils.isEmpty(placeOrderContext.getPayMethod()) == false)
            reqParam.putData("pay_method", placeOrderContext.getPayMethod());
        HttpUtils.getInstance(mContext).excuteHttpRequest(HttpRequsetUri.getInstance()
                .getAcquirerDoUri(), reqParam, new VFinResponseHandler() {

            @Override
            public void onSuccess(Object responseBean, String responseString) {
                if (SDKManager.getInstance().getCallbackHandler() != null) {
                    VFSDKResultModel result = new VFSDKResultModel();
                    result.setResultCode(VFCallBackEnum.OK.getCode());
                    result.setJsonData(responseString);
                    placeOrderContext.sendMessage(result);
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
    }
}
