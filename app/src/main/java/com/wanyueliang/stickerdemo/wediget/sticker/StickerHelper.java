package com.wanyueliang.stickerdemo.wediget.sticker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View.MeasureSpec;
import android.widget.TextView;

import com.wanyueliang.stickerdemo.R;
import com.wanyueliang.stickerdemo.utils.AppLog;
import com.wanyueliang.stickerdemo.utils.BitmapUtils;
import com.wanyueliang.stickerdemo.utils.PathMangerUtils;
import com.wanyueliang.stickerdemo.wediget.sticker.bean.item.CropMenuFxDataItemBean;
import com.wanyueliang.stickerdemo.wediget.sticker.bean.item.CropMenuPresetDataItemBean;
import com.wanyueliang.stickerdemo.wediget.sticker.bean.item.CustomTxtBean;
import com.wanyueliang.stickerdemo.wediget.textview.AutoResizeTextView;

public class StickerHelper {

    private static final String TAG = "StickerHelper";

    /**
     * 预设文字颜色
     */
    public static StickerModel getPresetStickerModel(StickerModel stickerModel, CropMenuPresetDataItemBean cropMenuPresetDataItemBean) {
        if (stickerModel == null) {
            stickerModel = new StickerModel();
        }
        String itemId = cropMenuPresetDataItemBean.getItemId();

        //透明度
        stickerModel.setAlpha(255);
        //字体颜色
        stickerModel.setTextColor("ffffff");
        stickerModel.setRoundRectColor(Color.WHITE);
        //对齐方式
        stickerModel.setAlign(Integer.parseInt(cropMenuPresetDataItemBean.align == null ? "0" : cropMenuPresetDataItemBean.align));
        /*本地保存路径*/
//        final String imagePath = PathMangerUtils.getPathFreeTemplateBigImage(AppConstant.FILE_PATH_MAINTEXT, itemId);
//        stickerModel.setImagePath(imagePath);
        /*服务器下载路径*/
//        String imageUrl = PathMangerUtils.getUrlFreeTemplateBigImage(AppConstant.FILE_PATH_MAINTEXT, itemId);
//        stickerModel.setImageUrl(imageUrl);
        stickerModel.setTimeLength(cropMenuPresetDataItemBean.timeLength);
        stickerModel.setMode(SuperStickerView.STICKER_MODE_ZERO);
        stickerModel.setCanEdit(cropMenuPresetDataItemBean.canEdit.equals("1"));
        //添加预设文字
        EditUtils.setPresetTxtRound(stickerModel, cropMenuPresetDataItemBean.edgeDistance);

        return stickerModel;

    }

    @NonNull
    public static TextView buildTextView(Context mContext, String inputString, StickerModel stickerModel) {
        AutoResizeTextView textView = (AutoResizeTextView) LayoutInflater.from(mContext).inflate(R.layout.material_edit_text, null);
        switch (stickerModel.getAlign()) {
            case 0:
                textView.setGravity(Gravity.LEFT);
                break;
            case 1:
                textView.setGravity(Gravity.CENTER);
                break;
            case 2:
                textView.setGravity(Gravity.RIGHT);
                break;
        }
        float screenWidth = StickerConfig.getViewWidth();
        float textSize = screenWidth / 18;
          /*阴影*/
        textView.setShadowLayer(1, 0, 0, Color.BLACK);
        textView.setMinTextSize(textSize);
        textView.setText(inputString);
        textView.setTextColor(Color.parseColor("#" + stickerModel.getTextColor()));
        textView.setBackgroundColor(Color.TRANSPARENT);
        return textView;
    }

    public static Bitmap buildPresetTxtBitmap(Context mContext, String imagePtah, String inputString, StickerModel stickerModel) {
        int stickerViewWidth = StickerConfig.getViewWidth();
        int stickerViewHeight = stickerViewWidth * 9 / 16;
        Bitmap groundBmp = BitmapUtils.decodeResource(imagePtah, stickerViewWidth, stickerViewHeight);
        Bitmap coverBmp = StickerHelper.convertViewToBitmap(mContext, inputString, stickerModel);
        Bitmap bitmap = StickerHelper.getOverlayBitmap(groundBmp, coverBmp, stickerModel.getLeft(), stickerModel.getTop());
        return bitmap;
    }

