package com.vfinworks.vfsdk.business;

import android.content.Context;

import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.Utils;
import com.vfinworks.vfsdk.context.PaymentContext;
import com.vfinworks.vfsdk.enumtype.VFCardTypeEnum;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.model.QpayNewBankCardModel;

import java.util.HashMap;

public class Payment {
	private Context mContext;
	private PaymentContext paymentContext;
	private int currentPayType;
	private boolean isNewCard;
	private QpayNewBankCardModel newBank = null;
	private String bankCardId;
	public Payment(Context context,PaymentContext payContext,int payType) {
		mContext = context;
		paymentContext = payContext;
		currentPayType = payType;
	}
	
	public void setNewCardFlag(boolean newBankFlag) {
		isNewCard = newBankFlag;
	}
	
	public void setNewBankModel(QpayNewBankCardModel bank) {
		newBank = bank;
	}

	public void setBankCardId(String bankCardId) {
		this.bankCardId = bankCardId;
	}

	public void doPay(VFinResponseHandler vfinResponseHand) {
		RequestParams reqParam = new RequestParams(paymentContext);
		reqParam.putData("request_no", paymentContext.getRequestNo());
		reqParam.putData("service", "create_pay");
		reqParam.putData("token", SDKManager.token);
		reqParam.putData("outer_trade_no_list", paymentContext.getOrderNums());
		reqParam.putData("access_channel", "WEB");
		reqParam.putData("channel", "SDK");
		if(currentPayType == VFCardTypeEnum.ZHIFUBAO.getCode()) {
			initAlipayRequestParams(reqParam);
		}else if(currentPayType == VFCardTypeEnum.QPAY.getCode()) {
			initQpayRequestParams(reqParam);
		}else if(currentPayType == VFCardTypeEnum.Overage.getCode()) {
			initOverageRequestParams(reqParam);
		} else if (currentPayType == VFCardTypeEnum.EBANK.getCode()) {
			initEbankRequestParams(reqParam);
		}else if(currentPayType == VFCardTypeEnum.WEIXIN.getCode()){
			iniWXinRequestParams(reqParam);
		}

		HttpUtils.getInstance(mContext).excuteHttpRequest(HttpRequsetUri.getInstance()
				.getAcquirerDoUri(), reqParam, vfinResponseHand, mContext);
	}

	/**
	 * 设置微信参数
	 * @param reqParam
	 */
	private void iniWXinRequestParams(RequestParams reqParam) {
		String strPayMethod = "[{\"pay_channel\":\"02\",\"amount\":" + paymentContext.getOrderAmount() + ",\"memo\":\"WXPAY,C,DC\"}]";
		reqParam.putData("pay_method", strPayMethod);
	}

	/**
	 * 设置网银参数
	 * @param reqParam
	 */
	private void initEbankRequestParams(RequestParams reqParam) {
		String strPayMethod = "[{\"pay_channel\":\"02\",\"amount\":" + paymentContext.getOrderAmount()
				+ ",\"memo\":\"TESTBANK,C,DC\"}]";
		reqParam.putData("pay_method", strPayMethod);
		reqParam.putData("access_channel", "WEB");
	}

	/**
	 * 设置余额支付参数
	 * @param reqParam
	 */
	private void initOverageRequestParams(RequestParams reqParam) {
		String strPayMethod = "[{\"pay_channel\":\"01\",\"amount\":" + paymentContext.getOrderAmount() + "}]";
		reqParam.putData("pay_method", strPayMethod);
	}

	private void initAlipayRequestParams(RequestParams reqParam) {
		String strPayMethod = "[{\"pay_channel\":\"02\",\"amount\":" + paymentContext.getOrderAmount() + ",\"memo\":\"ALIPAY,C,DC\"}]";
		reqParam.putData("pay_method", strPayMethod);
	}
	
	private void initQpayRequestParams(RequestParams reqParam) {
		String strPayMethod = "[{\"pay_channel\":\"05\",\"amount\":" + paymentContext.getOrderAmount() + "}]";
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
}
