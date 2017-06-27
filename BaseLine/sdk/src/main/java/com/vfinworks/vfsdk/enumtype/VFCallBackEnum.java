package com.vfinworks.vfsdk.enumtype;

/**
 * 列表item的类型
 */
public enum VFCallBackEnum{
	OK("200"),                   // 成功
	PROCESS("201"),             //处理中
	ERROR_CODE_NETWORK("100"),           // 网路异常
	ERROR_CODE_EXCEPTION("101"),         // 处理异常
	ERROR_CODE_USER_CANCEL("102"),       // 用户主动取消流程
	ERROR_CODE_BUSINESS("105"),          // 业务逻辑出错
    ERROR_CODE_NULL("106");     //返回数据空

    private String code;
    
    private VFCallBackEnum(String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }

	public static VFCallBackEnum getByCode(String code){
        for(VFCallBackEnum item : values()){
            if(item.getCode().equalsIgnoreCase(code)){
                return item;
            }
        }
        return null;
    }
}
