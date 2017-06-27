package com.vfinworks.vfsdk.view;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.common.Utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 加载dialog，VFinActivity中加载数据时的dialog就是此dialog
 *
 */
@SuppressLint("InlinedApi")
public class LoadingDialog extends Dialog {
	private static final int CONTAINER_WIDTH_HEIGHT_DIP = 125;
	
	private Context mContext;
	private View mContentView;
	private TextView mTextView;
	
	private boolean mCancelable = true;

	public LoadingDialog(Context context) {
		super(context, R.style.loadstyle);
		this.mContext = context;
		prepareContentView();
		setContentView(mContentView,getContentViewLayoutParams());
		setCancelable(true);
	}

	private LayoutParams getContentViewLayoutParams() {
		int width = (int) Utils.getInstance().dip2px(mContext, CONTAINER_WIDTH_HEIGHT_DIP);
		int height = width;
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
		params.gravity = Gravity.CENTER;
		return params;
	}
	private LayoutParams getTextViewLayoutParams() {
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		int margin = (int) Utils.getInstance().dip2px(mContext, 10);
		params.leftMargin = margin;
		params.rightMargin = margin;
		params.topMargin = margin;
		return params;
	}

	private void prepareContentView() {
		LinearLayout container = new LinearLayout(mContext);
		container.setOrientation(LinearLayout.VERTICAL);
		container.setGravity(Gravity.CENTER);
		
		ProgressBar progressBar = new ProgressBar(mContext);
		progressBar.setIndeterminateDrawable(ContextCompat.getDrawable(mContext,R.drawable.vf_progress_small));
		container.addView(progressBar, 0, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		mTextView = new TextView(mContext);
		mTextView.setTextSize(14);
		mTextView.setTextColor(Color.WHITE);
		mTextView.setSingleLine(true);
		mTextView.setEllipsize(TextUtils.TruncateAt.END);
		mTextView.setText("请稍等...");
	
		container.addView(mTextView, 1, getTextViewLayoutParams());
		mContentView = container;
	}
	
	/**
	 * 设置标题文字
	 */
	public void setTitle(CharSequence text) {
		mTextView.setText(text);
	}
	/**
	 * 设置标题文字
	 */
	public void setTitle(int resId) {
		mTextView.setText(getContext().getString(resId));
	}
	
	@Override
    public void setCancelable(boolean flag) {
        mCancelable = flag;
        super.setCancelable(flag);
    }
	 
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && mCancelable) {
            return true; 
        }
        return super.onKeyDown(keyCode, event);
    }
	
}
