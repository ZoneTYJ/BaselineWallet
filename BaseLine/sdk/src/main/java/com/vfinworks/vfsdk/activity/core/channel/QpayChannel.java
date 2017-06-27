package com.vfinworks.vfsdk.activity.core.channel;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.activity.core.PaySmsVerificationActivity;
import com.vfinworks.vfsdk.common.Utils;
import com.vfinworks.vfsdk.context.BaseContext;
import com.vfinworks.vfsdk.context.PaymentContext;
import com.vfinworks.vfsdk.context.RechargeContext;
import com.vfinworks.vfsdk.context.TransferContext;
import com.vfinworks.vfsdk.enumtype.VFCallBackEnum;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.model.ChannelModel;
import com.vfinworks.vfsdk.model.QpayNewBankCardModel;
import com.vfinworks.vfsdk.model.VFSDKResultModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by tangyijian on 2016/5/13.
 */
//@ChannelAnnotation(inst_code = "TESTBANK10110", pay_mode = "QUICKPAY")
public class QpayChannel extends BaseChannel {

    public QpayChannel() {
        super();
    }

    public QpayChannel(BaseActivity context, ChannelResponseHandler
            channelResponseHandler, boolean isNewCard, QpayNewBankCardModel newBank, String
            bankCardId) {
        super.setChannelPara(context, channelResponseHandler, isNewCard, newBank, bankCardId);
    }

    @Override
    public void doPay(PaymentContext paymentContext) {
        mPaymentContext = paymentContext;
        super.checkPayPassword(mPaymentContext.getToken(), mPaymentContext.getOrderNums(),
                mPaymentContext.getOrderAmount(), new
                PayPasswordHandler() {
                    @Override
                    public void OnSuccess() {
                        QpayChannel.super.doPay(mPaymentContext);
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

    public void doTranferToAccount(TransferContext transferContext) {
        mTransferContext = transferContext;
        super.checkPayPassword(mTransferContext.getToken(), mTransferContext.getOutTradeNumber(),
                mTransferContext.getAmount(), new
                PayPasswordHandler() {
                    @Override
                    public void OnSuccess() {
                        QpayChannel.super.doTranferToAccount(mTransferContext);
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

    @Override
    public void doRecharge(RechargeContext rechargeContext) {
        mRechargeContext = rechargeContext;
        super.checkPayPassword(mRechargeContext.getToken(), mRechargeContext.getOutTradeNumber(),
                mRechargeContext.getAmount(), new
                PayPasswordHandler() {
                    @Override
                    public void OnSuccess() {
                        QpayChannel.super.doRecharge(mRechargeContext);
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

    @Override
    protected void goPay(String status, String payResult, JSONObject json) {
        startPaySms(status, payResult, json, mPaymentContext);
    }


    @Override
    protected void goRecharge(String status, String stringResult, JSONObject json) {
        startPaySms(status, stringResult, json, mRechargeContext);
    }

    @Override
    protected void goTransferToAccount(String status, String stringResult, JSONObject json) {
        startPaySms(status, stringResult, json, mTransferContext);
    }

    private void startPaySms(String status, String payResult, JSONObject json, BaseContext
            baseContext) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("context", baseContext);
        bundle.putSerializable("currentChannelModel", getChannel());
        bundle.putBoolean("isNewCard", isNewCard);
        String newBankId = "";
        try {
            if (!json.isNull("bank_account_id")) {
                newBankId = json.getString("bank_account_id");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            mContext.showShortToast(e.getMessage());
        }
        bundle.putString("newBankId", newBankId);
        if (isNewCard) {
            bundle.putSerializable("newBank", newBank);
        } else {
            bundle.putString("bankCardId", bankCardId);
        }
        intent.putExtras(bundle);
        intent.setClass(mContext, PaySmsVerificationActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    public String getName(ChannelModel channel) {
        return "银行卡支付";
    }

    @Override
    public ChannelModel getChannelModel() {
        return getChannel();
    }

    @Override
    protected void initRequestParams(RequestParams reqParam, String amount) {
        String strPayMethod = "[{\"pay_channel\":\"05\",\"amount\":" + amount + "}]";
        reqParam.putData("pay_method", strPayMethod);
        String strQpayInfo = "";
        if (isNewCard == true) {
            //新卡
            HashMap<String, String> qpayInfo = new HashMap<String, String>();
            qpayInfo.put("bank_code", newBank.getBankcode());
            qpayInfo.put("bank_name", newBank.getBankName());
            qpayInfo.put("bank_account_no", newBank.getCardNo());
            qpayInfo.put("account_name", newBank.getName());
            qpayInfo.put("cert_no", newBank.getPersonId());
            qpayInfo.put("mobile", newBank.getPhone());
            qpayInfo.put("card_type", "DEBIT");
            qpayInfo.put("card_attribute", "C");
            strQpayInfo = Utils.getInstance().hashmap2Json(qpayInfo);
        } else {
            //已经绑定的快捷卡
            strQpayInfo = "{bank_account_id:" + bankCardId + "}";
        }
        reqParam.putData("qpay_info", strQpayInfo);
    }

    @Override
    protected void initRechargeRequestParams(RequestParams reqParam, String amount) {
        String strPayMethod = "{\"pay_channel\":\"05\",\"amount\":" + amount + "}";
        reqParam.putData("pay_method", strPayMethod);
        String strQpayInfo = "";
        if (isNewCard == true) {
            //新卡
            HashMap<String, String> qpayInfo = new HashMap<String, String>();
            qpayInfo.put("bank_code", newBank.getBankcode());
            qpayInfo.put("bank_name", newBank.getBankName());
            qpayInfo.put("bank_account_no", newBank.getCardNo());
            qpayInfo.put("account_name", newBank.getName());
            qpayInfo.put("cert_no", newBank.getPersonId());
            qpayInfo.put("mobile", newBank.getPhone());
            qpayInfo.put("card_type", "DEBIT");
            qpayInfo.put("card_attribute", "C");
            strQpayInfo = Utils.getInstance().hashmap2Json(qpayInfo);
        } else {
            //已经绑定的快捷卡
            strQpayInfo = "{bank_account_id:" + bankCardId + "}";
        }
        reqParam.putData("qpay_info", strQpayInfo);
    }

    @Override
    public void setDrawableIcon(ImageView imageview) {
        imageview.setImageResource(R.drawable.vf_yinlian_icon);
    }

    public static ChannelModel getChannel() {
        return new ChannelModel("TESTBANK10110", "QUICKPAY");
    }
}
