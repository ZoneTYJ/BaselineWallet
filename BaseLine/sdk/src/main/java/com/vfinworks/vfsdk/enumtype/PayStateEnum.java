package com.vfinworks.vfsdk.enumtype;

/**
 * Created by xiaoshengke on 2016/4/11.
 */
public enum PayStateEnum {
    WAIT_PAY("待支付"),
    WAIT_BUYER_PAY("买家付款提交中"),
    BUYER_PAYED("买家已支付"),
    PAY_FINISHED("支付成功"),
    PAY_SUCCESS("付款成功"),
    TRANSFER_SUCCESS("转账成功"),
    TRADE_FINISHED("交易结束(成功)"),
    TRADE_FAILED("交易结束(失败)"),
    TRADE_SUCCESS("交易成功"),
    PAY_BUYER_REFUND("退买家付款"),
    REFUND_SUCCESS("退款成功"),
    REFUND_FINISH("退款完成"),
    REFUND_FAIL("退款失败"),
    TRADE_CLOSED("交易关闭"),
    DEPOSIT_SUCCESS("充值成功"),
    WITHDRAWAL_SUBMIT("出款、提现申请"),
    WITHDRAWAL_SUCCESS("出款、提现成功"),
    WITHDRAWAL_FAILED("出款、提现失败"),
    FREEZE_PROCESS("冻结或解冻处理中"),
    FREEZE_FAILED("冻结或解冻失败"),
    FREEZE_SUCCESS("冻结或解冻成功");


    private String desc;

    PayStateEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public static boolean isWaitPay(String desc){
        return WAIT_PAY.desc.equals(desc);
    }

    public static boolean isDealAccept(String stateDesc) {
        return stateDesc.contains(PAY_FINISHED.desc);
    }

    public static boolean isFinish(String trade_status){
        if(trade_status.contains("FINISHED")
                || trade_status.equals(DEPOSIT_SUCCESS.toString())){
            return  true;
        }else {
            return false;
        }
    }
    public static boolean isWait(String trade_status){
        return trade_status.equals("requery")|| trade_status.equals(WAIT_PAY.toString());
    }
}
