package com.wanyueliang.stickerdemo.wediget.sticker.bean.item;

import com.wanyueliang.stickerdemo.bean.BaseBean;

public abstract class CropMenuKindContentBaseBean<T> extends BaseBean<T> {

    //itemId
    public String itemId;
    //item名字,可以为空
    public String itemName;

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName == null ? "" : itemName;
    }

}
