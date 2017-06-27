package com.vfinworks.vfsdk.common;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;


/**
 * Created by cheng on 15/1/21 信息提示对话框
 */
public class VFShowDialog extends Dialog {
 
	private OnCompleteListener mOnCompleteListener = null;
	private String content = null;
	private String okText = null;

	private boolean mCancelable = true;

	public VFShowDialog(Context context) {
		super(context, R.style.vf_sdk_transparent);
		setCancelable(false);
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public void setOKText(String text) {
		this.okText = text;
	}
	
	@Override
	public void show() {
		super.show();
		setContentView(R.layout.vf_sdk_alert_dialog);
		TextView tvContent = (TextView) findViewById(R.id.text_content);
		tvContent.setText(content);
		 
		View okView = findViewById(R.id.my_alert_dialog_ok_layout);
		TextView tvOk = (TextView) findViewById(R.id.dialog_textview);
		tvOk.setText(okText);
		okView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) { 
				mOnCompleteListener.onComplete(); 
			}
		});
		  
		/**
		 * 取消按钮
		 */
		View cancelView = findViewById(R.id.my_alert_dialog_cancel_layout);
		cancelView.setVisibility(View.VISIBLE);
		cancelView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mOnCompleteListener.onCancel();
			}
		});
	}
	
	@Override
    public void setCancelable(boolean flag) {
        mCancelable = flag;
        super.setCancelable(flag);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK && event.getRepeatCount() == 0 && mCancelable) {
            if (mOnCompleteListener != null) {
            	mOnCompleteListener.onCancel();
                dismiss();
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
		void onComplete();

		void onCancel();
	}

}
