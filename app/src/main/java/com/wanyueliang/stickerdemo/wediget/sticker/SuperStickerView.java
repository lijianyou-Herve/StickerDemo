package com.wanyueliang.stickerdemo.wediget.sticker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.view.MotionEventCompat;

import com.wanyueliang.stickerdemo.R;
import com.wanyueliang.stickerdemo.utils.AppLog;
import com.wanyueliang.stickerdemo.utils.BitmapUtils;
import com.wanyueliang.stickerdemo.wediget.sticker.bean.AdjustParamBean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 可以添加贴纸的自定义View
 * 可以添加
 */
public class SuperStickerView extends View {
    private enum ActionMode {
        NONE,   //nothing
        DRAG,   //drag the sticker with your finger
        ZOOM_WITH_TWO_FINGER,   //zoom in or zoom out the sticker and rotate the sticker with two finger
        ZOOM_WITH_ICON,    //zoom in or zoom out the sticker and rotate the sticker with icon
        DELETE,  //delete the handling sticker
        FLIP_HORIZONTAL //horizontal flip the sticker
    }

    /**
     * 贴纸类型
     */
    public static final int STICKER_TYPE_CUSTOM_TXT = 1;//1:是自定义文字
    public static final int STICKER_TYPE_PRESET_TXT = 2;//2：预设文字
    public static final int STICKER_TYPE_FX = 3;//特效图片
    /**
     * 贴纸的mode(特效才有)
     */
    //0：不循环，自由对齐；1: 不循环，头对齐；2: 不循环，尾对齐；3:循环，头对齐；4:不循环，头尾对齐
    public static final String STICKER_MODE_ZERO = "0";//0：不循环，自由对齐
    public static final String STICKER_MODE_FIRST = "1";//1: 不循环，头对齐
    public static final String STICKER_MODE_SECOND = "2";//2: 不循环，尾对齐
    public static final String STICKER_MODE_THIRD = "3";//3:循环，头对齐
    public static final String STICKER_MODE_FOURTH = "4";//4:不循环，头尾对齐


    public static final String STICKER_CANEDIT = "1";//能移动
    public static final String STICKER_CAN_NOT_EDIT = "0";//不能移动


    private static final String TAG = "StickerView";
    public static final float DEFAULT_ICON_RADIUS = 30f;
    public static final float DEFAULT_ICON_EXTRA_RADIUS = 10f;

    private Paint mBorderPaint;

    private RectF mStickerRect;

    private Matrix mSizeMatrix;
    private Matrix mDownMatrix;
    private Matrix mMoveMatrix;

    private BitmapStickerIcon mDeleteIcon;
    private BitmapStickerIcon mZoomIcon;
    private BitmapStickerIcon mFlipIcon;

    private float mIconRadius = DEFAULT_ICON_RADIUS;
    private float mIconExtraRadius = DEFAULT_ICON_EXTRA_RADIUS;

    //the first point down position
    private float mDownX;
    private float mDownY;

    private float mOldDistance = 1f;
    private float mOldRotation = 0;

    private PointF mMidPoint;

    private ActionMode mCurrentMode = ActionMode.NONE;

    private List<Sticker> mStickers = new ArrayList<>();

    private DrawableSticker mHandlingSticker;
    private int selectedStickerPosition;
    private boolean touchOnSelect = false;


    private TextView mStickerTextView;
    private boolean mLooked;

    public SuperStickerView(Context context) {
        this(context, null);
    }

    public SuperStickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SuperStickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStrokeWidth(2);

        mSizeMatrix = new Matrix();
        mDownMatrix = new Matrix();
        mMoveMatrix = new Matrix();

        mStickerRect = new RectF();

