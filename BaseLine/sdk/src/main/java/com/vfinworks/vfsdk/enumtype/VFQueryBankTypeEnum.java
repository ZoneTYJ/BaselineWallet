package com.vfinworks.vfsdk.enumtype;

public enum VFQueryBankTypeEnum{
	QPAY("qpay"),                  
	NORMAL("normal"),        
	ALL("all");      
	

    private String code;
    
    private VFQueryBankTypeEnum(String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }

	public static VFQueryBankTypeEnum getByCode(String code){
        for(VFQueryBankTypeEnum item : values()){
            if(item.getCode().equalsIgnoreCase(code)){
                return item;
            }
        }
        return null;
    }
}
