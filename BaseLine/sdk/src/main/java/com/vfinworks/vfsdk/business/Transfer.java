package com.vfinworks.vfsdk.business;

import android.content.Context;
import android.text.TextUtils;

import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.Utils;
import com.vfinworks.vfsdk.context.TransferContext;
import com.vfinworks.vfsdk.enumtype.VFCardTypeEnum;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.model.QpayNewBankCardModel;

import java.util.HashMap;

public class Transfer {
    private Context mContext;
    private TransferContext transferContext;
    private int currentPayType;
    private boolean isNewCard;
    private QpayNewBankCardModel newBank = null;
    private String bankcardId = null;
    private String memo;

    public Transfer(Context context, TransferContext transfercontext, int payType) {
        mContext = context;
        this.transferContext = transfercontext;
        currentPayType = payType;
    }

    public void setNewCardFlag(boolean newBankFlag) {
        isNewCard = newBankFlag;
    }

    public void setNewBankModel(QpayNewBankCardModel bank) {
        newBank = bank;
    }

    public void setBankcardId(String bankcardId) {
        this.bankcardId = bankcardId;
    }

    public void doPay(VFinResponseHandler vfinResponseHand) {
        RequestParams reqParam = new RequestParams(transferContext);
        reqParam.putData("service", "balance_transfer");
        reqParam.putData("token", SDKManager.token);
//        reqParam.putData("pay_pwd", SHA256Encrypt.bin2hex("111111"));
        reqParam.putData("pay_pwd", "111111");
        reqParam.putData("outer_trade_no", transferContext.getOutTradeNumber());
        reqParam.putData("amount", transferContext.getAmount());
        reqParam.putData("fundin_identity_no", transferContext.getAccountNum());
        String strIdentityType = "MOBILE";
        if (transferContext.getAccountNum().contains("@") == true) {
            strIdentityType = "EMAIL";
        }
        reqParam.putData("fundin_identity_type", strIdentityType);
        reqParam.putData("notify_url", transferContext.getNotifyUrl());
        if(!TextUtils.isEmpty(transferContext.getMemo())){
            reqParam.putData("memo", transferContext.getMemo());
        }
        if (currentPayType == VFCardTypeEnum.ZHIFUBAO.getCode()) {
            initAlipayRequestParams(reqParam);
        } else if (currentPayType == VFCardTypeEnum.QPAY.getCode()) {
            initQpayRequestParams(reqParam);
        } else if (currentPayType == VFCardTypeEnum.EBANK.getCode()) {
            initEbankRequestParams(reqParam);
        }else if(currentPayType == VFCardTypeEnum.Overage.getCode()){
            initOverageRequestParams(reqParam);
        }
        HttpUtils.getInstance(mContext).excuteHttpRequest(HttpRequsetUri.getInstance()
                .getAcquirerDoUri(), reqParam, vfinResponseHand, mContext);
    }

    private void initEbankRequestParams(RequestParams reqParam) {
        String strPayMethod = "{\"pay_channel\":\"02\",\"amount\":" + transferContext.getAmount()
                + ",\"memo\":\"TESTBANK,C,DC\"}";
        reqParam.putData("pay_method", strPayMethod);
        reqParam.putData("access_channel", "WEB");
    }

    private void initAlipayRequestParams(RequestParams reqParam) {
        String strPayMethod = "{\"pay_channel\":\"02\",\"amount\":" + transferContext.getAmount()
                + ",\"memo\":\"ALIPAY,C,DC\"}";
        reqParam.putData("pay_method", strPayMethod);
    }

    private void initQpayRequestParams(RequestParams reqParam) {
        String strPayMethod = "{\"pay_channel\":\"05\",\"amount\":" + transferContext.getAmount()
                + "}";
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
            strQpayInfo = "{\"bank_account_id\":\"" + bankcardId + "\"}";
        }
        reqParam.putData("qpay_info", strQpayInfo);
    }

    private void initOverageRequestParams(RequestParams reqParam) {
        String strPayMethod = "[{\"pay_channel\":\"01\",\"amount\":" + transferContext.getAmount() + "}]";
        reqParam.putData("pay_method", strPayMethod);
    }
}
