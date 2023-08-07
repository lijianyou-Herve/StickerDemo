package com.wanyueliang.stickerdemo.wediget.sticker;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wanyueliang.stickerdemo.utils.AppLog;
import com.wanyueliang.stickerdemo.wediget.sticker.bean.item.CropMenuEdgeDistanceBean;

import java.util.ArrayList;

public class EditUtils {

    private static String TAG = "EditUtils";

    /**
     * Sets preset txt round.
     * 获取预设文字样式文字外框位置
     *
     * @param stickerModel the sticker model
     * @param edgeDistance the edge distance
     */
    public static void setPresetTxtRound(StickerModel stickerModel, CropMenuEdgeDistanceBean edgeDistance) {
        int stickerViewWidth = StickerConfig.getViewWidth();


        float scale = stickerViewWidth / 1920f;

        AppLog.i(TAG + "BJX", "updatePresetTextEdit: edgeDistance=" + edgeDistance.toString());

        float left = 0;
        float top = 0;
        float width = 0;
        float height = 0;
        try {
            left = (1920 / 2 + Float.parseFloat(edgeDistance.l)) * scale;
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            top = (1080 / 2 - Float.parseFloat(edgeDistance.t)) * scale;
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            width = (Float.parseFloat(edgeDistance.r) - Float.parseFloat(edgeDistance.l)) * scale;
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            height = (Float.parseFloat(edgeDistance.t) - Float.parseFloat(edgeDistance.b)) * scale;
        } catch (Exception e) {
            e.printStackTrace();
        }
        AppLog.i(TAG + "_BJX", "updatePresetTextEdit: stickerViewWidth=" + stickerViewWidth);
        AppLog.i(TAG + "_BJX", "updatePresetTextEdit: left=" + left);
        AppLog.i(TAG + "_BJX", "updatePresetTextEdit: top=" + top);
        AppLog.i(TAG + "_BJX", "updatePresetTextEdit: width=" + width);
        AppLog.i(TAG + "_BJX", "updatePresetTextEdit: height=" + height);

        stickerModel.setLeft(left);
        stickerModel.setTop(top);
        stickerModel.setWidth(width);
        stickerModel.setHeight(height);

    }

    /**
     * Auto to scroll position.
     * 自动滚动的RecyclerView处理
     *
     * @param position     the position
     * @param recyclerView the recycler view
     */
    public static void autoToScrollPosition(RecyclerView recyclerView, int position) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            final LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
            //获取第一个可见view的位置
            int firstItemPosition = linearManager.findFirstVisibleItemPosition();
            if (recyclerView.getChildAt(position - firstItemPosition) == null) {
                return;
            }
            int x = (int) recyclerView.getChildAt(position - firstItemPosition).getX();
            int itemWidth = recyclerView.getChildAt(0).getWidth();

            int totalWidth = recyclerView.getWidth();

            if (position >= 1) {
                recyclerView.smoothScrollBy(x + itemWidth / 2 - totalWidth / 2, 0);
            }

        }
    }

    /**
     * Gets color list.
     * 获取颜色条的颜色值
     *
     * @return the color list
     */
    public static ArrayList<String> getColorList() {

        ArrayList<String> mColors = new ArrayList<>();
        mColors.add("000000");
        mColors.add("ffffff");
        mColors.add("cccccc");
        mColors.add("999999");
        mColors.add("414154");
        mColors.add("c82b2d");
        mColors.add("c80103");
        mColors.add("960102");
        mColors.add("640100");
        mColors.add("f5e209");
        mColors.add("fecf08");
        mColors.add("fc9906");
        mColors.add("ca6723");
        mColors.add("986728");
        mColors.add("643401");
        mColors.add("cdd106");
        mColors.add("999e03");
        mColors.add("7bd204");
        mColors.add("699e01");
        mColors.add("366a01");
        mColors.add("6bcece");
        mColors.add("1797d0");
        mColors.add("065ed2");
        mColors.add("2f289d");
        mColors.add("aabcdd");
        mColors.add("fbabc7");
        mColors.add("e6428a");
        mColors.add("96239d");
        mColors.add("63259f");
        mColors.add("dacb8f");
        mColors.add("fbe1e0");
        mColors.add("e65277");
        mColors.add("654740");
        mColors.add("2e465d");
        mColors.add("0078ad");
        mColors.add("924665");
        mColors.add("53425f");
        mColors.add("8a714d");
        mColors.add("366477");
        mColors.add("f9cfd0");
        mColors.add("5a4839");
        mColors.add("727155");
        mColors.add("876a7b");
        mColors.add("61bfdb");
        mColors.add("bca78d");
        mColors.add("5b8158");
        mColors.add("6ca8be");
        mColors.add("999747");
        mColors.add("d1c6b0");
        mColors.add("e4a245");
        mColors.add("cde1f4");
        mColors.add("fefbe9");
        mColors.add("ad4a31");
        mColors.add("98c2c3");
        mColors.add("f28825");
        mColors.add("b83e48");
        mColors.add("8cb272");
        mColors.add("ee6b47");
        mColors.add("9a3133");
        mColors.add("cfcf94");
        mColors.add("ca1e2f");
        mColors.add("7f4143");
        mColors.add("fedf99");

        return mColors;
    }
}
