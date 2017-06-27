package com.vfinworks.vfsdk.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;


/**
 * 实名认证提示
 * Created by xiaoshengke on 2016/4/12.
 */
public class RealConfirmDialog extends Dialog {
    private Context context;
    private TextView tv_ccd_cancel;
    private TextView tv_ccd_call;
    private TextView tv_message;
    private String text;
    private String rightText;

    public RealConfirmDialog(Context context) {
        this(context, 0);
    }

    public RealConfirmDialog(Context context, int themeResId) {
        super(context, R.style.CustomDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_call_confirm);
        bindViews();
        initListener();
    }

    private void bindViews() {
        tv_ccd_cancel = (TextView) findViewById(R.id.tv_ccd_cancel);
        tv_ccd_call = (TextView) findViewById(R.id.tv_ccd_call);
        tv_message = (TextView) findViewById(R.id.tv_message);

        if(text != null){
            tv_message.setText(text);
        }
        if(rightText != null){
            tv_ccd_call.setText(rightText);
        }

        setRightVisible(rightVisible);
    }

    private void initListener() {
        tv_ccd_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        if(sureListener != null)
            tv_ccd_call.setOnClickListener(sureListener);
        else
            throw new RuntimeException("CallConfirmDialog的sureListener为空");
    }

    public void setMessage(String text){
        this.text = text;
        if(tv_message != null){
            tv_message.setText(text);
        }
    }

    public void setRightBtnText(String rightBtnText){
        rightText = rightBtnText;
        if(tv_ccd_call != null){
            tv_ccd_call.setText(rightBtnText);
        }
    }

    public void setSureClickListener(View.OnClickListener sureClickListener){
        sureListener = sureClickListener;
        if(tv_ccd_call != null){
            tv_ccd_call.setOnClickListener(sureListener);
        }
    }

    private View.OnClickListener sureListener;
    private boolean rightVisible = true;

    public void setRightVisible(boolean b) {
        rightVisible = b;
        if(tv_ccd_call != null) {
            if (b) {
                tv_ccd_call.setVisibility(View.VISIBLE);
            } else {
                tv_ccd_call.setVisibility(View.GONE);
            }
        }
    }
}
