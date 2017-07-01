package com.wanyueliang.stickerdemo.wediget.sticker;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;


public abstract class Sticker {
    protected static final String TAG = "Sticker";

    protected boolean isSelected;
    protected boolean needShow;
    protected Matrix mMatrix;
    protected Matrix fillMatrix;
    protected boolean mIsFlipped;
    protected String txt;
    protected StickerModel mStickerModel;

    private boolean firstAdd;
    private boolean halfScaleEd;

    public StickerModel getStickerModel() {
        return mStickerModel;
    }

    public void setStickerModel(StickerModel stickerModel) {
        this.mStickerModel = stickerModel;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isNeedShow() {
        return needShow;
    }

    public void setNeedShow(boolean needShow) {
        this.needShow = needShow;
    }

    public boolean isFlipped() {
        return mIsFlipped;
    }

    public void setFlipped(boolean flipped) {
        mIsFlipped = flipped;
    }

    public boolean isFirstAdd() {
        return firstAdd;
    }

    public void setFirstAdd(boolean firstAdd) {
        this.firstAdd = firstAdd;
    }

    public Matrix getMatrix() {
        if (mStickerModel.isCanEdit()) {
            return mMatrix;
        } else {
            if (fillMatrix == null) {
                fillMatrix = new Matrix();
            }
            return fillMatrix;
        }
    }

    public void doScale(float sx, float sy, float px, float py) {
        if (!halfScaleEd && isFirstAdd()) {
            getMatrix().postScale(sx, sy, px, py);
            halfScaleEd = true;
        }
    }

    public void setMatrix(Matrix matrix) {
        mMatrix.set(matrix);
    }

    public abstract void draw(Canvas canvas);

    public abstract int getWidth();

    public abstract int getHeight();

    public float[] getBoundPoints() {
        if (!mIsFlipped) {
            return new float[]{
                    0f, 0f,
                    getWidth(), 0f,
                    0f, getHeight(),
                    getWidth(), getHeight()
            };
        } else {
            return new float[]{
                    getWidth(), 0f,
                    0f, 0f,
                    getWidth(), getHeight(),
                    0f, getHeight()
            };
        }
    }

    public float[] getMappedBoundPoints() {
        float[] dst = new float[8];
        getMatrix().mapPoints(dst, getBoundPoints());
        return dst;
    }

    public float[] getMappedPoints(float[] src) {
        float[] dst = new float[src.length];
        getMatrix().mapPoints(dst, src);
        return dst;
    }

    public Rect getRectBound() {
        return new Rect(0, 0, getWidth(), getHeight());
    }

    public RectF getRectFBound() {
        return new RectF(0, 0, getWidth(), getHeight());
    }

    public RectF getMappedBound() {
        RectF dst = new RectF();
        getMatrix().mapRect(dst, getRectFBound());
        return dst;
    }

    public PointF getCenterPoint() {
        return new PointF(getWidth() / 2, getHeight() / 2);
    }

    public PointF getMappedCenterPoint() {
        PointF pointF = getCenterPoint();
        float[] dst = getMappedPoints(new float[]{
                pointF.x,
                pointF.y
        });
        return new PointF(dst[0], dst[1]);
    }

    public abstract void release();

}