        mDeleteIcon = new BitmapStickerIcon(ContextCompat.getDrawable(getContext(), R.mipmap.ic_close_white_18dp));
        mZoomIcon = new BitmapStickerIcon(ContextCompat.getDrawable(getContext(), R.mipmap.ic_scale_white_18dp));
        mFlipIcon = new BitmapStickerIcon(ContextCompat.getDrawable(getContext(), R.mipmap.ic_flip_white_18dp));
    }

    public void setStickerTextView(TextView stickerTextView) {

        mStickerTextView = stickerTextView;

    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            mStickerRect.left = left;
            mStickerRect.top = top;
            mStickerRect.right = right;
            mStickerRect.bottom = bottom;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < mStickers.size(); i++) {
            Sticker sticker = mStickers.get(i);
            if (sticker != null && sticker.isNeedShow()) {
                sticker.draw(canvas);
            }
        }

        if (mHandlingSticker != null && mHandlingSticker.isNeedShow() && !mLooked) {

            mHandlingSticker.draw(canvas);

            float[] bitmapPoints = getStickerPoints(mHandlingSticker);

            float x1 = bitmapPoints[0];
            float y1 = bitmapPoints[1];
            float x2 = bitmapPoints[2];
            float y2 = bitmapPoints[3];
            float x3 = bitmapPoints[4];
            float y3 = bitmapPoints[5];
            float x4 = bitmapPoints[6];
            float y4 = bitmapPoints[7];

            float rotation = calculateRotation(x3, y3, x4, y4);

            //draw flip icon
            mFlipIcon.setX(x2);
            mFlipIcon.setY(y2);
            mFlipIcon.getMatrix().reset();
            mFlipIcon.getMatrix().postRotate(
                    rotation, mDeleteIcon.getWidth() / 2, mDeleteIcon.getHeight() / 2);
            mFlipIcon.getMatrix().postTranslate(
                    x2 - mFlipIcon.getWidth() / 2, y2 - mFlipIcon.getHeight() / 2);

            if (mHandlingSticker instanceof BitmapStickerTxt) {
                //文字素材,不绘制镜像图标&边框颜色可选
                mBorderPaint.setColor(mHandlingSticker.getStickerModel().getRoundRectColor());
            } else {
                //其他素材,边框为白色&绘制镜像图标
                mBorderPaint.setColor(Color.WHITE);
            }
            mBorderPaint.setAlpha(255);
            canvas.drawLine(x1, y1, x2, y2, mBorderPaint);
            canvas.drawLine(x1, y1, x3, y3, mBorderPaint);
            canvas.drawLine(x2, y2, x4, y4, mBorderPaint);
            canvas.drawLine(x4, y4, x3, y3, mBorderPaint);

            /**不可编辑状态*/
            if (mHandlingSticker.getStickerModel() == null || !mHandlingSticker.getStickerModel().isCanEdit()) {
                mFlipIcon.setX(-1000);
                mFlipIcon.setY(-1000);
            } else {
                if (mHandlingSticker instanceof BitmapStickerTxt) {
                    //不绘制镜像框
                    mFlipIcon.setX(-1000);
                    mFlipIcon.setY(-1000);
                } else {
                    mBorderPaint.setColor(Color.BLACK);
                    mBorderPaint.setAlpha(160);
                    canvas.drawCircle(x2, y2, mIconRadius, mBorderPaint);
                    mFlipIcon.draw(canvas);
                }
            }

            onDrawDeleteIcon(canvas, x1, y1, rotation);

            //draw zoom icon
            mZoomIcon.setX(x4);
            mZoomIcon.setY(y4);
            mZoomIcon.getMatrix().reset();
            mZoomIcon.getMatrix().postRotate(
                    45f + rotation, mZoomIcon.getWidth() / 2, mZoomIcon.getHeight() / 2);

            mZoomIcon.getMatrix().postTranslate(
                    x4 - mZoomIcon.getWidth() / 2, y4 - mZoomIcon.getHeight() / 2);

            if (mHandlingSticker.getStickerModel() == null || !mHandlingSticker.getStickerModel().isCanEdit()) {
                AppLog.i(TAG + "_BJX", "onDraw: =不绘制缩放图标");
                /**不可移动的隐藏缩放图标*/
                mZoomIcon.setX(-1000);
                mZoomIcon.setY(-1000);
            } else {
                AppLog.i(TAG + "_BJX", "onDraw: =绘制缩放图标");
                mBorderPaint.setColor(Color.BLACK);
                mBorderPaint.setAlpha(160);
                canvas.drawCircle(x4, y4, mIconRadius, mBorderPaint);

                mZoomIcon.draw(canvas);
            }

        }

    }

    /**
     * 删除图标
     */
    private void onDrawDeleteIcon(Canvas canvas, float x1, float y1, float rotation) {
        //draw delete icon
        canvas.drawCircle(x1, y1, mIconRadius, mBorderPaint);
        mDeleteIcon.setX(x1);
        mDeleteIcon.setY(y1);
        mDeleteIcon.getMatrix().reset();
        mDeleteIcon.getMatrix().postRotate(
                rotation, mDeleteIcon.getWidth() / 2, mDeleteIcon.getHeight() / 2);
        mDeleteIcon.getMatrix().postTranslate(
                x1 - mDeleteIcon.getWidth() / 2, y1 - mDeleteIcon.getHeight() / 2);
        mDeleteIcon.draw(canvas);
    }

    private GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

        /**
         * 双击发生时的通知
         * @param e
         * @return
         */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (onDoubleTapListener != null) {
                onDoubleTapListener.onSelectedMatrix(selectedStickerPosition, mStickerTextView, mHandlingSticker);
            }
            return super.onDoubleTap(e);
        }

        /**
         * 双击手势过程中发生的事件，包括按下、移动和抬起事件
         * @param e
         * @return
         */
        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return super.onDoubleTapEvent(e);
        }

    });


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mLooked) return super.onTouchEvent(event);

        AppLog.i(TAG, "Herve_=: onTouchEvent点击贴纸");

        if (mHandlingSticker != null) {
            if (mHandlingSticker instanceof BitmapStickerTxt) {
                gestureDetector.onTouchEvent(event);
            }
            if (mHandlingSticker.getStickerModel() == null || !mHandlingSticker.getStickerModel().isCanEdit()) {
                return true;
            }
        }


        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mCurrentMode = ActionMode.DRAG;

                mDownX = event.getX();
                mDownY = event.getY();

                if (checkDeleteIconTouched(mIconExtraRadius)) {
                    touchOnSelect = true;
                    mCurrentMode = ActionMode.DELETE;
                } else if (checkHorizontalFlipIconTouched(mIconExtraRadius)) {
                    touchOnSelect = true;
                    mCurrentMode = ActionMode.FLIP_HORIZONTAL;
                } else if (checkZoomIconTouched(mIconExtraRadius) && mHandlingSticker != null) {
                    touchOnSelect = true;
                    mCurrentMode = ActionMode.ZOOM_WITH_ICON;
                    mMidPoint = calculateMidPoint();
                    mOldDistance = calculateDistance(mMidPoint.x, mMidPoint.y, mDownX, mDownY);
                    mOldRotation = calculateRotation(mMidPoint.x, mMidPoint.y, mDownX, mDownY);
                } else {

                    touchOnWitchSticker();

                    touchOnSelect = touchOnSelect();
                }

                if (touchOnSelect && mHandlingSticker != null) {
                    mDownMatrix.set(mHandlingSticker.getMatrix());
                }
                invalidate();
                break;


            case MotionEvent.ACTION_POINTER_DOWN:

                mOldDistance = calculateDistance(event);
                mOldRotation = calculateRotation(event);

                mMidPoint = calculateMidPoint(event);

                if (touchOnSelect &&
                        mHandlingSticker != null &&
                        isInStickerArea(mHandlingSticker, event.getX(1), event.getY(1)) &&
                        !checkDeleteIconTouched(mIconExtraRadius))

                    mCurrentMode = ActionMode.ZOOM_WITH_TWO_FINGER;
                break;

            case MotionEvent.ACTION_MOVE:
                if (event.getX() < 0 || event.getX() > getWidth() || event.getY() < 0 || event.getY() > getHeight()) {
                    Log.i(TAG, "onTouchEvent: 已经越界:限制拖拽");
                } else {
                    handleCurrentMode(event);
                    invalidate();
                }

                break;

            case MotionEvent.ACTION_UP:
                if (touchOnSelect && mHandlingSticker != null) {
                    if (mCurrentMode == ActionMode.DELETE) {
                        mStickers.remove(mHandlingSticker);
                        mHandlingSticker.release();
                        mHandlingSticker = null;
                        selectedStickerPosition = -1;
                        invalidate();
                    }

                    if (mCurrentMode == ActionMode.FLIP_HORIZONTAL) {
                        mHandlingSticker.getMatrix().preScale(-1, 1,
                                mHandlingSticker.getCenterPoint().x, mHandlingSticker.getCenterPoint().y);

                        mHandlingSticker.setFlipped(!mHandlingSticker.isFlipped());
                        invalidate();
                    }

                    if (onSelectedMatrix != null) {
                        if (selectedStickerPosition >= 0) {
                            onSelectedMatrix.onSelectedMatrix(selectedStickerPosition, mHandlingSticker.getMatrix(), mHandlingSticker.getRectBound());
                        }
                    }
                }


                mCurrentMode = ActionMode.NONE;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                mCurrentMode = ActionMode.NONE;
                break;

        }//end of switch(action)

        return true;
    }


    private void handleCurrentMode(MotionEvent event) {
        switch (mCurrentMode) {
            case NONE:
                break;
            case DRAG:

                if (touchOnSelect && mHandlingSticker != null) {
                    mMoveMatrix.set(mDownMatrix);
                    mMoveMatrix.postTranslate(event.getX() - mDownX, event.getY() - mDownY);
//                            mHandlingSticker.getMatrix().reset();
                    mHandlingSticker.getMatrix().set(mMoveMatrix);
                }
                break;
            case ZOOM_WITH_TWO_FINGER:
                if (touchOnSelect && mHandlingSticker != null) {
                    float newDistance = calculateDistance(event);
                    float newRotation = calculateRotation(event);

                    mMoveMatrix.set(mDownMatrix);
                    mMoveMatrix.postScale(
                            newDistance / mOldDistance, newDistance / mOldDistance, mMidPoint.x, mMidPoint.y);
                    mMoveMatrix.postRotate(newRotation - mOldRotation, mMidPoint.x, mMidPoint.y);
//                            mHandlingSticker.getMatrix().reset();
                    mHandlingSticker.getMatrix().set(mMoveMatrix);
                }

                break;

            case ZOOM_WITH_ICON:
                if (touchOnSelect && mHandlingSticker != null) {
                    float newDistance = calculateDistance(mMidPoint.x, mMidPoint.y, event.getX(), event.getY());
                    float newRotation = calculateRotation(mMidPoint.x, mMidPoint.y, event.getX(), event.getY());

                    mMoveMatrix.set(mDownMatrix);
                    mMoveMatrix.postScale(
                            newDistance / mOldDistance, newDistance / mOldDistance, mMidPoint.x, mMidPoint.y);
                    mMoveMatrix.postRotate(newRotation - mOldRotation, mMidPoint.x, mMidPoint.y);
//                            mHandlingSticker.getMatrix().reset();
                    mHandlingSticker.getMatrix().set(mMoveMatrix);
                }

                break;
        }// end of switch(mCurrentMode)
    }

    //判断是否按在缩放按钮区域
    private boolean checkZoomIconTouched(float extraRadius) {
        float x = mZoomIcon.getX() - mDownX;
        float y = mZoomIcon.getY() - mDownY;
        float distance_pow_2 = x * x + y * y;
        return distance_pow_2 <= (mIconRadius + extraRadius) * (mIconRadius + extraRadius);
    }

    //判断是否按在删除按钮区域
    private boolean checkDeleteIconTouched(float extraRadius) {
        float x = mDeleteIcon.getX() - mDownX;
        float y = mDeleteIcon.getY() - mDownY;
        float distance_pow_2 = x * x + y * y;
        return distance_pow_2 <= (mIconRadius + extraRadius) * (mIconRadius + extraRadius);
//        return false;

    }

    //判断是否按在翻转按钮区域
    private boolean checkHorizontalFlipIconTouched(float extraRadius) {
        float x = mFlipIcon.getX() - mDownX;
        float y = mFlipIcon.getY() - mDownY;
        float distance_pow_2 = x * x + y * y;
        return distance_pow_2 <= (mIconRadius + extraRadius) * (mIconRadius + extraRadius);
    }

    //找到点击的区域属于哪个贴纸
    private int touchOnWitchSticker() {

        for (int i = mStickers.size() - 1; i >= 0; i--) {

            if (touchOnSelect()) {
                return mStickers.indexOf(mHandlingSticker);
            } else {
                if (isInStickerArea(mStickers.get(i), mDownX, mDownY)) {
                    if (mStickers.get(i) != mHandlingSticker) {
                        Log.i(TAG, "touchOnWitchSticker: 选中了" + i);
                        setSelect(i);
                    }
                    return i;
                }
            }
        }

        if (mHandlingSticker != null) {
            completeSticker();
        }

        return -1;
    }

    //找到点击的区域属于哪个贴纸
    private boolean touchOnSelect() {

        if (mHandlingSticker == null) {
            return false;
        }
        if (isInStickerArea(mHandlingSticker, mDownX, mDownY)) {
            return true;
        }
        return false;
    }

    private boolean isInStickerArea(Sticker sticker, float downX, float downY) {
        RectF dst = sticker.getMappedBound();
        return dst.contains(downX, downY);
    }

    private PointF calculateMidPoint(MotionEvent event) {
        if (event == null || event.getPointerCount() < 2) return new PointF();
        float x = (event.getX(0) + event.getX(1)) / 2;
        float y = (event.getY(0) + event.getY(1)) / 2;
        return new PointF(x, y);
    }

    private PointF calculateMidPoint() {
        if (mHandlingSticker == null) return new PointF();
        return mHandlingSticker.getMappedCenterPoint();
    }

    //计算两点形成的直线与x轴的旋转角度
    private float calculateRotation(MotionEvent event) {
        if (event == null || event.getPointerCount() < 2) return 0f;
        double x = event.getX(0) - event.getX(1);
        double y = event.getY(0) - event.getY(1);
        double radians = Math.atan2(y, x);
        return (float) Math.toDegrees(radians);
    }

    private float calculateRotation(float x1, float y1, float x2, float y2) {
        double x = x1 - x2;
        double y = y1 - y2;
        double radians = Math.atan2(y, x);
        return (float) Math.toDegrees(radians);
    }

    //计算两点间的距离
    private float calculateDistance(MotionEvent event) {
        if (event == null || event.getPointerCount() < 2) return 0f;
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);

        return (float) Math.sqrt(x * x + y * y);
    }

    private float calculateDistance(float x1, float y1, float x2, float y2) {
        double x = x1 - x2;
        double y = y1 - y2;

        return (float) Math.sqrt(x * x + y * y);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);


        /**
         * 会强制重新设置位置
         * */
