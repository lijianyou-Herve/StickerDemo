package com.wanyueliang.stickerdemo.wediget.sticker;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import java.io.InputStream;


public class CustomTextViewDrawable extends BitmapDrawable {

    private String txt;

    public CustomTextViewDrawable(Resources res, String filepath) {
        super(res, filepath);
    }

    public CustomTextViewDrawable(Resources res, InputStream is) {
        super(res, is);
    }

    //TextViewDrawable(getResources(), bitmap)
    public CustomTextViewDrawable(Resources res, Bitmap bitmap) {
        super(res, bitmap);
    }

    //TextViewDrawable(getResources(), bitmap)
    public CustomTextViewDrawable(Resources res, Bitmap bitmap, String txt) {
        super(res, bitmap);
        this.txt = txt;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }
}
