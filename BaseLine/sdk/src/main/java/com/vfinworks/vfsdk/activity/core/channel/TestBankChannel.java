package com.vfinworks.vfsdk.activity.core.channel;

import android.widget.ImageView;

import com.vfinworks.vfsdk.model.ChannelModel;

/**
 * Created by tangyijian on 2016/10/28.
 */
//@ChannelAnnotation(inst_code = "TESTBANK10101",pay_mode = "NETBANK")
public class TestBankChannel extends EbankChannel {

    @Override
    public String getName(ChannelModel channel) {
        return "测试银行";
    }

    @Override
    public void setDrawableIcon(ImageView iv_icon) {
        return;
    }
}
