package com.wanyueliang.stickerdemo.bean;

import android.content.ContentValues;
import android.database.Cursor;

public interface IDB<T> {
    public T cursorToBean(Cursor cursor);

    public ContentValues beanToValues();
}
