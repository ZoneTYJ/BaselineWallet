package com.vfinworks.vfsdk.context;

import java.io.Serializable;

public class RealNameContext extends BaseContext{
	private static final long serialVersionUID = 6423866860287065326L;
	
	//业务唯一请求号
	private String requestNo;
	//证件号码
	private String certNo;
	//真实姓名
	private String realName;
	//认证模式：ARD_CERT：银行卡实名认证；IDENTITY_CERT：身份认证；
	private String authMode;
	//银行卡号
	private String bankNo;
	//手机号
	private String mobile;
	// 信用卡cvv2
	private String cvv2;
	//信用卡有效期 格式：YYMM
	private String endDate;
	
	public String getRequestNo() {
		return requestNo;
	}
	public void setRequestNo(String requestNo) {
		this.requestNo = requestNo;
	}
	public String getCertNo() {
		return certNo;
	}
	public void setCertNo(String certNo) {
		this.certNo = certNo;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getAuthMode() {
		return authMode;
	}
	public void setAuthMode(String authMode) {
		this.authMode = authMode;
	}
	public String getBankNo() {
		return bankNo;
	}
	public void setBankNo(String bankNo) {
		this.bankNo = bankNo;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getCvv2() {
		return cvv2;
	}
	public void setCvv2(String cvv2) {
		this.cvv2 = cvv2;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
}
