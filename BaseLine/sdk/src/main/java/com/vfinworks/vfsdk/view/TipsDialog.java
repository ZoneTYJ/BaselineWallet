package com.vfinworks.vfsdk.view;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;


/**
 * Created by tangyijian on 2016/5/27.
 */
public class TipsDialog extends Dialog{
    private OnCompleteListener mOnCompleteListener = null;
    private String content = null;

    private boolean mCancelable = true;
    private String title;
    private TextView tv_dialog_title;
    private TextView tvContent;

    public TipsDialog(Context context) {
        super(context, R.style.vfDialog);
        setCancelable(true);
        init();
    }
    public TipsDialog(Context context, String content, String title) {
        super(context, R.style.vfDialog);
        setCancelable(true);
        this.title = title;
        this.content = content;
    }

    public TipsDialog setTitleString(String title) {
        this.title = title;
        return this;
    }
    public TipsDialog setContent(String content) {
        this.content = content;
        return this;
    }

    private void init() {
        setContentView(R.layout.dialog_tips);
        tv_dialog_title = (TextView) findViewById(R.id.tv_dialog_title);
        tvContent = (TextView) findViewById(R.id.text_content);
        /**
         * 取消按钮
         */
        Button btncancel = (Button) findViewById(R.id.btn_cancel);
        btncancel.setVisibility(View.VISIBLE);
        btncancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(mOnCompleteListener!=null) {
                    mOnCompleteListener.onCancel();
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
        if (keyCode == event.KEYCODE_BACK && event.getRepeatCount() == 0 && mCancelable) {
            dismiss();
            if (mOnCompleteListener != null) {
                mOnCompleteListener.onCancel();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 设置状态变化的监听
     * @param listener
     */
    public void setOnCompleteListener(OnCompleteListener listener) {
        mOnCompleteListener = listener;
    }

    public static interface OnCompleteListener {
        void onCancel();
    }

}
