package com.vfinworks.vfsdk.context;

public class RechargeContext extends BaseAcquireContext {
	private static final long serialVersionUID = 1483544605271218215L;
	//服务器异步通知页面路径
	private String notifyUrl;

	public String getNotifyUrl() {
		return notifyUrl;
	}
	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	/**
	 * 生成充值上下文内容
	 * @param callBackMessageId
	 * 		充值过程中handle发送的消息id
	 * @param outTradeNumber
	 * 		外部订单号
	 * @param notifyUrl
	 */
	public void generateRechargeContextContent(int callBackMessageId,String outTradeNumber,String notifyUrl){
		setCallBackMessageId(callBackMessageId);
		setOutTradeNumber(outTradeNumber);
		setNotifyUrl(notifyUrl);
	}
}
