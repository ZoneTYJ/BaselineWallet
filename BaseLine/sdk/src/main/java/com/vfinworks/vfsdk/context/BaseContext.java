package com.vfinworks.vfsdk.context;

import android.os.Message;

import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.model.VFSDKResultModel;

import java.io.Serializable;

public class BaseContext implements Serializable{
	private static final long serialVersionUID = 8236241859338731576L;
	
	private String token;
	private int callBackMessageId;
	
	private String sign;
	private String signType;
	
	//判断是不是从sdk外部调用，还是在sdk内部调用
	private boolean isExternal = true;
	//用于扫码收时候下单Id
	private String deviceId;
	//用于控制是否需要在下订单时候内部验签,true需要,false不需要
	private boolean signFlag=false;

	private String mobile;//用户当前登录的的手机号
	private String appExtension;
	public BaseContext(){}

	public BaseContext(BaseContext context) {
		token=context.getToken();
		callBackMessageId=context.getCallBackMessageId();
		sign=context.getSign();
		signType=context.getSignType();
		deviceId=context.getDeviceId();
		signFlag=context.isSignFlag();
		isExternal=context.isExternal();
	}

	public boolean isExternal() {
		return isExternal;
	}
	public void setExternal(boolean isExternal) {
		this.isExternal = isExternal;
	}
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}

	public void sendMessage(VFSDKResultModel resultModel) {
		if(SDKManager.getInstance().getCallbackHandler() != null) {
			Message msg = SDKManager.getInstance().getCallbackHandler().obtainMessage();
			msg.what = callBackMessageId;
			msg.obj = resultModel;
			SDKManager.getInstance().getCallbackHandler().sendMessage(msg);
		}
	}

	public int getCallBackMessageId() {
		return callBackMessageId;
	}
	public void setCallBackMessageId(int callBackMessageId) {
		this.callBackMessageId = callBackMessageId;
	}
	public String getSignType() {
		return signType;
	}
	public void setSignType(String signType) {
		this.signType = signType;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public String getAppExtension() {
		return appExtension;
	}

	public void setAppExtension(String appExtension) {
		this.appExtension = appExtension;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public boolean isSignFlag() {
		return signFlag;
	}
	public void setSignFlag(boolean signFlag) {
		this.signFlag = signFlag;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

}
