package com.vfinworks.vfsdk.view.paypwd;
 
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

/**    
 * Created by cheng on 14/12/15 密码键盘安全控件
 */
public class VFPasswordView extends View {

    /**
     * 密码长度
     */
    private static int mPwdLength = 6;
    /**
     * 每个矩形之间的间距
     */
    private float mItemPaddding = 0;
    /**
     * 内容的padding，内容距边界的距离
     */
    private int contentPadding = mLineWidth;
    /**
     * 矩形的线的宽度
     */
    private static int mLineWidth = 1;
    /**
     * 矩形画笔
     */
    private static Paint mRectPaint;
    /**
     * 数字画笔
     */
    private static Paint textPaint;

    private SecurityText[] mTexts = null;
    private SecurityText mText;
    private OnPwdChangeedListener mChangedListener;

    private int mCount = 0;
 
    private View mParent;

    private Context context;

    private float elementWidth;

    private final class SecurityText {

        private Rect rect;

        public SecurityText() {

        }

        void drawSelf(Canvas canvas) {
            canvas.drawRect(rect, mRectPaint);
        }

        void drawSatr(Canvas canvas) {
            float x = rect.exactCenterX();
            float y = rect.exactCenterY();
            canvas.drawCircle(x, y, dip2px(context, 5.0f), textPaint);
        }
    }

    /**
     * @param context
     * @param attrs
     */
    public VFPasswordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context; 
    }

    private void init() {
        mTexts = new SecurityText[mPwdLength];
        mRectPaint = new Paint();
        mRectPaint.setColor(Color.parseColor("#cccccc"));
        mRectPaint.setAntiAlias(true);
        mRectPaint.setStrokeWidth(mLineWidth);
        mRectPaint.setStyle(Paint.Style.STROKE);

        textPaint = new Paint();
        textPaint.setColor(Color.rgb(0, 0, 0));
        textPaint.setAntiAlias(true);

        int elementHeight = getMeasuredHeight() - contentPadding * 2;
        float left = contentPadding;
        for (int i = 0; i < mPwdLength; i++) {
            Rect rect = new Rect();
            rect.left = (int) (i * (elementWidth + mItemPaddding) + left) ;
            rect.right = (int) (rect.left + elementWidth);
            rect.top = contentPadding;
            rect.bottom = (int) (elementHeight +contentPadding);

            mText = new SecurityText();
            mTexts[i] = mText;
            mTexts[i].rect = rect;
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //计算出矩形的宽高
        elementWidth =  ((float) (getMeasuredWidth() - (mItemPaddding * (mPwdLength - 1)) - contentPadding*2) / mPwdLength);
        float height = elementWidth * 0.8f  + contentPadding*2;
        setMeasuredDimension((int) getMeasuredWidth(), (int) height); 
        init();
    }


    /*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < mPwdLength; i++) {
            mTexts[i].drawSelf(canvas);
        } 
        if (mCount > 0 && mCount <= mPwdLength) {
            for (int j = 0; j < mCount; j++) {
                mTexts[j].drawSatr(canvas);
            }
        }
    }

    public void setStarCount(int count) {
        this.mCount = count;
        invalidate();
    }

    public void setParent(View parent) {
        this.mParent = parent;
        invalidate();
    }

    public void setOnPwdChangeedListener(OnPwdChangeedListener listener) {
        mChangedListener = listener;
    }

    public static interface OnPwdChangeedListener {
        void onValueChanged(VFEncryptData result);
    }

    /**
     * 根据手机的屏幕属性从 dip 的单位 转成为 px(像素)
     *
     * @param context
     * @param value
     * @return
     */
    public static float dip2px(Context context, float value) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return value * metrics.density;
    }
}