package com.wanyueliang.stickerdemo.wediget.sticker;

import android.app.Application;

/**
 * 统一调度贴纸的参数信息
 */
public class StickerConfig {

    private static int mViewWidth;
    private static Application context;

    private static final float EDIT_PHOTO_DEFAULT_TIME_LENGTH = 5.0f;
    public static final String FILE_PATH_FILMEFFECT = "effect/";

    static int getViewWidth() {
        return mViewWidth;
    }

    static Application getContext() {
        return context;
    }

    public static void setViewWidth(int mViewWidth) {
        StickerConfig.mViewWidth = mViewWidth;
    }

    static float getEditPhotoDefaultTimeLength() {
        return EDIT_PHOTO_DEFAULT_TIME_LENGTH;
    }
}
