package com.vfinworks.vfsdk.context;


public class WithdrawContext extends BaseContext{
	private static final long serialVersionUID = 6423866860287065326L;

	private String memberId;
	private String availableAmount;
	//外部提现订单号
	private String outTradeNumber;
	//服务器异步通知页面路径
	private String notifyUrl;

	public String createOutTradeNumber(){
		return memberId+System.currentTimeMillis();
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getAvailableAmount() {
		return availableAmount;
	}

	public void setAvailableAmount(String availableAmount) {
		this.availableAmount = availableAmount;
	}

	public String getOutTradeNumber() {
		return outTradeNumber;
	}

	public void setOutTradeNumber(String outTradeNumber) {
		this.outTradeNumber = outTradeNumber;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}
}
