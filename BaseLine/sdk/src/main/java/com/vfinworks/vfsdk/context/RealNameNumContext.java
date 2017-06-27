package com.vfinworks.vfsdk.context;

public class RealNameNumContext extends BaseContext {
	private static final long serialVersionUID = 6423866860287065326L;
	
	//证件号码
	private String certNo;
	//真实姓名
	private String realName;

	private String certType = "IC";

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

	public String getCertType() {
		return certType;
	}

	public void setCertType(String certType) {
		this.certType = certType;
	}
}
