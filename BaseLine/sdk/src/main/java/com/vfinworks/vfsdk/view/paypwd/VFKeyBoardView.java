package com.vfinworks.vfsdk.view.paypwd;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.vfinworks.vfsdk.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**    
 * Created by cheng on 14/12/15 数字键盘安全控件
 */
public final class VFKeyBoardView extends View {
	/** 行数 */
	private int mRowCount = 4;
	/** 列数 */
	private int mColumnCount = 3;

	private int itemHeight;

	private int itemWidth;
 
	/** 字体大小 */
	private static int mTetxSize = 20;
 
	private OnKeyBoardClickedListener mKeyListener;
	/** 线条画笔 */
	private Paint mSeperatorPaint;
	/** 数字画笔 */
	private Paint mDigitalPaint;
	/** Button画笔 */
	private Paint mBtPaint;

	private int position = -1;
	
	private String mPassword = "";
	
	private Context context;
	 
	private static final int ARRAY_LENGTH = 12;
	private static final int DELETE_POSITION = 11;
	private static final int NULL_POSITION = 9;
	
	private List<String> mListStr = null;
	private List<SecurityButton> mListButton = null;

	
	private static class SecurityButton {
		private Rect rect; 
		private String value;  
		private float nameXOffset;
		private float nameYOffset;
		private float textWidth;
		private float textHeight;
		private Bitmap bg = null;
		
		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public SecurityButton() {
			
		}

		boolean isInside(Point point) {
			return this.rect.contains(point.x, point.y);
		}

		void drawButtonSelf(Canvas canvas, Paint paint) {
			canvas.drawRect(rect, paint);
			if(bg != null) {
				int startX = rect.left + (rect.width() - bg.getWidth()) / 2;
				int startY = rect.top + (rect.height() - bg.getHeight()) / 2;
				canvas.drawBitmap(bg, startX, startY, null);
			}
		}

		void drawTextSelf(Canvas canvas, Paint paint) { 
			Rect textRect = new Rect();
			paint.getTextBounds(value, 0, value.length(), textRect);
			textWidth = textRect.width();
			textHeight = textRect.height();

			nameXOffset = rect.left + (rect.width() - textWidth) / 2;
			nameYOffset = rect.top + (rect.height() - textHeight) / 2
					+ textHeight; 
			
			canvas.drawText(value, nameXOffset, nameYOffset, paint);
		}
	}

	/**
	 * @param context
	 */
	public VFKeyBoardView(Context context) {
		this(context, null);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public VFKeyBoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
        this.context = context;  
		initSource();
    }

	/**
	 * 初始化 
	 * @param viewWidth
	 * @param viewHeight
	 */
	protected void init() {
		// 画横线
		itemHeight = getMeasuredHeight() / mRowCount;
		// 画竖线
		itemWidth = getMeasuredWidth() / mColumnCount;

		mSeperatorPaint = new Paint();
		// 设置线条画笔颜色
		mSeperatorPaint.setColor(Color.parseColor("#eeeeee"));
//		mSeperatorPaint.setColor(Color.rgb(196, 200, 204));
		mSeperatorPaint.setAntiAlias(true);
		mSeperatorPaint.setStrokeWidth(1);

		mDigitalPaint = new Paint();
		// 设置数字画笔颜色
		mDigitalPaint.setColor(Color.rgb(0, 0, 0));
		mDigitalPaint.setAntiAlias(true);
		mDigitalPaint.setStyle(Paint.Style.STROKE);
		mDigitalPaint.setTextSize(getTextSize(context, mTetxSize));

		mBtPaint = new Paint();
		// 设置Button画笔颜色
		mBtPaint.setColor(Color.WHITE);
//		mBtPaint.setColor(Color.rgb(246, 246, 246));
		mBtPaint.setAntiAlias(true);
 	 	     
		for (int i = 0; i < mListButton.size(); i++) {
			int rowIndex = i / mColumnCount;    // 行
			int columnIndex = i % mColumnCount; // 列

			int xBtnOffset = columnIndex * itemWidth;
			int yBtnOffset = rowIndex * itemHeight;
			Rect rect = new Rect(xBtnOffset, yBtnOffset,
					xBtnOffset + itemWidth, yBtnOffset + itemHeight);
						
			SecurityButton button = mListButton.get(i);
			button.rect = rect;
		}      
	}
	
