package com.vfinworks.vfsdk.enumtype;

//卡类型 1:借记卡；2：信用卡,3:支付宝，4：微信，5：银联,6:快捷，7：网银 8:余额
public enum VFCardTypeEnum{
	DEPOSIT(1), //   借记卡              
	CREDIT(2),        
	ZHIFUBAO(3),
	WEIXIN(4),
	YINLIAN(5),
	QPAY(6),
    EBANK(7),
    Overage(8);
	

    private int code;
    
    private VFCardTypeEnum(int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }

	public static VFCardTypeEnum getByCode(int code){
        for(VFCardTypeEnum item : values()){
            if(item.getCode() == code){
                return item;
            }
        }
        return null;
    }
}
