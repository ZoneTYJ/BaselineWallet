package com.vfinworks.vfsdk.enumtype;

//create_instant_trade  即时到帐交易   
//
public enum VFOrderTypeEnum{
	//create_instant_trade  即时到帐交易   
	TRADE_INSTANT(1,"create_instant_trade"),     
	//create_ensure_trade  担保交易
	TRADE_ENSURE(2,"create_ensure_trade");
	

    private int code;
    private String tradeService;
    
    private VFOrderTypeEnum(int code,String tradeSer){
        this.code = code;
        setTradeService(tradeSer);
    }

    public int getCode() {
        return code;
    }

	public static VFOrderTypeEnum getByCode(int code){
        for(VFOrderTypeEnum item : values()){
            if(item.getCode() == code){
                return item;
            }
        }
        return null;
    }

	public String getTradeService() {
		return tradeService;
	}

	public void setTradeService(String tradeService) {
		this.tradeService = tradeService;
	}
}