    /**
     * 构建
     *
     * @param groundBmp the 底层的bitmap
     * @param coverBmp  the 上层的 bmp
     * @param left      the x偏移量
     * @param top       the y偏移量
     * @return the overlay bitmap(叠加后的bitmap)
     */
    public static Bitmap getOverlayBitmap(Bitmap groundBmp, Bitmap coverBmp, float left, float top) {
        Bitmap bmOverlay = Bitmap.createBitmap(groundBmp.getWidth(), groundBmp.getHeight(), groundBmp.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(groundBmp, new Matrix(), null);
        canvas.drawBitmap(coverBmp, left, top, null);

        if (coverBmp != null && !coverBmp.isRecycled()) {
            coverBmp.recycle();
            coverBmp = null;
        }
        if (groundBmp != null && !groundBmp.isRecycled()) {
            groundBmp.recycle();
            groundBmp = null;
        }
        return bmOverlay;
    }

    /**
     * Convert view to bitmap bitmap.
     * 预设文字
     *
     * @param mContext     the m context
     * @param inputString  the 填充的文字
     * @param stickerModel StickerModel 贴纸的属性
     * @return the bitmap 返回文字填充TextVIew的框的位图
     */
    public static Bitmap convertViewToBitmap(Context mContext, String inputString, StickerModel stickerModel) {
        AppLog.i(TAG + "_BJX", "convertViewToBitmap:inputString =" + inputString);
        AppLog.i(TAG + "_BJX", "convertViewToBitmap:stickerModel =" + stickerModel.toString());

        AutoResizeTextView autofitTextView = (AutoResizeTextView) LayoutInflater.from(mContext).inflate(R.layout.material_edit_text, null);
        autofitTextView.setMinTextSize(1);
        /*阴影*/
        autofitTextView.setShadowLayer(1, 0, 0, Color.BLACK);
        autofitTextView.setWidth((int) stickerModel.getWidth());
        autofitTextView.setHeight((int) stickerModel.getHeight());

        int textSize = (int) Math.min(stickerModel.getWidth(), stickerModel.getHeight());
        autofitTextView.setTextSize(textSize);

        switch (stickerModel.getAlign()) {
            case 0:
                autofitTextView.setGravity(Gravity.LEFT);
                break;
            case 1:
                autofitTextView.setGravity(Gravity.CENTER);
                break;
            case 2:
                autofitTextView.setGravity(Gravity.RIGHT);
                break;
        }
        autofitTextView.setText(inputString);
        autofitTextView.setTextColor(Color.parseColor("#" + stickerModel.getTextColor()));

        autofitTextView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        int measuredWidth = autofitTextView.getMeasuredWidth();
        autofitTextView.layout(0, 0, measuredWidth, autofitTextView.getMeasuredHeight());  //根据字符串的长度显示view的宽度
        autofitTextView.destroyDrawingCache();
        autofitTextView.buildDrawingCache();
        Bitmap bitmap = autofitTextView.getDrawingCache();
        return bitmap;
    }

    public static Bitmap convertViewToBitmap(TextView textView) {
        textView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        int width = textView.getMeasuredWidth();

        textView.layout(0, 0, width, textView.getMeasuredHeight());  //根据字符串的长度显示view的宽度
        textView.destroyDrawingCache();
        textView.buildDrawingCache();
        Bitmap bitmap = textView.getDrawingCache();
        return bitmap;
    }

    /**
     * 自定义文字颜色
     */
    public static StickerModel getCustomStickerModel(StickerModel stickerModel, CustomTxtBean customTxtBean, String timeLength) {
        if (stickerModel == null) {
            stickerModel = new StickerModel();
        }
        AppLog.i(TAG, "getCustomStickerModel: " + customTxtBean.toString());

        //外框颜色
        stickerModel.setRoundRectColor(Color.WHITE);
        //透明度
        stickerModel.setAlpha((int) (Float.parseFloat(customTxtBean.alpha) * 255));
        //对齐方式
        stickerModel.setAlign(Integer.parseInt(customTxtBean.align));
        //字体颜色
        if (customTxtBean.color != null) {
            stickerModel.setTextColor(customTxtBean.color);
        } else {
            stickerModel.setTextColor("ffffff");
        }
        if (timeLength != null) {
            stickerModel.setTimeLength(timeLength);
        } else {
            stickerModel.setTimeLength(StickerConfig.getEditPhotoDefaultTimeLength() + "");
        }
        stickerModel.setMode(SuperStickerView.STICKER_MODE_ZERO);

        stickerModel.setCanEdit(true);

        return stickerModel;

    }

    /**
     * 特效颜色
     */
    public static StickerModel getFxStickerModel(StickerModel stickerModel, CropMenuFxDataItemBean cropMenuFxDataItemBean) {
        if (stickerModel == null) {
            stickerModel = new StickerModel();
        }
        String itemId = cropMenuFxDataItemBean.getItemId();
        //透明度
        stickerModel.setAlpha(255);
        //对齐方式
        stickerModel.setAlign(-1);
        //字体颜色
        stickerModel.setTextColor("ffffff");

        /*本地保存路径*/
        final String imagePtah = PathMangerUtils.getPathFreeTemplateBigImage(StickerConfig.FILE_PATH_FILMEFFECT, itemId);
        stickerModel.setImagePath(imagePtah);
        /*服务器下载路径*/
//        String bigImageUrl = PathMangerUtils.getUrlFreeTemplateBigImage(StickerConfig.FILE_PATH_FILMEFFECT, itemId);
//        stickerModel.setImageUrl(bigImageUrl);

        stickerModel.setRoundRectColor(Color.WHITE);
        stickerModel.setMode(cropMenuFxDataItemBean.mode);
        stickerModel.setCanEdit(cropMenuFxDataItemBean.canEdit.equals("1"));
        stickerModel.setTimeLength(cropMenuFxDataItemBean.timeLength);
        return stickerModel;

    }

}
