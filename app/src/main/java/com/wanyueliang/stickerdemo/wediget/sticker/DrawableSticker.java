package com.wanyueliang.stickerdemo.wediget.sticker;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.wanyueliang.stickerdemo.utils.AppLog;
import com.wanyueliang.stickerdemo.wediget.sticker.bean.AdjustParamBean;
import com.wanyueliang.stickerdemo.wediget.sticker.bean.CenterOffsetBean;

import java.text.DecimalFormat;

public class DrawableSticker extends Sticker {
    private DecimalFormat decimalFormat = new DecimalFormat("##0.00000000");
    protected static final String TAG = "DrawableSticker";
    protected Drawable mDrawable;
    protected Rect mRealBounds;

    public DrawableSticker(Drawable drawable, StickerModel stickerModel, Matrix matrix, Rect rect) {
        mDrawable = drawable;
        mMatrix = matrix;
        mRealBounds = rect;
        mStickerModel = stickerModel;
        initData(drawable);
    }

    public AdjustParamBean saveAsAdjust() {
        AdjustParamBean adjustParamBean = new AdjustParamBean();
        adjustParamBean.centerOffset = new CenterOffsetBean();

        float[] bitmapPoints = getMappedBoundPoints();
        float x1 = bitmapPoints[0];
        float y1 = bitmapPoints[1];
        float x2 = bitmapPoints[2];
        float y2 = bitmapPoints[3];
        float x3 = bitmapPoints[4];
        float y3 = bitmapPoints[5];
        float x4 = bitmapPoints[6];
        float y4 = bitmapPoints[7];

        /**
         * 16/9的宽高比
         * */
        int stickerViewWidth = StickerConfig.getViewWidth();

        int stickerViewHeight = stickerViewWidth * 9 / 16;
        float stickerViewScale = 1920f / stickerViewWidth;

        AppLog.i(TAG, "saveAsAdjust:保存偏移量X= " + (x1 + (x4 - x1) / 2));
        AppLog.i(TAG, "saveAsAdjust:保存偏移量Y=" + (y2 + (y3 - y2) / 2));


        float offsetX = ((x1 + (x4 - x1) / 2) - stickerViewWidth / 2) * stickerViewScale;
        float offsetY = (stickerViewHeight / 2 - (y2 + (y3 - y2) / 2)) * stickerViewScale;
        AppLog.i(TAG, "completeSticker: x1=" + x1);
        AppLog.i(TAG, "completeSticker: y1=" + y1);
        AppLog.i(TAG, "completeSticker: x2=" + x2);
        AppLog.i(TAG, "completeSticker: y2=" + y2);
        AppLog.i(TAG, "completeSticker: x3=" + x3);
        AppLog.i(TAG, "completeSticker: y3=" + y3);
        AppLog.i(TAG, "completeSticker: x4=" + x4);
        AppLog.i(TAG, "completeSticker: y4=" + y4);
        AppLog.i(TAG, "completeSticker: offsetX=" + offsetX);
        AppLog.i(TAG, "completeSticker: offsetY=" + offsetY);

        Matrix matrix = getMatrix();
        AppLog.i(TAG, "completeSticker: mMatrix=" + matrix.toString());
        AppLog.e(TAG, "buildStickerData: 数据的保存" + matrix.toString());

        float[] values = new float[9];
        matrix.getValues(values);

        // translation is simple
        float tx = values[Matrix.MTRANS_X];
        float ty = values[Matrix.MTRANS_Y];
        AppLog.i(TAG, "completeSticker: tx=" + tx);
        AppLog.i(TAG, "completeSticker: ty=" + ty);

        // calculate real scale
        float scalex = values[Matrix.MSCALE_X];
        float skewy = values[Matrix.MSKEW_Y];
        float scale = (float) Math.sqrt(scalex * scalex + skewy * skewy);
        AppLog.i(TAG, "completeSticker: scale=" + scale);

        // calculate the degree of rotation
        float degree = Math.round(Math.atan2(values[Matrix.MSKEW_X], values[Matrix.MSCALE_X]) * (180 / Math.PI));

        if (isFlipped()) {
            degree = 180 - degree;
            adjustParamBean.turnY = Math.PI + "";
        } else {
            adjustParamBean.turnY = 0 + "";
        }
        AppLog.i(TAG, "completeSticker: degree=" + degree);

        if (degree < 0) {
            degree = 360 + degree;
        }
        String rotateScale = String.valueOf(decimalFormat.format(((Math.abs(degree) / 360) * 2 * Math.PI)));

        AppLog.i(TAG, "参数调试draw_save: rotateScale=" + rotateScale);
        adjustParamBean.zoomScale = scale + "";
        adjustParamBean.rotateScale = rotateScale + "";
        adjustParamBean.centerOffset.offsetX = offsetX + "";
        adjustParamBean.centerOffset.offsetY = offsetY + "";
        AppLog.i(TAG, "completeSticker: adjustParamBean=" + adjustParamBean.toString());

        return adjustParamBean;
    }

    public float[] getCenterOffset() {
        float[] centerOffset = new float[2];
        float[] bitmapPoints = getMappedBoundPoints();
        float x1 = bitmapPoints[0];
        float y1 = bitmapPoints[1];
        float x2 = bitmapPoints[2];
        float y2 = bitmapPoints[3];
        float x3 = bitmapPoints[4];
        float y3 = bitmapPoints[5];
        float x4 = bitmapPoints[6];
        float y4 = bitmapPoints[7];

        /**
         * 16/9的宽高比
         * */
        int stickerViewWidth = StickerConfig.getViewWidth();

        int stickerViewHeight = stickerViewWidth * 9 / 16;

        float offsetX = ((x1 + (x4 - x1) / 2) - stickerViewWidth / 2);
        float offsetY = (stickerViewHeight / 2 - (y2 + (y3 - y2) / 2));
        AppLog.i(TAG + "_BJX", "getCenterOffset:offsetX=" + offsetX);
        AppLog.i(TAG + "_BJX", "getCenterOffset:offsetY=" + offsetY);
        centerOffset[0] = offsetX;
        centerOffset[1] = offsetY;
        return centerOffset;
    }

    private void initData(Drawable drawable) {
        if (mMatrix == null) {
            mMatrix = new Matrix();
        }
        if (mRealBounds == null) {
            mRealBounds = new Rect(0, 0, getWidth(), getHeight());
        }
        if (mStickerModel == null) {
            mStickerModel = new StickerModel(255, "000000", Color.WHITE);
        }
        setFlipped(mStickerModel.isFlipped);

        if (drawable instanceof CustomTextViewDrawable) {
            txt = ((CustomTextViewDrawable) drawable).getTxt();
        }
    }


    public Drawable getDrawable() {
        return mDrawable;
    }

    public void updateDrawable(Drawable drawable) {
        release();
        updateValue(drawable);
        mDrawable = drawable;
    }

    private void updateValue(Drawable drawable) {
        mRealBounds = new Rect(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();

        AppLog.i(TAG, "参数调试draw: mRealBounds=" + mRealBounds.toString());

        canvas.concat(getMatrix());
        mDrawable.setFilterBitmap(true);//抗锯齿
        mDrawable.setBounds(mRealBounds);
        mDrawable.setAlpha(getStickerModel().getAlpha());
        mDrawable.draw(canvas);
        canvas.restore();
    }

    @Override
    public int getWidth() {
        return mDrawable.getIntrinsicWidth();
    }

    @Override
    public int getHeight() {
        return mDrawable.getIntrinsicHeight();
    }


    @Override
    public void release() {
        if (mDrawable != null) {
            mDrawable = null;
        }
    }
}
