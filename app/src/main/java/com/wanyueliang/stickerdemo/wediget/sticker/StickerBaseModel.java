package com.wanyueliang.stickerdemo.wediget.sticker;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.widget.TextView;

import com.wanyueliang.stickerdemo.utils.AppLog;
import com.wanyueliang.stickerdemo.utils.BitmapUtils;
import com.wanyueliang.stickerdemo.wediget.sticker.bean.AdjustParamBean;
import com.wanyueliang.stickerdemo.wediget.sticker.bean.item.CropMenuCustomTxtArrayBean;

public abstract class StickerBaseModel {

    /*待修改*/
    private String TAG = getClass().getSimpleName();
    protected boolean isSelected;
    protected Matrix matrix;
    protected Rect realBounds;
    protected StickerModel stickerModel;
    protected StickerBean stickerBean;
    protected int width = 463;
    protected int height = 239;

    public StickerBaseModel(StickerBean stickerBean) {
        this.stickerBean = stickerBean;
    }

    public void saveAdjust(AdjustParamBean adjustParamBean) {
        stickerBean.setAdjust(adjustParamBean);
    }

    public void buildStickerData() {
        AdjustParamBean adjustParamBean = stickerBean.getAdjust();
        if (adjustParamBean != null) {

            int stickerViewWidth = StickerConfig.getViewWidth();
            int stickerViewHeight = stickerViewWidth * 9 / 16;
            float stickerViewScale = 1920f / stickerViewWidth;

            if (stickerBean instanceof CropMenuCustomTxtArrayBean) {//文字
                CropMenuCustomTxtArrayBean txtArrayBean = (CropMenuCustomTxtArrayBean) stickerBean;

                if (txtArrayBean.getTxtType().equals(SuperStickerView.STICKER_TYPE_PRESET_TXT + "")) {//预设文字
                    Bitmap bitmap = StickerHelper.buildPresetTxtBitmap(StickerConfig.getContext(), getStickerModel().getImagePath(), txtArrayBean.getTxt(), getStickerModel());
                    AppLog.d(TAG + "_BJX文字框大小", "buildStickerData: =" + bitmap.getWidth());
                    AppLog.d(TAG + "_BJX文字框大小", "buildStickerData: =" + bitmap.getHeight());

                    stickerModel = getStickerModel();
                    width = bitmap.getWidth();
                    height = bitmap.getHeight();
                } else {//自定义文字
                    stickerModel = getStickerModel();
                    TextView textView = StickerHelper.buildTextView(StickerConfig.getContext(), txtArrayBean.getTxt(), stickerModel);
                    Bitmap bitmap = StickerHelper.convertViewToBitmap(textView);
                    width = bitmap.getWidth();
                    height = bitmap.getHeight();
                }

                AppLog.d(TAG, "buildStickerData: 文字素材width=" + width);
                AppLog.d(TAG, "buildStickerData: 文字素材height" + height);
            } else {//图片
                Bitmap bitmap = BitmapUtils.decodeResource(getStickerModel().getImagePath(), stickerViewWidth, stickerViewHeight);
                width = bitmap.getWidth();
                height = bitmap.getHeight();
                // 先判断是否已经回收
                if (!bitmap.isRecycled()) {
                    // 回收并且置为null
                    bitmap.recycle();
                    bitmap = null;
                }
                stickerModel = getStickerModel();
            }


            AppLog.d(TAG, "saveAsAdjust:还原偏移量X= " + Float.parseFloat(adjustParamBean.centerOffset.offsetX + "f"));
            AppLog.d(TAG, "saveAsAdjust:还原偏移量Y=" + Float.parseFloat(adjustParamBean.centerOffset.offsetY + "f"));

            float degrees = (float) (Float.parseFloat(adjustParamBean.rotateScale + "f") / Math.PI * 180);
            float offsetX = (Float.parseFloat(adjustParamBean.centerOffset.offsetX + "f") / stickerViewScale + stickerViewWidth / 2);
            float offsetY = (stickerViewHeight / 2 - Float.parseFloat(adjustParamBean.centerOffset.offsetY + "f") / stickerViewScale);
            float scale = Float.parseFloat(adjustParamBean.zoomScale + "f");
            double turnY = Double.parseDouble(adjustParamBean.turnY);

            AppLog.d(TAG, "buildStickerData: degrees " + degrees);
            AppLog.d(TAG, "buildStickerData: offsetX " + offsetX);
            AppLog.d(TAG, "buildStickerData: offsetY " + offsetY);
            AppLog.d(TAG, "buildStickerData: scale   " + scale);
            AppLog.d(TAG, "buildStickerData: turnY   " + turnY);

            if (turnY > 3) {
                stickerModel.isFlipped = true;
            }
            matrix = new Matrix();
            /**
             * 顺序很重要，不能随便换
             * */
            /*大小的恢复*/
            matrix.postScale(scale, scale);
            /*平移的位置恢复*/
            matrix.postTranslate(offsetX - width * scale / 2, offsetY - height * scale / 2);

            /*镜像的恢复,前乘，所以中心点一原始完位置*/
            if (turnY > 0) {
                AppLog.i(TAG, "buildStickerData: scaleX= " + (float) Math.cos(turnY));
                AppLog.i(TAG, "buildStickerData: scaleY= " + (-(float) Math.cos(turnY)));
                matrix.preScale((float) Math.cos(turnY), -(float) Math.cos(turnY), width / 2, height / 2);
            }
            /*旋转角度的恢复*/
            matrix.postRotate(0 - degrees, offsetX, offsetY);
            AppLog.d(TAG, "buildStickerData: 数据的还原" + matrix.toString());
        }
    }


    public Matrix getMatrix() {
        return matrix;
    }

    public Rect getRect() {
        return realBounds;
    }

    public double getStartTime() {
        try {
            return Double.parseDouble(stickerBean.getStartTime());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getStickerType() {
        return stickerBean.getStickerType();
    }

    public boolean setSelected() {
        return false;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    public void setRect(Rect rect) {
        this.realBounds = rect;
    }

    public void setStartTime(double startTime) {
        stickerBean.setStartTime(startTime + "");
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public StickerBean getStickerBean() {
        return stickerBean;
    }

    public abstract StickerModel getStickerModel();

}
