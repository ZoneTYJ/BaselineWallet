package com.vfinworks.vfsdk.business;

import android.content.Context;

import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.Utils;
import com.vfinworks.vfsdk.context.RechargeContext;
import com.vfinworks.vfsdk.enumtype.VFCardTypeEnum;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.model.QpayNewBankCardModel;

import java.util.HashMap;

public class Recharge {
	private Context mContext;
	private RechargeContext rechargeContext;
	private int currentPayType;
	private boolean isNewCard;
	private QpayNewBankCardModel newBank = null;
	private String bankcardId=null;
	public Recharge(Context context,RechargeContext rechContext,int payType) {
		mContext = context;
		rechargeContext = rechContext;
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
		RequestParams reqParam = new RequestParams(rechargeContext);
		reqParam.putData("service", "do_deposit");
		reqParam.putData("token", SDKManager.token);
		reqParam.putData("outer_trade_no", rechargeContext.getOutTradeNumber());
		reqParam.putData("amount",rechargeContext.getAmount());
		reqParam.putData("operator_id", "");
		reqParam.putData("notify_url", rechargeContext.getNotifyUrl());
		if(currentPayType == VFCardTypeEnum.ZHIFUBAO.getCode()) {
			initAlipayRequestParams(reqParam);
		}else if(currentPayType == VFCardTypeEnum.QPAY.getCode()) {
			initQpayRequestParams(reqParam);
		}else if(currentPayType == VFCardTypeEnum.EBANK.getCode()){
			initEbankRequestParams(reqParam);
		}
		HttpUtils.getInstance(mContext).excuteHttpRequest(HttpRequsetUri.getInstance().getAcquirerDoUri(), reqParam, vfinResponseHand,mContext);
	}

	private void initEbankRequestParams(RequestParams reqParam) {
		String strPayMethod = "{\"pay_channel\":\"02\",\"amount\":" + rechargeContext.getAmount() + ",\"memo\":\"TESTBANK,C,DC\"}";
		reqParam.putData("pay_method", strPayMethod);
		reqParam.putData("access_channel", "WEB");
	}

	private void initAlipayRequestParams(RequestParams reqParam) {
		String strPayMethod = "{\"pay_channel\":\"02\",\"amount\":" + rechargeContext.getAmount() + ",\"memo\":\"ALIPAY,C,DC\"}";
		reqParam.putData("pay_method", strPayMethod);
	}
	
	private void initQpayRequestParams(RequestParams reqParam) {
		String strPayMethod = "{\"pay_channel\":\"05\",\"amount\":" + rechargeContext.getAmount() + "}";
		reqParam.putData("pay_method", strPayMethod);
		String strQpayInfo = "";
		if(isNewCard == true) {
			//新卡
			HashMap<String,String> qpayInfo = new HashMap<String,String>();
			qpayInfo.put("bank_code", newBank.getBankcode());
			qpayInfo.put("bank_name", newBank.getBankName());
			qpayInfo.put("bank_account_no", newBank.getCardNo());
			qpayInfo.put("account_name", newBank.getName());
			qpayInfo.put("cert_no", newBank.getPersonId());
			qpayInfo.put("mobile", newBank.getPhone());
			qpayInfo.put("card_type", "DEBIT");
			qpayInfo.put("card_attribute", "C");
			strQpayInfo = Utils.getInstance().hashmap2Json(qpayInfo);
		}else{
			//已经绑定的快捷卡
			strQpayInfo = "{\"bank_account_id\":\"" + bankcardId + "\"}";
		}
		reqParam.putData("qpay_info", strQpayInfo);
	}
}
