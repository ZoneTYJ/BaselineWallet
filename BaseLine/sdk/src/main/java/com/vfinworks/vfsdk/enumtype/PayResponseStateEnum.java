package com.vfinworks.vfsdk.enumtype;

/**
 * Created by xiaoshengke on 2016/4/14.
 */
public enum PayResponseStateEnum {
    S("交易成功"),
    P("处理中"),
    F("交易失败");

    private final String desc;

    PayResponseStateEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
