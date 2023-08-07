package com.wanyueliang.stickerdemo.wediget.slider;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;

import com.wanyueliang.stickerdemo.R;
import com.wanyueliang.stickerdemo.bean.EditBean;

import java.util.List;

public class ShadowDoubleSliderView extends RangeDoubleSliderView {

    private final String TAG = getClass().getSimpleName();
    private Context mContext;

    /*data*/
    //参数
    private float precisionValue = 100f;//核心数据----精度
    private float mCurrentTime;//核心数据----精度
    //绘制数据
    private Paint mShadowPaint;//线条的画笔颜色
    private Paint mLinePaint;//线条的画笔颜色

    private float mLineSize;//线条的画笔宽度
    private int mShadowColor;//线条的画笔颜色
    protected float mViewWidth;//View的宽度
    protected float mViewHeight;//View的高度
    protected Rect rect = new Rect();
    private List<EditBean> editBeans;

    public ShadowDoubleSliderView(Context context) {
        this(context, null);
    }

    public ShadowDoubleSliderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShadowDoubleSliderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            mViewWidth = w;
            mViewHeight = h;
        }
    }

    private void initialize() {
        mContext = getContext();

        mShadowColor = getResources().getColor(R.color.black);

        mShadowPaint = new Paint();
        mShadowPaint.setColor(mShadowColor);
        mShadowPaint.setAlpha((int) (255 * 0.3));
        mShadowPaint.setAntiAlias(true);
        mShadowPaint.setStyle(Paint.Style.FILL);

        mLineSize = 8;

        int lineColor = getResources().getColor(R.color.black);

        mLinePaint = new Paint();
        mLinePaint.setColor(lineColor);
        mLinePaint.setStrokeWidth(mLineSize);
        mLinePaint.setAlpha((int) (255 * 0.5));
        mLinePaint.setAntiAlias(true);

    }

    public void setEditBeans(List<EditBean> editBeans) {
        this.editBeans = editBeans;
        invalidate();
    }

    public void notifyDataChange() {
        invalidate();
    }

    private float mOffsetX;

    @Override
    public void setCustomScrollX(float currentTime, int offsetX, int dxValue) {
        super.setCustomScrollX(currentTime, offsetX, dxValue);
        mCurrentTime = currentTime;
        mOffsetX = offsetX;

        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {

        if (editBeans != null) {

            for (int i = 0; i < editBeans.size(); i++) {
                EditBean editBean = editBeans.get(i);

                rect.left = (int) (mViewWidth / 2f + editBean.getStartTime() / 5f * mViewHeight - mOffsetX);
                rect.right = (int) (mViewWidth / 2f + editBean.getEndTime() / 5f * mViewHeight - mOffsetX) + 1;
                rect.top = 0;
                rect.bottom = (int) mViewHeight;

                canvas.drawLine(rect.left + mLineSize / 2f, 0, rect.left + mLineSize / 2f, mViewHeight, mLinePaint);

                canvas.drawLine(rect.right - mLineSize / 2f, 0, rect.right - mLineSize / 2f, mViewHeight, mLinePaint);
                canvas.drawRect(rect, mShadowPaint);
            }
        }

        super.onDraw(canvas);
        Log.i(TAG, "onDraw: ");
    }
}