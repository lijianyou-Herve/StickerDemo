package com.wanyueliang.stickerdemo.wediget.sticker;


import com.wanyueliang.stickerdemo.wediget.sticker.bean.item.CropMenuFxArrayBean;
import com.wanyueliang.stickerdemo.wediget.sticker.bean.item.CropMenuFxDataItemBean;

public class StickerFxModelBean extends StickerBaseModel {
    private CropMenuFxArrayBean fxArrayBean;
    private CropMenuFxDataItemBean cropMenuFxDataItemBean;

    public StickerFxModelBean(CropMenuFxArrayBean stickerBean, CropMenuFxDataItemBean cropMenuFxDataItemBean) {
        super(stickerBean);
        this.fxArrayBean = stickerBean;
        this.cropMenuFxDataItemBean = cropMenuFxDataItemBean;
        buildStickerData();
    }

    public void setCropMenuFxDataItemBean(CropMenuFxDataItemBean cropMenuFxDataItemBean) {
        this.cropMenuFxDataItemBean = cropMenuFxDataItemBean;
    }

    public CropMenuFxDataItemBean getCropMenuFxDataItemBean() {
        return cropMenuFxDataItemBean;
    }

    public String getFxId() {
        return fxArrayBean.getFxId();
    }

    public void setFxId(String fxId) {
        fxArrayBean.setFxId(fxId);
    }

    public String getFxKindId() {
        return fxArrayBean.getFxKindId();
    }

    public void setFxKindId(String fxKindId) {
        fxArrayBean.setFxKindId(fxKindId);
    }


    @Override
    public StickerModel getStickerModel() {

        return StickerHelper.getFxStickerModel(stickerModel, cropMenuFxDataItemBean);
    }
}
