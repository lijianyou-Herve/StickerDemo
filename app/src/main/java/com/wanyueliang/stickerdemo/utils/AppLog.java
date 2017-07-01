package com.wanyueliang.stickerdemo.utils;

import android.text.TextUtils;
import android.util.Log;

public class AppLog {

    //    public static boolean LOGGING = BuildConfig.DEBUG;
    public static boolean LOGGING = true;
    private static int MAX_LENGTH = 1024 * 3;

    public static void i(String tag, String message, Throwable tr) {
        if (LOGGING) {
            if (!TextUtils.isEmpty(message) && message.length() > MAX_LENGTH) {
                while (message.length() > MAX_LENGTH) {
                    Log.i(tag, message.substring(0, MAX_LENGTH), tr);
                    message = message.substring(MAX_LENGTH);
                }
                Log.i(tag, message, tr);
            } else {
                Log.i(tag, message, tr);
            }
        }
    }

    public static void i(String tag, String message) {
        if (LOGGING) {
            if (!TextUtils.isEmpty(message) && message.length() > MAX_LENGTH) {
                while (message.length() > MAX_LENGTH) {
                    Log.i(tag, message.substring(0, MAX_LENGTH));
                    message = message.substring(MAX_LENGTH);
                }
                Log.i(tag, message);
            } else {
                Log.i(tag, message);
            }
        }
    }

    public static void d(String tag, String message) {
        if (LOGGING) {
            if (!TextUtils.isEmpty(message) && message.length() > MAX_LENGTH) {
                while (message.length() > MAX_LENGTH) {
                    Log.d(tag, message.substring(0, MAX_LENGTH));
                    message = message.substring(MAX_LENGTH);
                }
                Log.d(tag, message);
            } else {
                Log.d(tag, message);
            }
        }
    }

    public static void e(String tag, String message) {
        if (LOGGING) {
            if (!TextUtils.isEmpty(message) && message.length() > MAX_LENGTH) {
                while (message.length() > MAX_LENGTH) {
                    Log.e(tag, message.substring(0, MAX_LENGTH));
                    message = message.substring(MAX_LENGTH);
                }
                Log.e(tag, message);
            } else {
                Log.e(tag, message);
            }
        }
    }

    public static void w(String tag, String message) {
        if (LOGGING) {
            if (!TextUtils.isEmpty(message) && message.length() > MAX_LENGTH) {
                while (message.length() > MAX_LENGTH) {
                    Log.w(tag, message.substring(0, MAX_LENGTH));
                    message = message.substring(MAX_LENGTH);
                }
                Log.w(tag, message);
            } else {
                Log.w(tag, message);

            }
        }
    }
}
