package com.vfinworks.vfsdk.activity.core.channel;

import android.text.TextUtils;
import android.widget.ImageView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.alipay.AliPay;
import com.vfinworks.vfsdk.alipay.PayResult;
import com.vfinworks.vfsdk.common.Utils;
import com.vfinworks.vfsdk.enumtype.VFCallBackEnum;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.model.ChannelModel;
import com.vfinworks.vfsdk.model.QpayNewBankCardModel;
import com.vfinworks.vfsdk.model.VFSDKResultModel;

import org.json.JSONObject;

/**
 * Created by tangyijian on 2016/5/13.
 */
//@ChannelAnnotation(inst_code = "ALIPAY10401",pay_mode = "NETBANK")
public class AlipayChannel extends BaseChannel {
    public AlipayChannel(){
        super();
    }
    public AlipayChannel(BaseActivity context, ChannelResponseHandler
            channelResponseHandler, boolean isNewCard, QpayNewBankCardModel newBank, String bankCardId) {
        super.setChannelPara(context, channelResponseHandler, isNewCard, newBank, bankCardId);
    }

    @Override
    protected void goPay(String status, String payResult, JSONObject jsonObject) {
        doAliPay(status, payResult, jsonObject,mPaymentContext.getInnerOrderNo());
    }

    @Override
    protected void goRecharge(String status, String stringResult, JSONObject json) {
        doAliPay(status, stringResult, json,mRechargeContext.getInnerOrderNo());
    }

    @Override
    protected void goTransferToAccount(String status, String stringResult, JSONObject json) {
        doAliPay(status,stringResult,json,mTransferContext.getInnerOrderNo());
    }

    @Override
    public String getName(ChannelModel channel) {
        return "支付宝支付";
    }

    @Override
    public ChannelModel getChannelModel() {
        return null;
    }

    @Override
    public void setDrawableIcon(ImageView imageview) {
        imageview.setImageResource(R.drawable.vf_zfb_icon);
    }


    private void doAliPay(String status, String payResult, JSONObject jsonObject,final String innerOrderNo) {
        if (!TextUtils.isEmpty(payResult)) {
            payResult=Utils.getAliformMaps(payResult);


            AliPay alipay = new AliPay(mContext, new AliPay.PayResultListener() {
                @Override
                public void onSuccess() {
                    channelResponseHandler.OnSuccess(innerOrderNo);
                }
                @Override
                public void onFailed(PayResult payResult) {
                    mContext.finishAll();
                    if (SDKManager.getInstance().getCallbackHandler() != null) {
                        VFSDKResultModel result = new VFSDKResultModel();
                        result.setResultCode(VFCallBackEnum.PROCESS.getCode());
                        result.setMessage(payResult.getMemo());
                        mPaymentContext.sendMessage(result);
                    }
                }

                @Override
                public void onProcess() {
                    mContext.finishAll();
                    if (SDKManager.getInstance().getCallbackHandler() != null) {
                        VFSDKResultModel result = new VFSDKResultModel();
                        result.setResultCode(VFCallBackEnum.PROCESS.getCode());
                        result.setMessage("支付处理中");
                        mPaymentContext.sendMessage(result);
                    }
                }

            });
            alipay.pay(payResult);
        } else {
            mContext.showShortToast("支付宝订单为空！");
        }
    }

    @Override
    protected void initRequestParams(RequestParams reqParam,String amount) {
        String strPayMethod = "[{\"pay_channel\":\"02\",\"amount\":" + amount + ",\"memo\":\"ALIPAY,C,DC\"}]";
        reqParam.putData("pay_method", strPayMethod);
    }

    @Override
    protected void initRechargeRequestParams(RequestParams reqParam, String amount) {
        String strPayMethod = "{\"pay_channel\":\"02\",\"amount\":" + amount + ",\"memo\":\"ALIPAY,C,DC\"}";
        reqParam.putData("pay_method", strPayMethod);
    }
}
