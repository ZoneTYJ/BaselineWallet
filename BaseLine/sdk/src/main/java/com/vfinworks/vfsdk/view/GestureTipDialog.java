package com.vfinworks.vfsdk.view;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;


/**
 * 手势失效提示
 * Created by tangyijian on 2016/5/27.
 */
public class GestureTipDialog extends Dialog{
    private View.OnClickListener mOnCompleteListener = null;
    private String content = null;

    private boolean mCancelable = true;
    private String title;
    private TextView tv_dialog_title;
    private TextView tvContent;

    public GestureTipDialog(Context context) {
        super(context, R.style.vfDialog);
        setCancelable(true);
        init();
    }
    public GestureTipDialog(Context context, String content, String title) {
        super(context, R.style.vfDialog);
        setCancelable(true);
        this.title = title;
        this.content = content;
    }

    public GestureTipDialog setTitleString(String title) {
        this.title = title;
        return this;
    }
    public GestureTipDialog setContent(String content) {
        this.content = content;
        return this;
    }

    private void init() {
        setContentView(R.layout.dialog_tips);
        tv_dialog_title = (TextView) findViewById(R.id.tv_dialog_title);
        tvContent = (TextView) findViewById(R.id.text_content);

        tv_dialog_title.setText("手势密码已失效");
        tvContent.setText("请重新登录");
        /**
         * 取消按钮
         */
        Button btncancel = (Button) findViewById(R.id.btn_cancel);
        btncancel.setText("重新登录");
        btncancel.setVisibility(View.VISIBLE);
        btncancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(mOnCompleteListener!=null) {
                    mOnCompleteListener.onClick(v);
                }
                dismiss();
            }
        });

    }

    @Override
    public void show() {
        if(title!=null) {
            tv_dialog_title.setText(title);
        }
        if(content!=null) {
            tvContent.setText(content);
        }
        super.show();
    }

    @Override
    public void setCancelable(boolean flag) {
        mCancelable = flag;
        super.setCancelable(flag);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }

    /**
     * 设置状态变化的监听
     * @param listener
     */
    public void setOnCompleteListener(View.OnClickListener listener) {
        mOnCompleteListener = listener;
    }



}
