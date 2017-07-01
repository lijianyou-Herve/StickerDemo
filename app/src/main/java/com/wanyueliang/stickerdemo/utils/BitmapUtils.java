package com.wanyueliang.stickerdemo.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.File;

public class BitmapUtils {


    /**
     * 从文件获取指定的bitmap,并且设置大小
     *
     * @param srcPath the src path
     * @param w       the w
     * @param h       the h
     * @return the bitmap
     */
    public static Bitmap decodeResource(String srcPath, int w, int h) {
        Bitmap newbmp = null;
        try {
            Bitmap oldbmp = loadBitmapFile(srcPath);
            Matrix matrix = new Matrix();
            float scaleWidth = ((float) w / oldbmp.getWidth());
            float scaleHeight = ((float) h / oldbmp.getHeight());
            matrix.postScale(scaleWidth, scaleHeight);
            newbmp = Bitmap.createBitmap(oldbmp, 0, 0, oldbmp.getWidth(), oldbmp.getHeight(), matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
            File file = new File(srcPath);
            if (file.exists()) {
                file.delete();
            }
        }
        return newbmp;
    }

    // 从文件调入bitmap
    public static Bitmap loadBitmapFile(String srcPath) {
        try {
            if (new File(srcPath).exists() == false)
                return null;

            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            newOpts.inPreferredConfig = Config.ARGB_8888;// null;//bitmap.getConfig();// 该模式是默认的,可不设
            newOpts.inPurgeable = true;// 同时设置才会有效
            newOpts.inInputShareable = true;// 。当系统内存不够时候图片自动被回收
            newOpts.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(srcPath, newOpts);
        } catch (OutOfMemoryError e) {

        }
        return null;
    }
}