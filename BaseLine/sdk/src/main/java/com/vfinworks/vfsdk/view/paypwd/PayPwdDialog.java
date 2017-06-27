package com.vfinworks.vfsdk.view.paypwd;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.activity.PeopleInfo.SafeCheckActivity;
import com.vfinworks.vfsdk.context.BaseContext;
import com.vfinworks.vfsdk.enumtype.PaymentPwdEnum;

public class PayPwdDialog extends Dialog {
	
    private PayPwdView mVFPaymentView;
    private PayPwdView.OnStatusChangeListener mOnStatusChangeListener;
    private boolean mCancelable = true;
    private BaseContext mBaseContext;
    private Context mContext;

    public PayPwdDialog(Context context,BaseContext baseContext) {
        super(context, R.style.vf_sdk_transparent_noanim);
        mBaseContext=baseContext;
        mContext=context;
        mVFPaymentView = new PayPwdView(context);
        setContentView(mVFPaymentView
                ,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));

        setCancelable(true);
        mVFPaymentView.setOnForgetPwdListener(new PayPwdView.OnForgetPwdListener() {
            @Override
            public void OnForgetPwd() {
                Intent setpayintent = new Intent(mContext, SafeCheckActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("context", mBaseContext);
                bundle.putSerializable("type", PaymentPwdEnum.SETPASSWORD);
                setpayintent.putExtras(bundle);
                mContext.startActivity(setpayintent);
            }
        });
    }

    @Override
    public void show() {
        try {
        	reset();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.show();
    }
    
    public void reset() {
    	mVFPaymentView.reset();
    }

    @Override
    public void setCancelable(boolean flag) {
        mCancelable = flag;
        super.setCancelable(flag);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK && event.getRepeatCount() == 0 && mCancelable) {
            if (mOnStatusChangeListener != null) {
                mOnStatusChangeListener.onUserCanel();
                dismiss();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    
    /**
     * 设置金额
     * @param money
     */
    public void setPayMoney(String money) {
    	mVFPaymentView.setPayMoney(money);
    }
    
    /**
     * 设置状态变化的监听
     * @param listener
     */
    public void setOnStatusChangeListener(PayPwdView.OnStatusChangeListener listener) {
        mOnStatusChangeListener = listener;
        mVFPaymentView.setOnStatusChangeListener(listener);
    }
}
