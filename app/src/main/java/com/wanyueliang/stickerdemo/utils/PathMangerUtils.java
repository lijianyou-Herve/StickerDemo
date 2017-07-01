package com.wanyueliang.stickerdemo.utils;


import android.os.Environment;

public class PathMangerUtils {

    public static final String TOMATO_FILE_PATH_SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath() + "/avm/";
    public static final String FILE_PATH_CACHE_FX_IMAGE = "fxImageCache/";//
    public static final String LOCAL_FILM_FX_DIR = TOMATO_FILE_PATH_SDCARD + FILE_PATH_CACHE_FX_IMAGE;// 特效图片的缓存目录
    public static final String FILE_SUFFIX_PNGZ = ".pngz";

    /**
     * 获取已下载的素材大图(自由创作)
     *
     * @param pathName the path name
     * @param itemId   the item id
     * @return the server big image path
     */
    public static String getPathFreeTemplateBigImage(String pathName, String itemId) {
        return LOCAL_FILM_FX_DIR + pathName + itemId + "/" + itemId + "_b" + FILE_SUFFIX_PNGZ;
    }

}
