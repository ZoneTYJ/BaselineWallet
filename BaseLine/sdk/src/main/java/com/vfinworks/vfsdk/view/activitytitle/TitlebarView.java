package com.vfinworks.vfsdk.view.activitytitle;

import com.vfinworks.vfsdk.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 标题栏view
 */
public class TitlebarView extends RelativeLayout {
	
	private static final String TAG = "TitlebarView";
	private LinearLayout layLeft;
	private TextView tvLeft;
	private TextView tvRight;
	private TextView tvTitle;
	private Context mContext;

	public TitlebarView(Context context) {
		super(context);
		init(context);
	}
	
	public TitlebarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	private void init(Context context) {
		this.mContext = context;
		View.inflate(context, R.layout.view_titlebar, this);
		layLeft = (LinearLayout)findViewById(R.id.layout_left);
		tvLeft = (TextView)findViewById(R.id.tv_back);
		tvRight = (TextView)findViewById(R.id.btn_right);
		tvTitle = (TextView)findViewById(R.id.tv_title);
	}
	
	public TitlebarView setTitle(String title) {
		tvTitle.setText(title);
		return this;
	}
	public TitlebarView setTitle(int resId) {
		tvTitle.setText(mContext.getString(resId));
		return this;
	}
	public TitlebarView initLeft(String text,OnClickListener onClickListener) { 
		tvLeft.setText(text);
		if (onClickListener != null) {
			layLeft.setOnClickListener(onClickListener);
		}
		return this;
	}
	
	public TitlebarView initLeft(OnClickListener onClickListener) { 
		if (onClickListener != null) {
			layLeft.setOnClickListener(onClickListener);
		}
		return this;
	}
	
	public TitlebarView setLeftVisible(boolean bVisible) {
		if(bVisible == true) {
			layLeft.setVisibility(View.VISIBLE);
		}else{
			layLeft.setVisibility(View.GONE);
		}
		
		return this;
	}
	
	public TitlebarView initRight(String text,OnClickListener onClickListener) { 
		tvRight.setText(text);
		tvRight.setVisibility(View.VISIBLE);
		if (onClickListener != null) {
			tvRight.setOnClickListener(onClickListener);
		}
		return this;
	}
	
	public TitlebarView initRight(int bgResId,OnClickListener onClickListener) { 
		tvRight.setBackgroundResource(bgResId);
		tvRight.setVisibility(View.VISIBLE);
		if (onClickListener != null) {
			tvRight.setOnClickListener(onClickListener);
		}
		return this;
	}
	
	
	/**
	 * 得到左边的标题栏按钮
	 * @return
	 */
	public TextView getRightBtn() {
		return tvRight;
	}
	
	/**
	 * 得到中间的TextView
	 * @return
	 */
	public TextView getTextView() {
		return tvTitle;
	}
}
