package com.wanyueliang.stickerdemo.wediget.sticker.bean.item;

import android.content.ContentValues;
import android.database.Cursor;

public class CropMenuFilterDataItemBean extends CropMenuKindContentBaseBean<CropMenuFilterDataItemBean> {

    @Override
    public CropMenuFilterDataItemBean cursorToBean(Cursor cursor) {
        return this;
    }

    @Override
    public ContentValues beanToValues() {
        return null;
    }

}
