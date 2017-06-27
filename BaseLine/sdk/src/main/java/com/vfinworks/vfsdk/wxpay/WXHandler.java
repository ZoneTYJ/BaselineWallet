package com.vfinworks.vfsdk.wxpay;

import android.os.Handler;
import android.os.Message;

/**
 * Created by tangyijian on 2017/1/4.
 */
public class WXHandler extends Handler {
    public static int OK=1;
    private static WXHandler mWXHandler;
    private WXRunnable mRunnable;

    public static WXHandler getInstance(){
        if(mWXHandler==null){
            mWXHandler=new WXHandler();
        }
        return mWXHandler;
    }

    public void setRunnnable(WXRunnable r){
//        mRunnable = new WeakReference<Runnable>(r);
    }

    public void attachRunnable(WXRunnable r){
        mRunnable = r;
    }

    public void detachRunnable(){
        mRunnable = null;
    }

    @Override
    public void handleMessage(Message msg) {
        if(msg.what==OK){
            if(mRunnable!=null) {
                mRunnable.setMessage(msg);
                mRunnable.run();
            }
        }
    }
}
