package com.vfinworks.vfsdk.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.vfinworks.vfsdk.common.L;
import com.vfinworks.vfsdk.common.ResourceUtil;


/**
 * @package com.netfinworks.wallet.view
 * @description:
 * @version v1.0
 * @author JackieCheng
 * @email xiaming5368@163.com
 * @date 2014-11-25 上午10:12:28
 */
public class SlipButton extends View implements OnTouchListener {
	private Context context;

	/** 记录当前按钮是否打开,true为打开,flase为关闭 */
	private boolean NowChoose = false;

	private boolean isChecked;
	/** 记录用户是否在滑动的变量 */
	private boolean OnSlip = false;
	/** 按下时的x,当前的x */
	private float DownX, NowX;
	/** 打开和关闭状态下,游标的Rect */
	private Rect Btn_On, Btn_Off;

	private boolean isChgLsnOn = false;

	private OnChangedListener ChgLsn;

	private Bitmap bg_on, bg_off, slip_btn;

	public SlipButton(Context context) {
		this(context,null);
	}

	public SlipButton(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public SlipButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	/**
	 * 初始化
	 * @param context
	 */
	private void init(Context context) {

		bg_on = BitmapFactory.decodeResource(getResources(),
				ResourceUtil.getDrawableResource(context,"split_pressed"));
		bg_off = BitmapFactory.decodeResource(getResources(),
				ResourceUtil.getDrawableResource(context,"split_normal"));
		slip_btn = BitmapFactory.decodeResource(getResources(),
				ResourceUtil.getDrawableResource(context,"split_click"));
		Btn_On = new Rect(0, 0, slip_btn.getWidth(), slip_btn.getHeight());
		Btn_Off = new Rect(bg_off.getWidth() - slip_btn.getWidth(), 0,
				bg_off.getWidth(), slip_btn.getHeight());
		// 设置监听器,也可以直接复写OnTouchEvent
		setOnTouchListener(this);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		super.onDraw(canvas);

		Matrix matrix = new Matrix();
		Paint paint = new Paint();
		float x;
		// 滑动到前半段与后半段的背景不同,在此做判断
		if (NowX < (bg_on.getWidth() / 2)) {
			x = NowX - slip_btn.getWidth() / 2;
			// 画出关闭时的背景
			canvas.drawBitmap(bg_off, matrix, paint);
		} else {
			x = bg_on.getWidth() - slip_btn.getWidth() / 2;
			// 画出打开时的背景
			canvas.drawBitmap(bg_on, matrix, paint);
		}
		// 是否是在滑动状态
		if (OnSlip) {
			// 是否划出指定范围,不能让游标跑到外头,必须做这个判断
			if (NowX >= bg_on.getWidth())
				// 减去游标1/2的长度...
				x = bg_on.getWidth() - slip_btn.getWidth() / 2;

			else if (NowX < 0) {
				x = 0;
			} else {
				x = NowX - slip_btn.getWidth() / 2;
			}
		} else {// 非滑动状态
			// 根据现在的开关状态设置画游标的位置
			if (NowChoose) {
				L.e("NowChoose111: ---->" + NowChoose);
				x = Btn_Off.left;
				// 初始状态为true时应该画出打开状态图片
				canvas.drawBitmap(bg_on, matrix, paint);
			} else {
				L.e("NowChoose222: ---->" + NowChoose);
				x = Btn_On.left;
				canvas.drawBitmap(bg_off, matrix, paint);
			}
		}
		if (isChecked) {
			L.e("isChecked111: ---->" + isChecked);
			canvas.drawBitmap(bg_on, matrix, paint);
			x = Btn_Off.left;
			isChecked = !isChecked;
		}  
		// 对游标位置进行异常判断...
		if (x < 0)
			x = 0;
		else if (x > bg_on.getWidth() - slip_btn.getWidth())
			x = bg_on.getWidth() - slip_btn.getWidth();
		// 画出游标.
		canvas.drawBitmap(slip_btn, x, 0, paint);

	}

	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction())
		// 根据动作来执行代码

		{
		case MotionEvent.ACTION_MOVE:// 滑动
			NowX = event.getX();
			break;

		case MotionEvent.ACTION_DOWN:// 按下

			if (event.getX() > bg_on.getWidth()
					|| event.getY() > bg_on.getHeight())
				return false;
			OnSlip = true;
			DownX = event.getX();
			NowX = DownX;
			break;

		case MotionEvent.ACTION_CANCEL: // 移到控件外部
			OnSlip = false;
			boolean choose = NowChoose;
			if (NowX >= (bg_on.getWidth() / 2)) {
				NowX = bg_on.getWidth() - slip_btn.getWidth() / 2;
				NowChoose = true;
			} else {
				NowX = NowX - slip_btn.getWidth() / 2;
				NowChoose = false;
			}
			// 如果设置了监听器,就调用其方法..
			if (isChgLsnOn && (choose != NowChoose)) 
				ChgLsn.OnChanged(NowChoose);
			break;
			// 松开
		case MotionEvent.ACTION_UP:

			OnSlip = false;
			boolean LastChoose = NowChoose;

			if (event.getX() >= (bg_on.getWidth() / 2)) {
				NowX = bg_on.getWidth() - slip_btn.getWidth() / 2;
				NowChoose = true;
			}

			else {
				NowX = NowX - slip_btn.getWidth() / 2;
				NowChoose = false;
			}
			 // 如果设置了监听器,就调用其方法..
			if (isChgLsnOn && (LastChoose != NowChoose))

				ChgLsn.OnChanged(NowChoose);
			break;
		default:
		}
		// 重画控件
		invalidate();
		return true;
	}

	/**
	 * 设置监听器,当状态修改的时候
	 * @param l
	 */
	public void setOnChangedListener(OnChangedListener l) {
		isChgLsnOn = true;
		ChgLsn = l;
	}

	public interface OnChangedListener {
		abstract void OnChanged(boolean CheckState);
	}

	public void setCheck(boolean isChecked) {
		this.isChecked = isChecked;
		NowChoose = isChecked;
	}

}
