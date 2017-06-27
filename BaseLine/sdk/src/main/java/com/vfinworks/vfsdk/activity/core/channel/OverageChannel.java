package com.vfinworks.vfsdk.activity.core.channel;

import android.text.TextUtils;
import android.widget.ImageView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.annotation.ChannelAnnotation;
import com.vfinworks.vfsdk.context.PaymentContext;
import com.vfinworks.vfsdk.context.RechargeContext;
import com.vfinworks.vfsdk.context.TransferContext;
import com.vfinworks.vfsdk.enumtype.VFCallBackEnum;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.model.ChannelModel;
import com.vfinworks.vfsdk.model.QpayNewBankCardModel;
import com.vfinworks.vfsdk.model.VFSDKResultModel;

import org.json.JSONObject;

import java.math.BigDecimal;

/**
 * Created by tangyijian on 2016/5/13.
 */
@ChannelAnnotation(inst_code = "WALLET", pay_mode = "")
public class OverageChannel extends BaseChannel {
    public OverageChannel() {
        super();
    }

    public OverageChannel(BaseActivity context, ChannelResponseHandler
            channelResponseHandler, boolean isNewCard, QpayNewBankCardModel newBank, String
            bankCardId) {
        super.setChannelPara(context, channelResponseHandler, isNewCard, newBank, bankCardId);
    }

    @Override
    protected void goPay(String status, String payResult, JSONObject json) {
        channelResponseHandler.OnSuccess(mPaymentContext.getOrderNums());
    }

    @Override
    protected void goRecharge(String status, String stringResult, JSONObject json) {
        channelResponseHandler.OnSuccess(mRechargeContext.getOutTradeNumber());
    }

    @Override
    protected void goTransferToAccount(String status, String stringResult, JSONObject json) {
        channelResponseHandler.OnSuccess(mTransferContext.getOutTradeNumber());
    }

    @Override
    public String getName(ChannelModel channel) {
        return "余额    " + channel.amount;
    }

    @Override
    public ChannelModel getChannelModel() {
        return getChannel();
    }

    @Override
    public void setDrawableIcon(ImageView imageview) {
        imageview.setImageResource(R.drawable.vf_qb_icon);
    }

    @Override
    protected void initRequestParams(RequestParams reqParam, String amount) {
        String strPayMethod = "[{\"pay_channel\":\"01\",\"amount\":" + amount + "}]";
        reqParam.putData("pay_method", strPayMethod);
    }

    @Override
    protected void initRechargeRequestParams(RequestParams reqParam, String amount) {
        String strPayMethod = "{\"pay_channel\":\"01\",\"amount\":" + amount + "}";
        reqParam.putData("pay_method", strPayMethod);
    }

    @Override
    public void doPay(PaymentContext paymentContext) {
        mPaymentContext = paymentContext;
        if (checkAmount(mPaymentContext.getOrderAmount())) {
            super.checkPayPassword(mPaymentContext.getToken(), mPaymentContext.getOrderNums(),
                    mPaymentContext.getOrderAmount(),
                    new PayPasswordHandler() {

                        @Override
                        public void OnSuccess() {
                            OverageChannel.super.doPay(mPaymentContext);
                        }

                        @Override
                        public void OnError(String errorMsg) {
                            if (SDKManager.getInstance().getCallbackHandler() != null) {
                                VFSDKResultModel result = new VFSDKResultModel();
                                result.setResultCode(VFCallBackEnum.ERROR_CODE_BUSINESS
                                        .getCode());
                                result.setMessage(errorMsg);
                                mPaymentContext.sendMessage(result);
                            }
                        }
                    });
        }
    }

    @Override
    public void doTranferToAccount(TransferContext transferContext) {
        mTransferContext = transferContext;
        if (checkAmount(mTransferContext.getAmount())) {
            super.checkPayPassword(mTransferContext.getToken(), mTransferContext
                    .getOutTradeNumber(), mTransferContext.getAmount(), new
                    PayPasswordHandler() {
                        @Override
                        public void OnSuccess() {
                            OverageChannel.super.doTranferToAccount(mTransferContext);
                        }

                        @Override
                        public void OnError(String errorMsg) {
                            if (SDKManager.getInstance().getCallbackHandler() != null) {
                                VFSDKResultModel result = new VFSDKResultModel();
                                result.setResultCode(VFCallBackEnum.ERROR_CODE_BUSINESS
                                        .getCode());
                                result.setMessage(errorMsg);
                                mTransferContext.sendMessage(result);
                            }
                        }
                    });
        }
    }

    @Override
    public void doRecharge(RechargeContext rechargeContext) {
        mRechargeContext = rechargeContext;
        if (checkAmount(mRechargeContext.getAmount())) {
            super.checkPayPassword(mRechargeContext.getToken(), mRechargeContext
                    .getOutTradeNumber(), mRechargeContext.getAmount(), new
                    PayPasswordHandler() {

                        @Override
                        public void OnSuccess() {
                            OverageChannel.super.doRecharge(mRechargeContext);
                        }

                        @Override
                        public void OnError(String errorMsg) {
                            if (SDKManager.getInstance().getCallbackHandler() != null) {
                                VFSDKResultModel result = new VFSDKResultModel();
                                result.setResultCode(VFCallBackEnum.ERROR_CODE_BUSINESS
                                        .getCode());
                                result.setMessage(errorMsg);
                                mRechargeContext.sendMessage(result);
                            }
                        }
                    });
        }
    }

    private boolean checkAmount(String payAmount) {
        if (TextUtils.isEmpty(amount)) {
            mContext.showShortToast("余额信息出错");
            return false;
        }
        //小于
        if (new BigDecimal(amount).compareTo(new BigDecimal(payAmount)) == -1) {
            mContext.showShortToast("余额不足");
            return false;
        }
        return true;
    }

    public static ChannelModel getChannel() {
        return new ChannelModel("WALLET", "");
    }
}
