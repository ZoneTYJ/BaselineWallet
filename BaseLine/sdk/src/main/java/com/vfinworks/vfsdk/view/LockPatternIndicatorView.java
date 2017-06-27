package com.vfinworks.vfsdk.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

/**
 * 手势图案指示器
 * Created by xiaoshengke on 2016/9/14.
 */
public class LockPatternIndicatorView extends View{
    private boolean[][] mPatternSelect = new boolean[3][3];

    private int defaultColor = Color.argb(255, 173, 216, 230);
    private int selectColor = Color.argb(255, 0, 191, 255);
    /**
     * 最小半径
     */
    private final float MIN_RADIUS = 5;
    /**
     * 圆之间的空隙
     */
    private final float space;
    private float radius;
    private float density;
    private Paint mAirPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mFullPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public LockPatternIndicatorView(Context context) {
        this(context,null);
    }

    public LockPatternIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        density = getResources().getDisplayMetrics().density;
        space = 5 * density;
        radius = MIN_RADIUS * density;

        mAirPaint.setStyle(Paint.Style.STROKE);
        mAirPaint.setStrokeWidth(0);
        mAirPaint.setColor(defaultColor);

        mFullPaint.setStyle(Paint.Style.FILL);
        mFullPaint.setColor(selectColor);
    }

    /**
     * 根据选中的点更新该指示器
     * @param selectPattern
     */
    public void updatePatternSelect(ArrayList<LockPatternView.Cell> selectPattern){
        clearPattern();
        for(int i = 0; i < selectPattern.size(); i++){
            LockPatternView.Cell cell = selectPattern.get(i);
            mPatternSelect[cell.row][cell.column] = true;
        }
        invalidate();
    }

    private void clearPattern() {
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                mPatternSelect[i][j] = false;
            }
        }
    }

    /**
     * 重置指示器
     */
    public void resetPatternIndicator(){
        clearPattern();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int contentWith = (int) (getPaddingLeft() + getPaddingRight() + radius * 2 * 3 + space * 2);
        int contentHeight = (int) (getPaddingTop() + getPaddingBottom() + radius * 2 * 3 + space * 2);
        int with = resolveSize(contentWith,widthMeasureSpec);
        int height = resolveSize(contentHeight,heightMeasureSpec);
        setMeasuredDimension(with,height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int left = getPaddingLeft();
        int top = getPaddingTop();
        for(int i = 0; i < 3; i++){
            left = getPaddingLeft();
            for(int j = 0; j < 3; j++){
                if(mPatternSelect[i][j]){
                    canvas.drawCircle(left+radius,top+radius,radius,mFullPaint);
                }else{
                    canvas.drawCircle(left+radius,top+radius,radius,mAirPaint);
                }
                left += radius * 2 + space;
            }
            top += radius * 2 + space;
        }
    }
}
