package com.wanyueliang.stickerdemo.wediget.sticker.bean.item;

import android.content.ContentValues;
import android.database.Cursor;


public class CropMenuFxDataItemBean extends CropMenuKindContentBaseBean<CropMenuFxDataItemBean> {


    //特效的持续时长
    public String timeLength;
    public String canEdit;
    public String mode;//0：不循环，自由对齐；1: 不循环，头对齐；2: 不循环，尾对齐；3:循环，头对齐；4:不循环，头尾对齐
    public String fxwidth;
    public String fxheight;

    @Override
    public CropMenuFxDataItemBean cursorToBean(Cursor cursor) {
        return null;
    }

    @Override
    public ContentValues beanToValues() {
        return null;
    }
}
