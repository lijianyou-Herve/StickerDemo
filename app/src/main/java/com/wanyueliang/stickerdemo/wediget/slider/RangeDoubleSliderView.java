package com.wanyueliang.stickerdemo.wediget.slider;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.wanyueliang.stickerdemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 可以拖动的双滑块
 * 可以设置的属性
 * :正常状态图标 normalIcon
 * :触摸状态的图标 touchIcon
 * :上下两根线的宽度 lineSize
 * :中间的阴影透明度 0-1
 * :裁剪区域 totalCurrent
 * :最小的裁剪区域 minCurrent
 * <p>
 * =========
 * 考虑到设置的参数类型比较多，有些可以忽略,参数统一通过
 * {@link #setBuilder(Builder)}
 * setBuilder(Builder builder)
 * {@link RangeDoubleSliderView.Builder}来设置
 * =========
 * 拖动事件的监听,尽量不要做耗时操作(数据库查询，IO，Bitmap读取)，否则容易引起绘制的卡顿
 * {@link #addOnSliderChangerListener(OnSliderChangerListener)}
 * {@link RangeDoubleSliderView.OnSliderChangerListener}
 * =========
 *
 * @attr ref R.styleable#DoubleSliderView_normalIcon
 * @attr ref R.styleable#DoubleSliderView_touchIcon
 * @attr ref R.styleable#DoubleSliderView_lineSize
 * @attr ref R.styleable#DoubleSliderView_middleAlpha
 * @attr ref R.styleable#DoubleSliderView_totalCurrent
 * @attr ref R.styleable#DoubleSliderView_minCurrent
 */
class RangeDoubleSliderView extends View {

    protected final String TAG = getClass().getSimpleName();
    /*状态判断的静态常量*/
    public static final int LEFT_THUMB = 0;//按在左边的滑块上面
    public static final int RIGHT_THUMB = 1;//按在右边的滑块上面
    public static final int RESET = -1;//没有滑块被按住(重置状态)

    private Drawable mLeftDrawableThumb;//正常状态
    private Drawable mRightDrawableThumb;//点击状态

    protected float mViewWidth;//View的宽度
    protected float mViewHeight;//View的高度

    protected float mDrawableWidth;//滑块的宽度
    protected float mDrawableHeight;//滑块的宽度
    private float expandTouchWidth;

    private Paint mLinePaint;//线条的画笔颜色
    private Paint mDefaultSliderPaint;//默认滑块的画笔颜色*
    private Paint mMiddlePaint;//中间半透明的的画笔颜色

    private int lineColor;

    private float mLineSize;//线条的默认粗细
    private float mMiddleAlpha;//中间的透明度

    /*滑动状态相关的变量*/
    private int mSelectedThumb;//当前选中的thumb 0:left 1:right
    protected float mLeftTranX;//左边的平移
    protected float mRightTranX;//右边的平移
    private float mStartX;//按下的x点
    private float mOffset;//变化的的偏移量点
    private float mMinWidth = -1;//最小的刻度对应的宽度
    private int mMinCurrent;//最小的刻度
    private float mMaxWidth = -1;//最大的刻度对应的宽度
    private int mMaxCurrent;//最大的刻度
    private int mTotalCurrent;//总共的刻度

    private int mStartCurrent;//开始的时间
    private int mDurationCurrent;//持续的时间

    protected float mLeftCurrent;//左边对应的时间
    protected float mRightCurrent;//右边对应的时间
    protected Builder mBuilder;//外部传入的参数

    private View mLimitLeftView;
    private View mLimitRightView;
    private boolean mLimit;
    private float mLeftLimit;
    private float mRightLimit;

    public RangeDoubleSliderView(Context context) {
        this(context, null);
    }

    public RangeDoubleSliderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RangeDoubleSliderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getTheme(attrs);
        initialize();
    }

    /**
     * 初始化画笔
     */
    private void initialize() {

        mSelectedThumb = RESET;
        /**
         *默认使用主题的colorAccent颜色
         * */
        TypedArray array = getContext().getTheme().obtainStyledAttributes(new int[]{
                R.attr.colorAccent,
                R.attr.colorPrimary,
        });

        int colorAccent = array.getColor(0, Color.RED);
        int colorPrimary = array.getColor(1, Color.YELLOW);

        lineColor = colorPrimary;

        array.recycle();

        mLinePaint = new Paint();
        mLinePaint.setStrokeWidth(mLineSize);
        mLinePaint.setColor(lineColor);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Paint.Style.FILL);

        mDefaultSliderPaint = new Paint();
        mDefaultSliderPaint.setColor(lineColor);
        mDefaultSliderPaint.setAntiAlias(true);
        mDefaultSliderPaint.setStyle(Paint.Style.FILL);

        mMiddlePaint = new Paint();
        mMiddlePaint.setColor(lineColor);
        mMiddlePaint.setAlpha((int) (mMiddleAlpha * 255));
        mMiddlePaint.setAntiAlias(true);
        mMiddlePaint.setStyle(Paint.Style.FILL);

    }

    /**
     * @attr DoubleSliderView
     * 获取XML 设置的信息
     */
    private void getTheme(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.DoubleSliderView);

        mLeftDrawableThumb = typedArray.getDrawable(R.styleable.DoubleSliderView_normalIcon);
        mRightDrawableThumb = typedArray.getDrawable(R.styleable.DoubleSliderView_touchIcon);
        mLineSize = typedArray.getDimension(R.styleable.DoubleSliderView_lineSize, dip2px(4));
        mMiddleAlpha = typedArray.getFloat(R.styleable.DoubleSliderView_middleAlpha, 0.6f);
        mTotalCurrent = typedArray.getInteger(R.styleable.DoubleSliderView_totalCurrent, 100);
        mMinCurrent = typedArray.getInteger(R.styleable.DoubleSliderView_minCurrent, 10);
        mMaxCurrent = typedArray.getInteger(R.styleable.DoubleSliderView_maxCurrent, 100);
        mStartCurrent = typedArray.getInteger(R.styleable.DoubleSliderView_startCurrent, 0);
        mDurationCurrent = typedArray.getInteger(R.styleable.DoubleSliderView_durationCurrent, mTotalCurrent - mStartCurrent);

        typedArray.recycle();

    }

    /**
     * 获取Builder设置的信息
     */
    public void setBuilder(Builder builder) {
        this.mBuilder = builder;

        if (builder.getTotalCurrent() != Builder.DEFAULT_VALUE) {
            this.mTotalCurrent = builder.getTotalCurrent();
        } else {
            //不变
        }

        if (builder.getMaxCurrent() != Builder.DEFAULT_VALUE) {
            this.mMaxCurrent = builder.getMaxCurrent();
        } else {
            this.mMaxCurrent = this.mTotalCurrent;//默认等于总时间
        }

        if (builder.getStartCurrent() != Builder.DEFAULT_VALUE) {
            this.mStartCurrent = builder.getStartCurrent();
        } else {
            this.mStartCurrent = 0;//默认为起始位置
        }

        if (builder.getDurationCurrent() != Builder.DEFAULT_VALUE) {
            this.mDurationCurrent = builder.getDurationCurrent();
        } else {
            this.mDurationCurrent = this.mMaxCurrent - this.mStartCurrent;//默认为总时间减去开始时间
        }

        if (builder.getMinCurrent() != Builder.DEFAULT_VALUE) {
            this.mMinCurrent = builder.getMinCurrent();
        } else {
            this.mMinCurrent = 0;//默认可以为0
        }

        if (builder.getMiddleAlpha() != Builder.DEFAULT_VALUE) {
            this.mMiddleAlpha = builder.getMiddleAlpha();
        } else {
            //nothing
        }

        if (builder.getLineSize() != Builder.DEFAULT_VALUE) {
            this.mLineSize = dip2px(builder.getLineSize());
        } else {
            //nothing
        }

        if (builder.getLeftDrawableThumb() != null) {
            this.mLeftDrawableThumb = builder.getLeftDrawableThumb();
        } else {
            //nothing
        }

        if (builder.getRightDrawableThumb() != null) {
            this.mRightDrawableThumb = builder.getRightDrawableThumb();
        } else {
            //nothing
        }

        resetDrawData();

        initTranX();

        invalidate();
    }

    public Builder getBuilder() {
        return mBuilder;
    }

    /**
     * 改变初始化值的数据
     */
    private void resetDrawData() {
        setDrawData();
    }

    /**
     * 设置数据
     */
    private void setDrawData() {

        this.mLeftCurrent = this.mStartCurrent;
        this.mRightCurrent = this.mLeftCurrent + this.mDurationCurrent;

        this.mMiddlePaint.setAlpha((int) (mMiddleAlpha * 255));
        this.mLinePaint.setStrokeWidth(mLineSize);

    }

    /**
     * 考虑到数据比较多，有些可以忽略，所以使用建造者模式Builder
     */
    public static class Builder {
        private static final int DEFAULT_VALUE = -1;//默认的为处理数值
        private int mTotalCurrent = DEFAULT_VALUE;
        private int mMinCurrent = DEFAULT_VALUE;
        private int mMaxCurrent = DEFAULT_VALUE;
        private int mStartCurrent = DEFAULT_VALUE;
        private int mDurationCurrent = DEFAULT_VALUE;
        private float mMiddleAlpha = DEFAULT_VALUE;//中间的透明度
        private float mLineSize = DEFAULT_VALUE;//线条的默认粗细
        private Drawable mLeftDrawableThumb;//正常状态
        private Drawable mRightDrawableThumb;//点击状态

        public Builder setTotalCurrent(int totalCurrent) {
            this.mTotalCurrent = totalCurrent;
            return this;
        }

        public Builder setMinCurrent(int minCurrent) {
            this.mMinCurrent = minCurrent;
            return this;
        }

        public Builder setMaxCurrent(int maxCurrent) {
            this.mMaxCurrent = maxCurrent;
            return this;
        }

        public Builder setStartCurrent(int startCurrent) {
            this.mStartCurrent = startCurrent;
            return this;
        }

        public Builder setDurationCurrent(int durationCurrent) {
            this.mDurationCurrent = durationCurrent;
            return this;
        }

        public Builder setMiddleAlpha(float middleAlpha) {
            this.mMiddleAlpha = middleAlpha;
            return this;
        }

        public Builder setLineSize(float lineSize) {
            this.mLineSize = lineSize;
            return this;
        }

        public Builder setLeftDrawableThumb(Drawable leftDrawableThumb) {
            this.mLeftDrawableThumb = leftDrawableThumb;
            return this;
        }

        public Builder setRightDrawableThumb(Drawable rightDrawableThumb) {
            this.mRightDrawableThumb = rightDrawableThumb;
            return this;
        }

        private int getTotalCurrent() {
            return mTotalCurrent;
        }

        private int getMaxCurrent() {
            return mMaxCurrent;
        }

        private int getMinCurrent() {
            return mMinCurrent;
        }

        private int getStartCurrent() {
            return mStartCurrent;
        }

        private int getDurationCurrent() {
            return mDurationCurrent;
        }

        private float getMiddleAlpha() {
            return mMiddleAlpha;
        }

        private float getLineSize() {
            return mLineSize;
        }

        private Drawable getLeftDrawableThumb() {
            return mLeftDrawableThumb;
        }

        private Drawable getRightDrawableThumb() {
            return mRightDrawableThumb;
        }
    }


    public void setCustomScrollX(float currentTime, int offsetX, int dxValue) {
        mLeftTranX -= dxValue;
        mRightTranX -= dxValue;
        if (mLimit) {
            mLeftLimit -= dxValue;
            mRightLimit -= dxValue;
        }

        Log.i(TAG, "onScrolled: mLeftTranX=" + mLeftTranX);

        invalidate();
    }

    public void setLimitView(View limitLeftView, View limitRightView) {
        this.mLimitLeftView = limitLeftView;
        this.mLimitRightView = limitRightView;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    protected int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (showSlider) {
            int mx = (int) event.getX();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    //理论上是 mx >= mLeftTranX 即可，但是太窄的时候，不好操作,预留了一个靠左的宽度(拓展宽度)：mLeftTranX - expandTouchWidth
                    if (mx >= mLeftTranX - expandTouchWidth && mx < mLeftTranX + mDrawableWidth) {
                        mSelectedThumb = LEFT_THUMB;
                        mStartX = mx;
                    }
                    //理论上是 mx <= mRightTranX + mDrawableWidth 即可，但是太窄的时候，不好操作,预留了一个靠右的宽度：mx <= mRightTranX + mDrawableWidth + expandTouchWidth
                    else if (mx >= mRightTranX && mx <= mRightTranX + mDrawableWidth + expandTouchWidth) {
                        mSelectedThumb = RIGHT_THUMB;
                        mStartX = mx;
                    }

                    for (int i = 0; i < mOnSliderChangerListeners.size(); i++) {
                        mOnSliderChangerListeners.get(i).onStartTouch(mSelectedThumb);
                    }

                    break;
                case MotionEvent.ACTION_MOVE:

                    if (mSelectedThumb == LEFT_THUMB) {

                        mLeftTranX -= mOffset;//恢复到原始的偏移值
                        mOffset = mx - mStartX;//本地操作的偏移值
                        mLeftTranX += mOffset;//计算新的左滑块的整体偏移值

                        float limitValue = mLimitLeftView.getX() + mLimitLeftView.getMeasuredWidth();

                        if (limitValue > 0 && mLimitLeftView.isShown()) {
                            if (mLeftTranX + mDrawableWidth < limitValue) {
                                Log.i(TAG, "onTouchEvent: 左边限制");
                                if (mLimitLeftView.isShown()) {
                                    Log.i(TAG, "onTouchEvent: 左边限制显示");
                                }
                                mLeftTranX = mLimitLeftView.getX() + mLimitLeftView.getMeasuredWidth() - mDrawableWidth;
                            }
                        }

                        if (mLeftTranX + mDrawableWidth < mLimitLeftView.getX() + mLimitLeftView.getMeasuredWidth()) {
                            mLeftTranX = 0;
                        }
                        //自己处理,滑动左边最小值越界
                        if (mLeftTranX < 0) {
                            mLeftTranX = 0;
                        }

                        //自己处理,滑动左边最大值越界
                        if (mLeftTranX > mViewWidth - mDrawableWidth) {
                            mLeftTranX = mViewWidth - mDrawableWidth;
                        }

                        //左边推动右边
                        if (mLeftTranX > mRightTranX - mDrawableWidth - mMinWidth) {
                            mLeftTranX = mRightTranX - mDrawableWidth - mMinWidth;//恢复到原始的偏移值
                        }

                    }


                    if (mSelectedThumb == RIGHT_THUMB) {

                        mRightTranX -= mOffset;//恢复到原始的偏移值
                        mOffset = mx - mStartX;//本地操作的偏移值
                        mRightTranX += mOffset;//计算新的右滑块的整体偏移值

                        float limitValue = mLimitRightView.getX();

                        if (limitValue > 0 && mLimitRightView.isShown()) {
                            if (mRightTranX > limitValue) {
                                Log.i(TAG, "onTouchEvent: 右边限制");
                                if (mLimitRightView.isShown()) {
                                    Log.i(TAG, "onTouchEvent: 右边限制显示");
                                }

                                mRightTranX = limitValue;
                            }
                        }

                        if (mRightTranX > mViewWidth - mDrawableWidth) {
                            mRightTranX = mViewWidth - mDrawableWidth;
                        }

                        //自己处理,滑动左边最小值越界
                        if (mRightTranX < 0) {
                            mRightTranX = 0;
                        }

                        //自己处理,滑动左边最大值越界
                        if (mRightTranX > mViewWidth - mDrawableWidth) {
                            mRightTranX = mViewWidth - mDrawableWidth;
                        }

                        if (mRightTranX < mLeftTranX + mDrawableWidth + mMinWidth) {
                            mRightTranX = mLeftTranX + mDrawableWidth + mMinWidth;//恢复到原始的偏移值
                        }


                    }

                    calculateChange();

                    break;
                case MotionEvent.ACTION_UP:
                    mStartX = 0;
                    mOffset = 0;

                    for (int i = 0; i < mOnSliderChangerListeners.size(); i++) {
                        mOnSliderChangerListeners.get(i).onStopTouch(mSelectedThumb);
                    }

                    mSelectedThumb = RESET;

                    break;
            }

            invalidate();

            if (mSelectedThumb == RESET) {
                return false;
            } else {
                return true;
            }
        }

        return false;

    }

    /**
     * 计算给外部的时间刻度数值
     * 根据位置，直接赋值
     */
    private void calculateChange() {

        if (mLimit) {
            if (mLeftTranX < mLeftLimit) {
                mLeftTranX = mLeftLimit;
            }
            if (mRightTranX > mRightLimit) {
                mRightTranX = mRightLimit;
            }
        }

        float middleWidth = mViewWidth - mDrawableWidth * 2;//滑块中间的宽度

        float leftCurrent = (mLeftTranX / middleWidth) * mTotalCurrent;
        float percent = ((mRightTranX - mDrawableWidth) / middleWidth);

        float rightCurrent = percent * mTotalCurrent;

        float tempOffsetValue = rightCurrent - leftCurrent;

        mLeftCurrent = 0;
        mRightCurrent = balanceValue(tempOffsetValue);

        //平衡精度 0.5f,只给外部差值
        callBackChange((int) mLeftCurrent, (int) mRightCurrent, mTotalCurrent);

    }

    private int balanceValue(float value) {

        if (value % 0.5f != 0) {
            value += 0.5f;
        } else {
            //Nothing
            Log.i(TAG, "balanceValue: 有等于0的value=" + value);
        }

        return (int) value;
    }

    /**
     * 回调变化
     */
    private void callBackChange(int leftCurrent, int rightCurrent, int totalCurrent) {

        if (mSelectedThumb == LEFT_THUMB) {//左滑块回调
            for (int i = 0; i < mOnSliderChangerListeners.size(); i++) {
                mOnSliderChangerListeners.get(i).onLeftSliderChange(leftCurrent, rightCurrent, totalCurrent);
            }
        }

        if (mSelectedThumb == RIGHT_THUMB) {//右滑块回调
            for (int i = 0; i < mOnSliderChangerListeners.size(); i++) {
                mOnSliderChangerListeners.get(i).onRightSliderChange(leftCurrent, rightCurrent, totalCurrent);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            mViewWidth = w;
            mViewHeight = h;
            mDrawableWidth = mViewHeight / 3f;//滑块的宽度
            mDrawableHeight = mViewHeight;//滑块的高度
//            expandTouchWidth = mDrawableWidth;
            expandTouchWidth = 0;
            setDrawData();
            initTranX();
        }
    }

    /**
     * 缩放设置的图片，适应View的高度设置的
     */
    private void initTranX() {

        if (mMinCurrent > mTotalCurrent) {//设置错误，人为抛出异常
            throw new RuntimeException(getContext().toString()
                    + "\n you can not set minCurrent:" + mMinCurrent + " > totalCurrent:" + mTotalCurrent
                    + "\n 不能设置 minCurrent > totalCurrent");
        }

        if (mMaxCurrent > mTotalCurrent) {//设置错误，人为抛出异常
            throw new RuntimeException(getContext().toString()
                    + "\n you can not set maxCurrent:" + mMaxCurrent + " > totalCurrent:" + mTotalCurrent
                    + "\n 不能设置 maxCurrent > totalCurrent");
        }

        if (mMinCurrent > mMaxCurrent) {//设置错误，人为抛出异常
            throw new RuntimeException(getContext().toString()
                    + "\n you can not set minCurrent:" + mMinCurrent + " > maxCurrent:" + mMaxCurrent
                    + "\n 不能设置 minCurrent > maxCurrent");
        }

        if (mDurationCurrent < mMinCurrent) {
            throw new RuntimeException(getContext().toString()
                    + "\n you can not set durationCurrent:" + mDurationCurrent + " < minCurrent:" + mMinCurrent
                    + "\n 不能设置 durationCurrent < mMinCurrent");
        }

        if (mDurationCurrent < 0) {
            throw new RuntimeException(getContext().toString()
                    + "\n you can not set durationCurrent:" + mDurationCurrent + " < 0:"
                    + "\n 不能设置 durationCurrent < 0");
        }

        float middleWidth = mViewWidth - mDrawableWidth * 2;//滑块中间的宽度
        mMinWidth = ((float) mMinCurrent / (float) mTotalCurrent) * middleWidth;//最小刻度对应的宽度

        mMaxWidth = ((float) mMaxCurrent / (float) mTotalCurrent) * middleWidth;//最大刻度对应的宽度

        mLeftTranX = (mLeftCurrent / (float) mTotalCurrent) * middleWidth;//起始时间对应的宽度

        mRightTranX = (mRightCurrent / (float) mTotalCurrent) * middleWidth + mDrawableWidth;//结束时间对应的宽度

        Log.i(TAG, "setValue: mMinWidth=" + mMinWidth);
        Log.i(TAG, "setValue: mMaxWidth=" + mMaxWidth);

        /*右边最大越界处理*/
        if (mRightTranX > mViewWidth - mDrawableWidth) {
            mRightTranX = mViewWidth - mDrawableWidth;
        }

    }

    private boolean showSlider = true;

    public void setLimit(boolean limit) {
        this.mLimit = limit;
    }

    public void setLimitValue(float leftLimit, float rightLimit) {
        this.mLimit = true;
        this.mLeftLimit = leftLimit;
        this.mRightLimit = rightLimit;
    }

    /**
     * 显示滑块
     */
    public void showSlider() {
        showSlider = true;
        invalidate();
    }

    /**
     * 隐藏滑块
     */
    public void hideSlider() {
        showSlider = false;
        invalidate();
    }

    /**
     * 是否显示滑块
     */
    public boolean isSHowSlider() {
        return showSlider;
    }

    /**
     * 绘制变化的图形
     */
    @Override
    protected void onDraw(Canvas canvas) {

        if (showSlider) {

            if (mViewWidth > 0 && mViewHeight > 0) {

                if (mLineSize > 0) {
                    mLinePaint.setColor(lineColor);
                    mLinePaint.setStrokeWidth(mLineSize);
                /*绘制上方的线条--X轴 -1是于一个起始点防止出现空隙,Y轴上除以2是因为需要预留线条的粗细,起始点在中心，这样高度才会正确*/
                    canvas.drawLine(mLeftTranX + mDrawableWidth - 1, mLineSize / 2f, mRightTranX + 1, mLineSize / 2f, mLinePaint);//上面就的线条
                /*绘制下方的线条 */
                    canvas.drawLine(mLeftTranX + mDrawableWidth - 1, mViewHeight - mLineSize / 2f, mRightTranX + 1, mViewHeight - mLineSize / 2f, mLinePaint);//上面就的线条
                }

                if (mMiddleAlpha > 0) {
                    //绘制中间的半透明矩形区域
                    canvas.drawRect(mLeftTranX + mDrawableWidth, mLineSize, mRightTranX, mViewHeight - mLineSize, mMiddlePaint);
                }

                if (mLeftDrawableThumb != null) {
                    //绘制自定义的左滑块
                    mLeftDrawableThumb.setBounds((int) mLeftTranX, 0, (int) (mLeftTranX + mDrawableWidth), (int) (mViewHeight));
                    mLeftDrawableThumb.draw(canvas);
                } else {
                    //绘制默认的左滑块
                    canvas.drawRect(mLeftTranX, 0, mLeftTranX + mDrawableWidth, mViewHeight, mDefaultSliderPaint);

                    //左滑块里面的两根线条
                    float smallLineSize = mDrawableWidth / 6;
                    mLinePaint.setColor(Color.WHITE);
                    mLinePaint.setStrokeWidth(smallLineSize);
                    canvas.drawLine(mLeftTranX + mDrawableWidth / 4 + smallLineSize / 2, mViewHeight / 2 - mViewHeight / 6, mLeftTranX + mDrawableWidth / 4 + smallLineSize / 2, mViewHeight / 2 + mViewHeight / 6, mLinePaint);//上面就的线条
                    canvas.drawLine(mLeftTranX + mDrawableWidth * 3 / 4 - smallLineSize / 2, mViewHeight / 2 - mViewHeight / 6, mLeftTranX + mDrawableWidth * 3 / 4 - smallLineSize / 2, mViewHeight / 2 + mViewHeight / 6, mLinePaint);//上面就的线条
                }

                if (mRightDrawableThumb != null) {
                    //绘制自定义的右滑块
                    mRightDrawableThumb.setBounds((int) mRightTranX, 0, (int) (mRightTranX + mDrawableWidth), (int) (mViewHeight));
                    mRightDrawableThumb.draw(canvas);
                } else {
                    //绘制默认的右滑块
                    canvas.drawRect(mRightTranX, 0, mRightTranX + mDrawableWidth, mViewHeight, mDefaultSliderPaint);

                    //右滑块里面的两根线条
                    float smallLineSize = mDrawableWidth / 6;
                    mLinePaint.setColor(Color.WHITE);
                    mLinePaint.setStrokeWidth(smallLineSize);
                    canvas.drawLine(mRightTranX + mDrawableWidth / 4 + smallLineSize / 2, mViewHeight / 2 - mViewHeight / 6, mRightTranX + mDrawableWidth / 4 + smallLineSize / 2, mViewHeight / 2 + mViewHeight / 6, mLinePaint);//上面就的线条
                    canvas.drawLine(mRightTranX + mDrawableWidth * 3 / 4 - smallLineSize / 2, mViewHeight / 2 - mViewHeight / 6, mRightTranX + mDrawableWidth * 3 / 4 - smallLineSize / 2, mViewHeight / 2 + mViewHeight / 6, mLinePaint);//上面就的线条
                }
            }
        }

    }

    /**
     * 设置两个滑块的位置
     */
    public void setTran(float leftTranX, float rightTranX) {

        mLeftTranX = leftTranX;//起始时间对应的宽度

        mRightTranX = rightTranX;//结束时间对应的宽度

        mSelectedThumb = LEFT_THUMB;

        calculateChange();

        mSelectedThumb = RESET;

        invalidate();

    }

    /**
     * 重置两个滑块的位置
     */
    public void reset() {

        this.mLeftCurrent = this.mStartCurrent;
        this.mRightCurrent = this.mLeftCurrent + this.mDurationCurrent;

        float middleWidth = mViewWidth - mDrawableWidth * 2;//滑块中间的宽度

        mLeftTranX = (mLeftCurrent / (float) mTotalCurrent) * middleWidth;//起始时间对应的宽度

        mRightTranX = (mRightCurrent / (float) mTotalCurrent) * middleWidth + mDrawableWidth;//结束时间对应的宽度

        invalidate();

    }

    /**
     * 滑块的监听
     */
    private List<OnSliderChangerListener> mOnSliderChangerListeners = new ArrayList<>();


    public float getLeftTranX() {
        return mLeftTranX;
    }

    public float getRightTranX() {
        return mRightTranX;
    }

    /**
     * 获取当前左滑块的时间
     */
    public float getLeftCurrent() {
        return mLeftCurrent;
    }

    /**
     * 获取当前右滑块的时间
     */
    public float getRightCurrent() {
        return mRightCurrent;
    }

    /**
     * 设置监听
     */
    public void addOnSliderChangerListener(OnSliderChangerListener onSliderChangerListener) {
        mOnSliderChangerListeners.add(onSliderChangerListener);
    }

    /**
     * 清空设置的监听
     */
    public void clearOnSliderChangerListener() {
        mOnSliderChangerListeners.clear();
    }

    /**
     * 移除设置的监听
     */
    public void removeOnSliderChangerListener(OnSliderChangerListener onSliderChangerListener) {
        mOnSliderChangerListeners.remove(onSliderChangerListener);
    }

    /**
     * 拖动事件的监听，后期考虑加入人为拖动和数据设置的 boolean值来区分
     */
    public interface OnSliderChangerListener {
        /**
         * {@link #LEFT_THUMB }
         * {@link #RIGHT_THUMB }
         * {@link #RESET }
         */
        void onStartTouch(int touchThumb);//刚开始触摸到滑块

        void onLeftSliderChange(int leftCurrent, int rightCurrent, int totalCurrent);//左滑块的改变

        void onRightSliderChange(int leftCurrent, int rightCurrent, int totalCurrent);//右滑块的改变

        /**
         * {@link #LEFT_THUMB }
         * {@link #RIGHT_THUMB }
         * {@link #RESET }
         */
        void onStopTouch(int touchThumb);//结束触摸滑块

    }
}
