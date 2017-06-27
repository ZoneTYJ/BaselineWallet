package com.vfinworks.vfsdk.wxpay;

import android.os.Message;

/**
 * Created by tangyijian on 2017/5/17.
 */

public class WXRunnable implements Runnable {
    private Message mMessage;

    public Message getMessage() {
        return mMessage;
    }

    public void setMessage(Message message) {
        mMessage = message;
    }

    @Override
    public void run() {

    }
}
