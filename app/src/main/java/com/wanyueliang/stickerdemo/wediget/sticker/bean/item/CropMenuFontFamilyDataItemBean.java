package com.wanyueliang.stickerdemo.wediget.sticker.bean.item;

import android.content.ContentValues;
import android.database.Cursor;

public class CropMenuFontFamilyDataItemBean extends CropMenuKindContentBaseBean<CropMenuFontFamilyDataItemBean> {

    @Override
    public CropMenuFontFamilyDataItemBean cursorToBean(Cursor cursor) {
        return this;
    }

    @Override
    public ContentValues beanToValues() {
        return null;
    }

}
