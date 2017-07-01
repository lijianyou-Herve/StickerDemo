package com.wanyueliang.stickerdemo.wediget.sticker.bean.item;

import android.content.ContentValues;
import android.database.Cursor;


public class CropMenuPresetDataItemBean extends CropMenuKindContentBaseBean<CropMenuPresetDataItemBean> {

    //预设文字的持续时长
    public String timeLength;
    public String canEdit;//是否能编辑
    public String align;  //对齐方式 (左:0    中间：1   右：2)
    public CropMenuEdgeDistanceBean edgeDistance;


    @Override
    public CropMenuPresetDataItemBean cursorToBean(Cursor cursor) {
        return null;
    }

    @Override
    public ContentValues beanToValues() {
        return null;
    }
}
