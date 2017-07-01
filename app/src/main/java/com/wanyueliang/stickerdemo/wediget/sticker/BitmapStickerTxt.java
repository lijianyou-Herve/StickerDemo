package com.wanyueliang.stickerdemo.wediget.sticker;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class BitmapStickerTxt extends DrawableSticker {

    private String txt;
    protected Matrix presetMatrix;
    protected Matrix customMatrix;
    private int stickerType;

    public BitmapStickerTxt(Drawable drawable, StickerModel stickerModel, Matrix matrix, Rect rect, String txt) {
        super(drawable, stickerModel, matrix, rect);
        this.txt = txt;

    }

    public void initTxtMatrix() {
        if (stickerType == SuperStickerView.STICKER_TYPE_CUSTOM_TXT) {
            customMatrix = mMatrix;
        } else {
            presetMatrix = mMatrix;
        }
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public int getStickerType() {
        return stickerType;
    }

    public void setStickerType(int stickerType) {
        this.stickerType = stickerType;
    }

    @Override
    public Matrix getMatrix() {
        if (mStickerModel.isCanEdit()) {
            if (stickerType == SuperStickerView.STICKER_TYPE_CUSTOM_TXT) {
                if (customMatrix == null) {
                    customMatrix = new Matrix();
                    int stickerViewWidth = StickerConfig.getViewWidth();
                    int stickerViewHeight = stickerViewWidth * 9 / 16;
                    /**强制平移到中间，方便用户编辑*/
                    float offsetX = (stickerViewWidth - getWidth()) / 2;
                    float offsetY = stickerViewHeight - getHeight() * 1.5f;
                    customMatrix.postTranslate(offsetX, offsetY);
                }
                return customMatrix;
            } else {
                if (presetMatrix == null) {
                    presetMatrix = new Matrix();
                    doScale(0.5f, 0.5f, getWidth() / 2, getHeight() / 2);
                }
                return presetMatrix;
            }
        }
        return super.getMatrix();
    }

    @Override
    public void setMatrix(Matrix matrix) {
        if (mStickerModel.isCanEdit()) {
            if (stickerType == SuperStickerView.STICKER_TYPE_CUSTOM_TXT) {
                if (presetMatrix == null) {
                    presetMatrix = new Matrix(matrix);
                }
                presetMatrix = matrix;
            } else {
                if (customMatrix == null) {
                    customMatrix = new Matrix(matrix);
                }
                customMatrix = matrix;
            }
        }
        super.setMatrix(matrix);
    }
}
