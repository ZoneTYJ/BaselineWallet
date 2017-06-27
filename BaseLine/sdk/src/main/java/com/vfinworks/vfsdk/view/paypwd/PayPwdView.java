package com.vfinworks.vfsdk.view.paypwd;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.common.Utils;

public class PayPwdView extends LinearLayout implements View.OnClickListener {

    private Context mContext;
    private VFPasswordView pay_password_view;
    private VFKeyBoardView keyboard_view;
    private TextView tvMoney;
    private TextView tvDialogBank;
    private ImageView iv_cancel;
    private VFEncryptData mEncryptResult;
    private OnStatusChangeListener mOnStatusChangeListener;
    private OnForgetPwdListener mOnForgetPwdListener;

    private static final int PASS_LENGTH = 6;
    private TextView forget_psw;

    public PayPwdView(Context context) {
        this(context, null);
    }

    public PayPwdView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        inflate(context, R.layout.pay_pwd_view, this);
        initWidget();
    }

    /**
     * 初始化控件
     */
    private void initWidget() {
        forget_psw = (TextView) findViewById(R.id.forget_psw);
        forget_psw.setOnClickListener(this);
        pay_password_view = (VFPasswordView) findViewById(R.id.pay_password_view);
        keyboard_view = (VFKeyBoardView) findViewById(R.id.keyboard_view);
        tvMoney = (TextView) findViewById(R.id.my_alert_dialog_money);
        tvDialogBank = (TextView) findViewById(R.id.tv_dialog_bank);
        iv_cancel = (ImageView) findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(this);
        keyboard_view.setOnKeyBoardClickedListener(new VFKeyBoardView.OnKeyBoardClickedListener() {
            @Override
            public void onValueChanged(VFEncryptData result) {
                pay_password_view.setStarCount(result.getLength());
                mEncryptResult = result;
                if (result.getLength() == 6) {
                    mOnStatusChangeListener.onInputComplete(mEncryptResult);
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_cancel) {
            if (mOnStatusChangeListener != null) {
                mOnStatusChangeListener.onUserCanel();
            }
        }else if(v==forget_psw){
            if(mOnForgetPwdListener!=null){
                mOnForgetPwdListener.OnForgetPwd();
            }
        }
    }


    /**
     * 设置金额
     *
     * @param payMoney
     */
    public void setPayMoney(String payMoney) {
        if (null == payMoney) {
            tvMoney.setVisibility(View.GONE);
        } else {
            tvMoney.setVisibility(View.VISIBLE);
            tvMoney.setText(Utils.getInstance().formatMoney(payMoney) + "元");
        }
    }

    /**
     * 设置银行信息
     *
     * @param bankInfo
     */
    public void setBankInfo(String bankInfo) {
        if (null == bankInfo) {
            tvDialogBank.setVisibility(View.GONE);
        } else {
            tvDialogBank.setVisibility(View.VISIBLE);
            tvDialogBank.setText(bankInfo);
        }
    }

    /**
     * 设置状态变化的监听
     *
     * @param listener
     */
    public void setOnStatusChangeListener(OnStatusChangeListener listener) {
        mOnStatusChangeListener = listener;
    }

    public void setOnForgetPwdListener(OnForgetPwdListener listener){
        mOnForgetPwdListener=listener;
    }

    /**
     * 恢复到初始状态
     */
    public void reset() {
        pay_password_view.setStarCount(0);
        keyboard_view.reset();
    }
    public interface OnForgetPwdListener{
        void OnForgetPwd();
    }
    public interface OnStatusChangeListener {

        /**
         * 当密码输入完毕
         *
         * @param result
         */
        void onInputComplete(VFEncryptData result);

        /**
         * 用户点击取消按钮
         */
        void onUserCanel();

    }
}