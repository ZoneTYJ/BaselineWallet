package com.vfinworks.vfsdk.activity.core.channel;

import android.text.TextUtils;
import android.widget.ImageView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.context.BaseContext;
import com.vfinworks.vfsdk.enumtype.VFCallBackEnum;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.model.ChannelModel;
import com.vfinworks.vfsdk.model.QpayNewBankCardModel;
import com.vfinworks.vfsdk.model.VFSDKResultModel;
import com.vfinworks.vfsdk.wxpay.WXHandler;
import com.vfinworks.vfsdk.wxpay.WXRunnable;
import com.vfinworks.vfsdk.wxpay.WXinPay;

import org.json.JSONObject;

/**
 * Created by tangyijian on 2016/5/13.
 */
//@ChannelAnnotation(inst_code = "WXPAY10101",pay_mode = "NETBANK")
public class WXpayChannel extends BaseChannel {

    public WXpayChannel(){
        super();
    }

    public WXpayChannel(BaseActivity context, ChannelResponseHandler
            channelResponseHandler, boolean isNewCard, QpayNewBankCardModel newBank, String bankCardId) {
        super.setChannelPara(context, channelResponseHandler, isNewCard, newBank, bankCardId);
    }


    @Override
    protected void goPay(String status, String payResult, JSONObject jsonObject) {
        startWXinPay(status, payResult, jsonObject,mPaymentContext,mPaymentContext.getOrderNums());
    }

    @Override
    protected void goRecharge(String status, String stringResult, JSONObject json) {
        startWXinPay(status,stringResult,json,mRechargeContext,mRechargeContext.getOutTradeNumber());
    }

    @Override
    protected void goTransferToAccount(String status, String stringResult, JSONObject json) {
        startWXinPay(status,stringResult,json,mTransferContext,mTransferContext.getOutTradeNumber());
    }

    @Override
    public String getName(ChannelModel channel) {
        return "微信支付";
    }

    @Override
    public ChannelModel getChannelModel() {
       return getChannel();
    }

    @Override
    public void setDrawableIcon(ImageView imageview) {
        imageview.setImageResource(R.drawable.vf_wx_icon);
    }

    private void startWXinPay(String status, String payResult, JSONObject jsonObject, final BaseContext baseContext,final String innerNo) {
        WXHandler.getInstance().attachRunnable(new WXRunnable() {
            @Override
            public void run() {
                boolean isSuccess= (boolean) getMessage().obj;
                if(isSuccess) {
                    channelResponseHandler.OnSuccess(innerNo);
                }else {
                    mContext.hideProgress();
                }
                WXHandler.getInstance().detachRunnable();
            }
        });
        if (!TextUtils.isEmpty(payResult)) {
            WXinPay wXinPay = new WXinPay(mContext, new WXinPay.PayResultListener() {

                @Override
                public void onUnstalled(String msg) {
                    mContext.finishAll();
                    if (SDKManager.getInstance().getCallbackHandler() != null) {
                        VFSDKResultModel result = new VFSDKResultModel();
                        result.setResultCode(VFCallBackEnum.ERROR_CODE_BUSINESS.getCode());
                        result.setMessage(msg);
                        baseContext.sendMessage(result);
                    }
                }
            });
            wXinPay.goWeixinPay(payResult);
        } else {
            mContext.showShortToast("微信订单为空！");
        }
    }

    @Override
    protected void initRequestParams(RequestParams reqParam,String amount) {
        String strPayMethod = "[{\"pay_channel\":\"02\",\"amount\":" + amount + ",\"memo\":\"WXPAY1,C,DC\"}]";
        reqParam.putData("pay_method", strPayMethod);
    }

    @Override
    protected void initRechargeRequestParams(RequestParams reqParam, String amount) {
        String strPayMethod = "{\"pay_channel\":\"02\",\"amount\":" + amount + ",\"memo\":\"WXPAY1,C,DC\"}";
        reqParam.putData("pay_method", strPayMethod);
    }

    public static ChannelModel getChannel(){
        return new ChannelModel("WXPAY10101","NETBANK");
    }
}
