package com.wanyueliang.stickerdemo.wediget.sticker;

import com.wanyueliang.stickerdemo.wediget.sticker.bean.AdjustParamBean;

public interface StickerBean {


    String getStartTime();

    int getStickerType();

    void setStartTime(String startTime);

    AdjustParamBean getAdjust();

    void setAdjust(AdjustParamBean adjust);
}
