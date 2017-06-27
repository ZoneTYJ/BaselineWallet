package com.vfinworks.vfsdk.context;

/**
 * Created by tangyijian on 2017/1/6.
 */
public class BaseAcquireContext extends BaseContext {
    //外部订单号
    private String outTradeNumber;
    //内部使用,不对外提供,金额
    private String amount;
    //内部订单号.不需要外部提供，支付的时候返回
    private String innerOrderNo;

    public BaseAcquireContext(){

    }

    public BaseAcquireContext(BaseContext context){
        super(context);
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getOutTradeNumber() {
        return outTradeNumber;
    }

    public void setOutTradeNumber(String outTradeNumber) {
        this.outTradeNumber = outTradeNumber;
    }

    public String getInnerOrderNo() {
        return innerOrderNo;
    }

    public void setInnerOrderNo(String innerOrderNo) {
        this.innerOrderNo = innerOrderNo;
    }
}