	/**
	 * 初始化随机数字
	 */
	private void initSource() {
		mListStr = new ArrayList<String>();
		mListButton = new ArrayList<SecurityButton>();
		
		for(int i = 0; i < ARRAY_LENGTH - 2; i++) {
			mListStr.add("" + i);			 
		} 
		Collections.shuffle(mListStr);
		mListStr.add(9, "");//会把当前的和后面的值往后移位
		mListStr.add(11, "");//删除按钮 
		
		for(int j = 0; j < ARRAY_LENGTH; j++) {
			SecurityButton button = new SecurityButton();
			if(j == ARRAY_LENGTH - 1) {//删除键
				button.bg = BitmapFactory.decodeResource(getResources(), R.drawable.vf_sdk_delete_icon);
			}
			button.setValue(mListStr.get(j));
			mListButton.add(button);
		}
	}

	@SuppressLint("NewApi")
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		WindowManager wm = (WindowManager) this.getContext().getSystemService(
				Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int screenWidth = size.x;
		int screenHeight = size.y;

		heightMeasureSpec = MeasureSpec.makeMeasureSpec(
				(int) (screenHeight * 0.3f), MeasureSpec.EXACTLY);
		widthMeasureSpec = MeasureSpec.makeMeasureSpec(screenWidth,
				MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.WHITE);
		init();
		int mWidth = this.getWidth();
		int mHeight = this.getHeight();
		
		// 数字键盘上数字的点击处理
		for (int i = 0; i < mListButton.size(); i++) { 
			if (position == i) {
				mBtPaint.setColor(Color.rgb(185, 190, 195));
			} else {
				if(i == 9 || i == 11) {//空白格和删除键背景色
					mBtPaint.setColor(Color.parseColor("#eeeeee"));
//					mBtPaint.setColor(Color.rgb(213, 216, 219));
				}else {
					mBtPaint.setColor(Color.WHITE);
//					mBtPaint.setColor(Color.rgb(246, 246, 246));
				}
			} 			
			SecurityButton button = mListButton.get(i); 			
			// 画Button
			button.drawButtonSelf(canvas, mBtPaint);
			// 画数字
			button.drawTextSelf(canvas, mDigitalPaint);
		}

		// 画线
		for (int i = 1; i <= mRowCount; i++) {
			canvas.drawLine(0, i * itemHeight, mWidth, i * itemHeight,
					mSeperatorPaint);
			for (int j = 1; j <= mColumnCount; j++) {
				canvas.drawLine(j * itemWidth, 0, j * itemWidth, mHeight,
						mSeperatorPaint);
			}
		}
	}
	
	@Override 
	public boolean dispatchTouchEvent(MotionEvent event) {  
		Point point = new Point((int) event.getX(), (int) event.getY());
		for (int i = 0; i < mListButton.size(); i++) {	
			SecurityButton button = mListButton.get(i); 
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: 
				if (button.isInside(point)) {  
					position = i; 
					postInvalidate();
				}
				break;
			case MotionEvent.ACTION_UP:
				position = -1;
				postInvalidate();
				if (button.isInside(point)) {  
					if (i == NULL_POSITION)break;
					if (i == DELETE_POSITION) {
						// 当点击的字符串为delete的时候
						if (mPassword.length() > 0) { 
							mPassword = mPassword.substring(0, mPassword.length() - 1);   
						}
					} else {
						if (mPassword.length() < 6) {
							mPassword += button.getValue(); 
						}
					} 
                    mKeyListener.onValueChanged(encodePassword(mPassword)); 
				}			
				break;
			default:break;
			}			
		} 
		return true;  
	}
	
	public VFEncryptData encodePassword(String plaintext) {
        VFEncryptData result = new VFEncryptData();
        result.setCiphertext(mPassword);
        result.setLength(mPassword.length());
        return result;
    }

    /**
     * 恢复到初始状态
     */
    public void reset() {
        mPassword = "";
    }
 
	public void setOnKeyBoardClickedListener(OnKeyBoardClickedListener listener) {
		mKeyListener = listener;
	}

	public static interface OnKeyBoardClickedListener { 
        void onValueChanged(VFEncryptData result); 
	}
	
	public static int getTextSize(Context context, int spValue) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
				spValue, context.getResources().getDisplayMetrics());
	}
	
}
