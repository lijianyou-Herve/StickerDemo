package com.wanyueliang.stickerdemo.wediget.image;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.wanyueliang.stickerdemo.R;

public class ShapeView extends View {

    private final String TAG = getClass().getSimpleName();
    private Context mContext;
    /*data*/
    private float mViewWdith;
    private float mViewHeight;
    private int mShape;
    private Paint shapePaint;
    private float mUserPaddingInitial;

    public enum Shape {
        circle,
        square,
        Rectangle,
        triangle,
    }

    public ShapeView(Context context) {
        this(context, null);
    }

    public ShapeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShapeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
        getTheme(attrs);
    }

    private void initialize() {
        mContext = getContext();

        shapePaint = new Paint();
        shapePaint.setColor(Color.GRAY);
        shapePaint.setAntiAlias(true);
        shapePaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            mViewWdith = w;
            mViewHeight = h;
        }
    }

    private void getTheme(AttributeSet attrs) {

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ShapeView);

        if (typedArray.hasValue(R.styleable.ShapeView_shape)) {
            mShape = typedArray.getInt(R.styleable.ShapeView_shape, 0);
        }
        if (typedArray.hasValue(R.styleable.ShapeView_shapePadding)) {
            mUserPaddingInitial = typedArray.getDimension(R.styleable.ShapeView_shapePadding, 0);
        }

        typedArray.recycle();
    }

    public void setShape(Shape shape) {
        switch (shape) {
            case circle:
                mShape = 0;
                break;
            case square:
                mShape = 1;
                break;
            case Rectangle:
                mShape = 2;
                break;
            case triangle:
                mShape = 3;
                break;
        }
    }

    private RectF rectF = new RectF();
    private Path path = new Path();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mShape == 0) {//circle
            canvas.drawCircle(mViewWdith / 2f, mViewHeight / 2f, (Math.min(mViewWdith, mViewHeight) - mUserPaddingInitial * 2) / 2, shapePaint)
            ;
        } else if (mShape == 1) {//square

            float sideLength = Math.min(mViewWdith, mViewHeight) - mUserPaddingInitial * 2;

            float centerX = mViewWdith / 2f;
            float centerY = mViewHeight / 2f;

            rectF.left = centerX - sideLength / 2;
            rectF.right = centerX + sideLength / 2;
            rectF.top = centerY - sideLength / 2;
            rectF.bottom = centerY + sideLength / 2;

            canvas.drawRect(rectF, shapePaint);
        } else if (mShape == 2) {//Rectangle

            float sideLength = Math.min(mViewWdith, mViewHeight) - mUserPaddingInitial * 2;

            float centerX = mViewWdith / 2f;
            float centerY = mViewHeight / 2f;

            rectF.left = centerX - sideLength / 2;
            rectF.right = centerX + sideLength / 2;
            rectF.top = centerY - sideLength / 2 + sideLength / 6;
            rectF.bottom = centerY + sideLength / 2 - sideLength / 6;

            canvas.drawRect(rectF, shapePaint);

        } else if (mShape == 3) {//triangle

            float sideLength = Math.min(mViewWdith, mViewHeight) - mUserPaddingInitial * 2;

            float centerX = mViewWdith / 2f;
            float centerY = mViewHeight / 2f;

            path.moveTo(centerX, centerY - sideLength / 2);// 此点为多边形的起点
            path.lineTo(centerX + sideLength / 2, centerY + sideLength / 2);
            path.lineTo(centerX - sideLength / 2, centerY + sideLength / 2);
            path.close(); // 使这些点构成封闭的多边形
            canvas.drawPath(path, shapePaint);
        }

    }
}