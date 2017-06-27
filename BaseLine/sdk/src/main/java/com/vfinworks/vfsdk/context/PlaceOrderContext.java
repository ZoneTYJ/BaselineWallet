package com.vfinworks.vfsdk.context;

import com.vfinworks.vfsdk.enumtype.VFOrderTypeEnum;

public class PlaceOrderContext extends BaseContext{
	private static final long serialVersionUID = 1L;

	public String paymentSign;
	private VFOrderTypeEnum tradeType;
	//批次号 商户系统请求号，每次请求号不可重复
	private String requestNo;
	//Json格式:[{"out_trade_no":"201601270000000001","subject":"牛奶","total_amount":"11","ensure_amount":"1","seller":"13621722085","seller_type":"MOBILE","price":"11","quantity":1}] 
	private String tradeList;
	//Json格式:[{"pay_channel":"02","amount":11,"memo":"ALIPAY,C,DC"}]
	private String payMethod;

	/**
	 * 订单交易号
	 */
	public String tradeNo;
	/**
	 * 订单金额
	 */
	public String orderAmount;
	public String getPaymentSign() {
		return paymentSign;
	}

	public void setPaymentSign(String paymentSign) {
		this.paymentSign = paymentSign;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public String getRequestNo() {
		return requestNo;
	}
	public void setRequestNo(String requestNo) {
		this.requestNo = requestNo;
	}
	public VFOrderTypeEnum getTradeType() {
		return tradeType;
	}
	public void setTradeType(VFOrderTypeEnum tradeType) {
		this.tradeType = tradeType;
	}
	public String getTradeList() {
		return tradeList;
	}
	public void setTradeList(String tradeList) {
		this.tradeList = tradeList;
	}
	public String getPayMethod() {
		return payMethod;
	}
	public void setPayMethod(String payMethod) {
		this.payMethod = payMethod;
	}

	public String getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(String orderAmount) {
		this.orderAmount = orderAmount;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 *	生成即时交易订单
	 * @param out_trade_no
	 * 		外部订单号
	 * @param subject
	 * 		商品名称
	 * @param total_amount
	 * 		总金额
	 * @param seller
	 * 		卖家
	 * @param seller_type
	 * 		卖家类型（MOBILE,UID,MEMBER_ID,LOGIN_NAME)
	 * @param price
	 * 		商品价格
	 * @param quantity
	 * 		商品数量
	 */
	public void generateInstantTradeList(String out_trade_no,String subject,String total_amount,
										 String seller,String seller_type,String price,String quantity,String notify_url,String appExtension){
		String strTradeList = "[{\"out_trade_no\":\"" + out_trade_no + "\",\"subject\":\"" + subject + "\",\"total_amount\":\"" +
				total_amount + "\",";
		strTradeList = strTradeList + "\"seller\":\"" + seller + "\",\"seller_type\":\"" +
				seller_type + "\",\"price\":\"" + price +
				"\",\"quantity\":\"" + quantity +
				"\",\"app_extension\":\"" + appExtension +
				"\",\"notify_url\":\""+notify_url +"\"}]";
		tradeList = strTradeList;
		setTradeType(VFOrderTypeEnum.TRADE_INSTANT);
		tradeNo = out_trade_no;
		orderAmount = total_amount;
	}

	/**
	 * 生成担保交易订单
	 * @param out_trade_no
	 * 		外部订单号
	 * @param subject
	 * 		商品名称
	 * @param total_amount
	 * 		总金额
	 * @param ensure_amount
	 * 		担保金额
	 * @param seller
	 * 		卖家
	 * @param seller_type
	 * 		卖家类型（MOBILE,UID,MEMBER_ID,LOGIN_NAME)
	 * @param price
	 * 		商品价格
	 * @param quantity
	 * 		商品数量
	 */
	public void generateEnsureTradeList(String out_trade_no,String subject,String total_amount,String ensure_amount,
										 String seller,String seller_type,String price,String quantity,String notify_url,
											String appExtension){
		String strTradeList = "[{\"out_trade_no\":\"" + out_trade_no + "\",\"subject\":\"" + subject + "\",\"total_amount\":\"" +
				total_amount + "\",";
		strTradeList = strTradeList + "\"ensure_amount\":\"" + ensure_amount + "\",";
		strTradeList = strTradeList + "\"seller\":\"" + seller + "\",\"seller_type\":\"" +
				seller_type + "\",\"price\":\"" + price +
				"\",\"quantity\":\"" + quantity +
				"\",\"app_extension\":\"" + appExtension +
				"\",\"notify_url\":\""+notify_url + "\"}]";
		tradeList = strTradeList;
		setTradeType(VFOrderTypeEnum.TRADE_ENSURE);
		tradeNo = out_trade_no;
		orderAmount = total_amount;
	}
}
