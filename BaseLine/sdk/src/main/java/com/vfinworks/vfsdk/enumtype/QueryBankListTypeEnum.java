package com.vfinworks.vfsdk.enumtype;

//qpay， normal， all
public enum QueryBankListTypeEnum{
	QPAY("qpay"), //   充值卡            
	NORMAL("normal"),  //提现卡      
	ALL("all");//全部
	

    private String code;
    
    private QueryBankListTypeEnum(String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }

	public static QueryBankListTypeEnum getByCode(String code){
        for(QueryBankListTypeEnum item : values()){
            if(item.getCode().equalsIgnoreCase(code) == true){
                return item;
            }
        }
        return null;
    }
}
