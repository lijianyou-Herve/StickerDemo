package com.wanyueliang.stickerdemo.wediget.sticker.bean.item;

import android.content.ContentValues;
import android.database.Cursor;

import com.wanyueliang.stickerdemo.bean.BaseBean;

public class CropMenuEdgeDistanceBean extends BaseBean<CropMenuEdgeDistanceBean> {

    public String r;
    public String l;
    public String b;
    public String t;


    @Override
    public CropMenuEdgeDistanceBean cursorToBean(Cursor cursor) {
        return this;
    }

    @Override
    public ContentValues beanToValues() {
        return null;
    }


    @Override
    public String toString() {
        return "CropMenuEdgeDistanceBean{" +
                "r='" + r + '\'' +
                ", l='" + l + '\'' +
                ", b='" + b + '\'' +
                ", t='" + t + '\'' +
                '}';
    }
}
