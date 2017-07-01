package com.wanyueliang.stickerdemo.wediget.sticker;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;


public class BitmapStickerIcon extends DrawableSticker {
    private float x;
    private float y;

    public BitmapStickerIcon(Drawable drawable) {
        this(drawable, null);
    }

    public BitmapStickerIcon(Drawable drawable, StickerModel stickerModel) {
        this(drawable, stickerModel, null);
    }

    public BitmapStickerIcon(Drawable drawable, StickerModel stickerModel, Matrix matrix) {
        this(drawable, stickerModel, matrix, null);
    }

    public BitmapStickerIcon(Drawable drawable, StickerModel stickerModel, Matrix matrix, Rect rect) {
        super(drawable, stickerModel, matrix, rect);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
