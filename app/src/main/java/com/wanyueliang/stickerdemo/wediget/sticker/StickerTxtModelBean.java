package com.wanyueliang.stickerdemo.wediget.sticker;


import com.wanyueliang.stickerdemo.wediget.sticker.bean.PresetTxtBean;
import com.wanyueliang.stickerdemo.wediget.sticker.bean.item.CropMenuCustomTxtArrayBean;
import com.wanyueliang.stickerdemo.wediget.sticker.bean.item.CropMenuPresetDataItemBean;
import com.wanyueliang.stickerdemo.wediget.sticker.bean.item.CustomTxtBean;

public class StickerTxtModelBean extends StickerBaseModel {

    private CropMenuCustomTxtArrayBean txtArrayBean;
    public CropMenuPresetDataItemBean cropMenuPresetDataItemBean;

    public StickerTxtModelBean(CropMenuCustomTxtArrayBean stickerBean, CropMenuPresetDataItemBean cropMenuPresetDataItemBean) {
        super(stickerBean);
        this.txtArrayBean = stickerBean;
        this.cropMenuPresetDataItemBean = cropMenuPresetDataItemBean;
        buildStickerData();
    }

    public void setCropMenuPresetDataItemBean(CropMenuPresetDataItemBean cropMenuPresetDataItemBean) {
        this.cropMenuPresetDataItemBean = cropMenuPresetDataItemBean;
    }

    public CropMenuPresetDataItemBean getCropMenuPresetDataItemBean() {
        return cropMenuPresetDataItemBean;
    }

    public CustomTxtBean getCustomTxt() {
        return txtArrayBean.getCustomTxt();
    }

    public PresetTxtBean getPresetTxt() {
        return txtArrayBean.getPresetTxt();
    }

    public void setCustomTxt(CustomTxtBean customTxt) {
        txtArrayBean.setCustomTxt(customTxt);
    }

    public void setPresetTxt(PresetTxtBean presetTxt) {
        txtArrayBean.setPresetTxt(presetTxt);
    }

    public String getTxtWidth() {
        return txtArrayBean.getTxtWidth();
    }

    public void setTxtWidth(String txtWidth) {
        txtArrayBean.setTxtWidth(txtWidth);
    }

    public String getTxtHeigth() {
        return txtArrayBean.getTxtHeight();
    }

    public void setTxtHeight(String txtHeight) {
        txtArrayBean.setTxtHeight(txtHeight);
    }

    public String getTextType() {
        return txtArrayBean.getTxtType();
    }

    public void setTxtType(String txtType) {
        txtArrayBean.setTxtType(txtType);
    }

    public String getText() {
        return txtArrayBean.getTxt();
    }

    public void setText(String text) {
        txtArrayBean.setTxt(text);
    }

    public String getTimeLength() {
        return txtArrayBean.getTimeLength();
    }

    public void setTimeLength(String timeLength) {
        txtArrayBean.setTimeLength(timeLength);
    }

    @Override
    public StickerModel getStickerModel() {
        if (getTextType().equals(SuperStickerView.STICKER_TYPE_CUSTOM_TXT + "")) {
            return StickerHelper.getCustomStickerModel(stickerModel, ((CropMenuCustomTxtArrayBean) (stickerBean)).customTxt, getTimeLength());
        } else {
            return StickerHelper.getPresetStickerModel(stickerModel, cropMenuPresetDataItemBean);
        }
    }

    @Override
    public String toString() {
        return "StickerTxtModelBean{" +
                "txtArrayBean=" + txtArrayBean.toString() +
                '}';
    }
}
