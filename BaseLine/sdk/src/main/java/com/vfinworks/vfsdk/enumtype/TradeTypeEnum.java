package com.vfinworks.vfsdk.enumtype;

/**
 * Created by xiaoshengke on 2016/4/11.
 */
public enum TradeTypeEnum {
    ALL("全部", "ALL"),
    INSTANT("即时到账", "instantpay"),
    ENSURE("担保", "payment"),
    TRANSFER("转账到户", "transfer"),
    REFUND("退款", "refund"),
    DEPOSIT("充值", "recharge"),
    WITHDRAWAL("提现", "withdraw"),
    TRANSFER_CARD("转账到卡", "transferToCard");

    private String desc;
    private String code;

    TradeTypeEnum(String desc, String code) {
        this.desc = desc;
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public String getCode() {
        return code;
    }

    public static boolean isNeedPay(TradeTypeEnum t) {
        return t == TradeTypeEnum.INSTANT || t == TradeTypeEnum.ENSURE ||
                t == TradeTypeEnum.TRANSFER || t == TradeTypeEnum.TRANSFER_CARD;
    }

    public static TradeTypeEnum getTypeByCode(String key) {
        if (key == null) {
            return null;
        }
        for (TradeTypeEnum val : values()) {
            if (val.getCode().equals(key)) {
                return val;
            }
        }
        return null;
    }
}
