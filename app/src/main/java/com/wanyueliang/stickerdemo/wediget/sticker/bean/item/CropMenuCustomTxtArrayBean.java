package com.wanyueliang.stickerdemo.wediget.sticker.bean.item;


import com.wanyueliang.stickerdemo.wediget.sticker.StickerBean;
import com.wanyueliang.stickerdemo.wediget.sticker.bean.AdjustParamBean;
import com.wanyueliang.stickerdemo.wediget.sticker.bean.PresetTxtBean;

public class CropMenuCustomTxtArrayBean implements StickerBean {

    public String txtType;//是否是自定义的文字类型（1:是自定义文字；2：预设文字）
    /**
     * 素材属性
     */
    public String txt;
    public String startTime;
    public String timeLength;
    public String txtWidth;
    public String txtHeigth;
    public AdjustParamBean adjust;

    public CustomTxtBean customTxt;
    public PresetTxtBean presetTxt;


    public String getTxtType() {
        return txtType;
    }

    public void setTxtType(String txtType) {
        this.txtType = txtType;
    }

    public CustomTxtBean getCustomTxt() {
        return customTxt;
    }

    public void setCustomTxt(CustomTxtBean customTxt) {
        this.customTxt = customTxt;
    }

    public PresetTxtBean getPresetTxt() {
        return presetTxt;
    }

    public void setPresetTxt(PresetTxtBean presetTxt) {
        this.presetTxt = presetTxt;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    @Override
    public void setAdjust(AdjustParamBean adjust) {
        this.adjust = adjust;
    }

    public String getTimeLength() {
        return timeLength;
    }

    public void setTimeLength(String timeLength) {
        this.timeLength = timeLength;
    }

    public String getTxtWidth() {
        return txtWidth;
    }

    public void setTxtWidth(String txtWidth) {
        this.txtWidth = txtWidth;
    }

    public String getTxtHeight() {
        return txtHeigth;
    }

    public void setTxtHeight(String txtHeight) {
        this.txtHeigth = txtHeight;
    }

    public AdjustParamBean getAdjust() {
        return adjust;
    }

    @Override
    public String getStartTime() {
        return startTime;
    }

    @Override
    public int getStickerType() {
        return Integer.parseInt(txtType);
    }

}
