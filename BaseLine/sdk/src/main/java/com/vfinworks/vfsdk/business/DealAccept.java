package com.vfinworks.vfsdk.business;

import android.app.Dialog;
import android.content.Context;

import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.SHA256Encrypt;
import com.vfinworks.vfsdk.context.DealAcceptContext;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.view.LoadingDialog;
import com.vfinworks.vfsdk.view.paypwd.PayPwdDialog;
import com.vfinworks.vfsdk.view.paypwd.PayPwdView;
import com.vfinworks.vfsdk.view.paypwd.VFEncryptData;

import org.json.JSONException;
import org.json.JSONObject;

public class DealAccept {
    private  Dialog mLoadingDialog;
    private  AcceeptResponseHandler responseHandler;
    private Context mContext;
    private DealAcceptContext dealAcceptContext;

    public DealAccept(Context context, DealAcceptContext dealAcceptContext,AcceeptResponseHandler vFinResponseHandler) {
        mContext = context;
        this.dealAcceptContext = dealAcceptContext;
        mLoadingDialog = new LoadingDialog(context);
        responseHandler=vFinResponseHandler;
    }
    public void doDealAccept(){
        checkPayPassword();
    }

    protected void checkPayPassword() {
        final PayPwdDialog mDialog = new PayPwdDialog(mContext,dealAcceptContext);
        mDialog.setPayMoney(null);
        mDialog.setOnStatusChangeListener(new PayPwdView.OnStatusChangeListener() {
            @Override
            public void onInputComplete(VFEncryptData result) {
                withdrawPro(dealAcceptContext.getToken(),dealAcceptContext.getOutTradeNumber(),result.getCiphertext());
                mDialog.dismiss();
            }

            @Override
            public void onUserCanel() {
                mDialog.dismiss();
            }

        });
        mDialog.show();
    }


    private void withdrawPro(String token,String outTradeNo,String payPwd) {
        showProgress();
        RequestParams reqParam = new RequestParams();
        reqParam.putData("service", "verify_paypwd");
        reqParam.putData("token", SDKManager.token);
        reqParam.putData("out_trade_no", outTradeNo);
        reqParam.putData("pay_pwd", SHA256Encrypt.bin2hex(payPwd));

        HttpUtils.getInstance(mContext).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(),
                reqParam, new VFinResponseHandler() {

                    @Override
                    public void onSuccess(Object responseBean, String responseString) {
                        JSONObject json;
                        try {
                            json = new JSONObject(responseString);
                            String isSuccess = json.getString("is_success");
                            //T代表验证成功
                            if (isSuccess.equalsIgnoreCase("T")) {
                                httpDealAccept();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            responseHandler.onError("error",e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String statusCode, String errorMsg) {
                        hideProgress();
                        responseHandler.onError(statusCode,errorMsg);
                    }
                }, this);
    }

    public void httpDealAccept() {
        RequestParams reqParam = new RequestParams(dealAcceptContext);
        reqParam.putData("service", "create_settle");
        reqParam.putData("token", SDKManager.token);
        reqParam.putData("request_no", dealAcceptContext.getRequestNo());
        reqParam.putData("outer_trade_no", dealAcceptContext.getOutTradeNumber());
        HttpUtils.getInstance(mContext).excuteHttpRequest(HttpRequsetUri.getInstance()
                .getAcquirerDoUri(), reqParam, new VFinResponseHandler() {
            @Override
            public void onSuccess(Object responseBean, String responseString) {
                hideProgress();
                if(responseHandler!=null) {
                    responseHandler.onSuccess(responseBean, responseString);
                }
            }

            @Override
            public void onError(String statusCode, String errorMsg) {
                hideProgress();
                if(responseHandler!=null) {
                    responseHandler.onError(statusCode,errorMsg);;
                }
            }
        }, this);
    }


    public void showProgress(){
        mLoadingDialog.show();
    }

    public void hideProgress(){
        mLoadingDialog.dismiss();
    }

    public interface  AcceeptResponseHandler {
        void onSuccess(Object responseBean, String responseString);
        void onError(String statusCode, String errorMsg);
    }
}