//        for (int i = 0; i < mStickers.size(); i++) {
//            addFxSticker sticker = mStickers.get(i);
//            if (sticker != null) {
//                transformSticker(sticker);
//            }
//        }

    }


    //sticker的图片会过大或过小，需要转化
    //step 1：使sticker图片的中心与View的中心重合
    //step 2：计算缩放值，进行缩放
    private void transformSticker(Sticker sticker) {
        if (sticker == null) {
            Log.e(TAG, "transformSticker: the bitmapSticker is null or the bitmapSticker bitmap is null");
            return;
        }


        if (mSizeMatrix != null) {
            mSizeMatrix.reset();
        }

        //step 1
        float offsetX = (getWidth() - sticker.getWidth()) / 2;
        float offsetY = (getHeight() - sticker.getHeight()) / 2;

        mSizeMatrix.postTranslate(offsetX, offsetY);

        //step 2
        float scaleFactor;
        if (getWidth() < getHeight()) {
            scaleFactor = (float) getWidth() / sticker.getWidth();
        } else {
            scaleFactor = (float) getHeight() / sticker.getHeight();
        }

        mSizeMatrix.postScale(scaleFactor / 2, scaleFactor / 2,
                getWidth() / 2, getHeight() / 2);

        sticker.getMatrix().reset();
        sticker.getMatrix().set(mSizeMatrix);

        invalidate();
    }

    //更
    public float[] getStickerPoints(Sticker sticker) {
        if (sticker == null) {
            return new float[8];
        }
        return sticker.getMappedBoundPoints();
    }

    public CustomTextViewDrawable buildTextViewSticker(TextView textView) {
        Bitmap bitmap = StickerHelper.convertViewToBitmap(textView);
        return new CustomTextViewDrawable(getResources(), bitmap, textView.getText().toString());
    }

    public void addTextViewSticker(TextView textView, StickerModel stickerModel, Matrix matrix, Rect rect, boolean isNewSticker) {
        addSticker(buildTextViewSticker(textView), stickerModel, matrix, rect, isNewSticker);
    }

    public void addSticker(Bitmap stickerBitmap, StickerModel stickerModel, Matrix matrix, Rect rect, boolean isNewSticker) {
        addSticker(new BitmapDrawable(getResources(), stickerBitmap), stickerModel, matrix, rect, isNewSticker);
    }

    public void addPresetTxtSticker(Bitmap stickerBitmap, StickerModel stickerModel, Matrix matrix, Rect rect, boolean isNewSticker) {
        addSticker(new PresetTextViewDrawable(getResources(), stickerBitmap), stickerModel, matrix, rect, isNewSticker);
    }

    public void addSticker(Drawable stickerDrawable, StickerModel stickerModel, Matrix matrix, Rect rect, boolean isNewSticker) {
        Sticker drawableSticker = null;

        if (stickerDrawable instanceof PresetTextViewDrawable) {
            BitmapStickerTxt bitmapStickerTxt = new BitmapStickerTxt(stickerDrawable, stickerModel, matrix, rect, ((PresetTextViewDrawable) stickerDrawable).getTxt());
            bitmapStickerTxt.setStickerType(SuperStickerView.STICKER_TYPE_PRESET_TXT);
            bitmapStickerTxt.initTxtMatrix();
            drawableSticker = bitmapStickerTxt;
        } else if (stickerDrawable instanceof CustomTextViewDrawable) {
            BitmapStickerTxt bitmapStickerTxt = new BitmapStickerTxt(stickerDrawable, stickerModel, matrix, rect, ((CustomTextViewDrawable) stickerDrawable).getTxt());
            bitmapStickerTxt.setStickerType(SuperStickerView.STICKER_TYPE_CUSTOM_TXT);
            bitmapStickerTxt.initTxtMatrix();
            drawableSticker = bitmapStickerTxt;
        } else {
            drawableSticker = new DrawableSticker(stickerDrawable, stickerModel, matrix, rect);
        }
        if (isNewSticker) {
            /**强制平移到中间，方便用户编辑，还有一个设置此属性的在BitmapStickerTxt 的getMatrix() 方法里面*/
            float offsetX = (getWidth() - drawableSticker.getWidth()) / 2;
            float offsetY = 0;
            if (stickerDrawable instanceof CustomTextViewDrawable) {
                offsetY = getHeight() - drawableSticker.getHeight() * 1.5f;
            } else {
                offsetY = (getHeight() - drawableSticker.getHeight()) / 2;
            }
            drawableSticker.getMatrix().postTranslate(offsetX, offsetY);
            drawableSticker.setFirstAdd(true);
            if (onSelectedMatrix != null) {
                onSelectedMatrix.onSelectedMatrix(mStickers.size(), drawableSticker.getMatrix(), drawableSticker.getRectBound());
            }
        }

        /**强制正方形，感觉不符合业务需求，暂时关闭*/

//        float scaleFactor;
//        if (getWidth() < getHeight()) {
//            scaleFactor = (float) getWidth() / stickerDrawable.getIntrinsicWidth();
//        } else {
//            scaleFactor = (float) getHeight() / stickerDrawable.getIntrinsicWidth();
//        }
//        drawableSticker.getMatrix().postScale(scaleFactor / 2, scaleFactor / 2, getWidth() / 2, getHeight() / 2);

        mStickers.add(drawableSticker);

        showSticker(mStickers.indexOf(drawableSticker));

    }

    public int getStickerSize() {
        return mStickers.size();
    }

    public DrawableSticker getHandlingSticker() {
        return mHandlingSticker;
    }

    public void removeSticker(int position) {

        Log.i(TAG, "移除指令removeSticker: position=" + position);
        if (mStickers.size() > 0) {
            mStickers.get(position).release();
            if (mHandlingSticker != null && mHandlingSticker == mStickers.get(position)) {
                mHandlingSticker = null;
            }
            mStickers.remove(position);
            selectedStickerPosition = -1;
            invalidate();
        }
    }

    public AdjustParamBean completeSticker() {

        AdjustParamBean adjustParamBean = null;
        if (mStickers.size() > 0) {
            adjustParamBean = mHandlingSticker.saveAsAdjust();
            mHandlingSticker = null;
            selectedStickerPosition = -1;
            invalidate();
        }
        return adjustParamBean;

    }

    public void updateCustomTxtSticker(TextView textView, StickerModel stickerModel) {

        if (mStickers.size() > 0) {
            AppLog.i(TAG, "updateTxtSticker: stickerModel=" + stickerModel.toString());

            mHandlingSticker.updateDrawable(buildTextViewSticker(textView));
            mHandlingSticker.setStickerModel(stickerModel);
            invalidate();
        }

    }

    public void updatePresetTxtSticker(StickerModel stickerModel, String inputString) {

        AppLog.i(TAG, "Herve_updatePresetTxtSticker()=: inputString=" + inputString);

        if (mStickers.size() > 0) {

            /**文字输入框的位置信息*/
            int stickerViewWidth = StickerConfig.getViewWidth();

            int stickerViewHeight = stickerViewWidth * 9 / 16;
            Bitmap groundBmp = BitmapUtils.decodeResource(stickerModel.getImagePath(), stickerViewWidth, stickerViewHeight);
            Bitmap coverBmp = StickerHelper.convertViewToBitmap(getContext().getApplicationContext(), inputString, stickerModel);
            Bitmap bitmap = StickerHelper.getOverlayBitmap(groundBmp, coverBmp, stickerModel.getLeft(), stickerModel.getTop());
            AppLog.i(TAG, "updatePresetTxtSticker: stickerModel=" + stickerModel.toString());
            mHandlingSticker.updateDrawable(new PresetTextViewDrawable(getResources(), bitmap));
            mHandlingSticker.setStickerModel(stickerModel);
            invalidate();
        }

    }

    public void updateFxSticker(StickerModel stickerModel, Bitmap bitmap) {

        if (mStickers.size() > 0) {
            AppLog.i(TAG, "updateTxtSticker: stickerModel=" + stickerModel.toString());
            mHandlingSticker.updateDrawable(new BitmapDrawable(getResources(), bitmap));
            mHandlingSticker.setStickerModel(stickerModel);
            invalidate();
        }

    }

    public void applyAllTxt(StickerModel stickerModel) {

        if (mStickers.size() > 0) {
            for (int i = 0; i < mStickers.size(); i++) {
                if (mStickers.get(i) instanceof BitmapStickerTxt) {

                    BitmapStickerTxt bitmapStickerTxt = (BitmapStickerTxt) mStickers.get(i);

                    if (bitmapStickerTxt.getStickerType() == SuperStickerView.STICKER_TYPE_CUSTOM_TXT) {
                        AppLog.i(TAG, "更新updateSticker: stickerModel=" + stickerModel.toString());

                        TextView textView = StickerHelper.buildTextView(getContext(), bitmapStickerTxt.getTxt(), stickerModel);

                        bitmapStickerTxt.updateDrawable(buildTextViewSticker(textView));

                        StickerModel stickerModelClone = stickerModel.clone();

                        bitmapStickerTxt.setStickerModel(stickerModelClone);
                    }
                }
            }
            invalidate();
        }
    }


    public void showSticker(int position) {

        if (position < 0) {
            return;
        }
        AppLog.i(TAG, "showSticker: position=" + position);
        if (mStickers.size() > 0 && position <= mStickers.size() - 1) {
            if (!mStickers.get(position).isNeedShow()) {
                mStickers.get(position).setNeedShow(true);
                invalidate();
            }
        }
    }

    public void hideSticker(int position) {

        if (position < 0) {
            return;
        }
        AppLog.i(TAG, "hideSticker: position=" + position);
        if (mStickers.size() > 0 && position <= mStickers.size() - 1) {
            mStickers.get(position).setNeedShow(false);
            invalidate();
        }
    }

    public void setSelect(int position) {

        if (mStickers.size() > 0 && position <= mStickers.size() - 1) {
            mHandlingSticker = (DrawableSticker) mStickers.get(position);
            selectedStickerPosition = position;
            mHandlingSticker.setSelected(true);
            invalidate();
        }

    }

    public void clearAllSticker() {
        mStickers.clear();

    }

    public int getSelectedStickerPosition() {
        return selectedStickerPosition;
    }

    public void save(File file) {
        Bitmap bitmap = null;
        FileOutputStream outputStream = null;

        try {
            bitmap = createBitmap();
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);

            BitmapUtil.notifySystemGallery(getContext(), file);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (bitmap != null) {
                bitmap.recycle();
            }

            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void saveData() {

        for (int i = 0; i < mStickers.size(); i++) {
            AppLog.i(TAG, "saveData: " + mStickers.get(i).getWidth());
            AppLog.i(TAG, "saveData: " + mStickers.get(i).getHeight());
            AppLog.i(TAG, "saveData: " + mStickers.get(i).getMatrix().toString());

        }
    }

    private Bitmap createBitmap() {
        mHandlingSticker = null;
        selectedStickerPosition = -1;
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        this.draw(canvas);
        return bitmap;
    }


    public float getIconRadius() {
        return mIconRadius;
    }

    public void setIconRadius(float iconRadius) {
        mIconRadius = iconRadius;
        invalidate();
    }

    public float getIconExtraRadius() {
        return mIconExtraRadius;
    }

    public void setIconExtraRadius(float iconExtraRadius) {
        mIconExtraRadius = iconExtraRadius;
    }

    public boolean isLooked() {
        return mLooked;
    }

    public void setLooked(boolean looked) {
        mLooked = looked;
    }


    public void setOnSelectedMatrix(OnSelectedMatrix onSelectedMatrix) {
        this.onSelectedMatrix = onSelectedMatrix;
    }

    public void setOnDoubleTapListener(SuperStickerView.onDoubleTapListener onDoubleTapListener) {
        this.onDoubleTapListener = onDoubleTapListener;
    }

    private OnSelectedMatrix onSelectedMatrix;
    private onDoubleTapListener onDoubleTapListener;

    public interface OnSelectedMatrix {
        void onSelectedMatrix(int position, Matrix matrix, Rect realBounds);
    }

    public interface onDoubleTapListener {
        void onSelectedMatrix(int position, TextView mStickerTextView, Sticker mHandlingSticker);
    }

}
