package com.wanyueliang.stickerdemo.wediget.sticker.bean.item;


import com.wanyueliang.stickerdemo.wediget.sticker.StickerBean;
import com.wanyueliang.stickerdemo.wediget.sticker.bean.AdjustParamBean;

public class CropMenuFxArrayBean implements StickerBean {

    public String fxId;
    public String fxKindId;
    public String startTime;
    public String fxWidth;
    public String fxHeigth;
    public AdjustParamBean adjust;

    @Override

    public String getStartTime() {
        return startTime;
    }

    @Override
    public int getStickerType() {
        return 3;
    }

    @Override
    public void setStartTime(String startTime) {

        this.startTime = startTime;
    }

    @Override
    public AdjustParamBean getAdjust() {
        return adjust;
    }

    @Override
    public void setAdjust(AdjustParamBean adjust) {
        this.adjust = adjust;
    }

    public String getFxId() {
        return fxId;
    }

    public void setFxId(String fxId) {
        this.fxId = fxId;
    }

    public String getFxKindId() {
        return fxKindId;
    }

    public void setFxKindId(String fxKindId) {
        this.fxKindId = fxKindId;
    }

    public String getFxWidth() {
        return fxWidth;
    }

    public void setFxWidth(String fxWidth) {
        this.fxWidth = fxWidth;
    }

    public String getFxHeigth() {
        return fxHeigth;
    }

    public void setFxHeigth(String fxHeigth) {
        this.fxHeigth = fxHeigth;
    }
}
