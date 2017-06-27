package com.vfinworks.vfsdk.context;

public class PaymentContext extends BaseAcquireContext {
    private static final long serialVersionUID = 1483544605271218215L;
    //批次号
    private String requestNo;
    //商户订单号订单号集：使用^连接，总数不超过十笔
    private String orderNums;
    //订单信息
    private String orderInfo;
    //订单金额
    private String orderAmount;
    //收款方
    private String payee_name;

    private String order_trade_type;//查询时候查找的交易类型
    public PaymentContext(){}
    public PaymentContext(BaseContext baseContext) {
        super(baseContext);
    }

    public String getOrderNums() {
        return orderNums;
    }

    public void setOrderNums(String orderNums) {
        super.setOutTradeNumber(orderNums);
        this.orderNums = orderNums;
    }

    public String getRequestNo() {
        return requestNo;
    }

    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
    }

    public String getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(String orderAmount) {
        super.setAmount(orderAmount);
        this.orderAmount = orderAmount;
    }

    public String getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
    }

    public String getPayee_name() {
        return payee_name;
    }

    public void setPayee_name(String payee_name) {
        this.payee_name = payee_name;
    }

    public String getOrder_trade_type() {
        return order_trade_type;
    }

    public void setOrder_trade_type(String order_trade_type) {
        this.order_trade_type = order_trade_type;
    }
}
