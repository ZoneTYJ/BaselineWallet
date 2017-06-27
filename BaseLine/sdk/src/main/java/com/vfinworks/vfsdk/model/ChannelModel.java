package com.vfinworks.vfsdk.model;

import java.io.Serializable;

/**
 * Created by tangyijian on 2016/5/19.
 */
public class ChannelModel implements Serializable {
    public static final int OTHER_QPAY = 700;
    public String inst_code = "null";
    public String pay_mode;
    public String target_inst_code;
    //余额渠道余额
    public String amount;


    public ChannelModel() {}

    public ChannelModel(String inst_code, String pay_mode) {
        this.inst_code = inst_code;
        this.pay_mode = pay_mode;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ChannelModel) {
            ChannelModel c = (ChannelModel) o;
            return inst_code.equals(c.inst_code) && pay_mode.equals(c.pay_mode);
        } else {
            return super.equals(o);
        }
    }

    @Override
    public int hashCode() {
        String str = inst_code + pay_mode;
        return str.hashCode();
    }
}
