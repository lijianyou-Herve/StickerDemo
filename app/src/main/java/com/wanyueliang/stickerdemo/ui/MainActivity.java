package com.wanyueliang.stickerdemo.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wanyueliang.stickerdemo.R;
import com.wanyueliang.stickerdemo.bean.EditBean;
import com.wanyueliang.stickerdemo.bean.MaterialBean;
import com.wanyueliang.stickerdemo.lisntener.OnAdapterItemClickListener;
import com.wanyueliang.stickerdemo.ui.adapter.StickerAdapter;
import com.wanyueliang.stickerdemo.wediget.slider.ScrollCropView;
import com.wanyueliang.stickerdemo.wediget.sticker.StickerHelper;
import com.wanyueliang.stickerdemo.wediget.sticker.StickerModel;
import com.wanyueliang.stickerdemo.wediget.sticker.SuperStickerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";

    private Context mContext;
    /*View*/
    private ScrollCropView mShadowDoubleSliderView;
    private SuperStickerView mSvSticker;
    private RecyclerView mRvAddSticker;
    /*data*/
    private List<MaterialBean> scrollData;//底层的预览条数据
    private float totalTime;
    private StickerAdapter stickerAdapter;
    private List<MaterialBean> stickerData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        findView();
        initView();
        initData();
        initListener();
    }

    private void findView() {

        mShadowDoubleSliderView = (ScrollCropView) findViewById(R.id.shadow_double_slider_view);
        mSvSticker = (SuperStickerView) findViewById(R.id.sv_sticker);
        mRvAddSticker = (RecyclerView) findViewById(R.id.rv_add_sticker);

    }

    private void initView() {

    }

    private void initData() {

        initScrollData();//滑动的底层图像

        initStickerData();//模拟的贴纸的数据
    }

    private void initStickerData() {
        stickerAdapter = new StickerAdapter(mContext);
        stickerData = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            stickerData.add(new MaterialBean());
        }
        stickerAdapter.setData(stickerData);
        mRvAddSticker.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
        mRvAddSticker.setAdapter(stickerAdapter);
    }

    private void initScrollData() {
        scrollData = new ArrayList<>();

        for (int i = 0; i < 20; i++) {//数据模拟
            MaterialBean materialBean = new MaterialBean();

            if (i == 0) {
                totalTime += 5f;
                materialBean.setDuration(5f);
                materialBean.setColorRes(Color.YELLOW);
            } else if (i == 1) {
                totalTime += 15f;
                materialBean.setDuration(15f);
                materialBean.setColorRes(Color.GREEN);
            } else if (i == 6) {
                totalTime += 8f;
                materialBean.setDuration(8f);
                materialBean.setColorRes(Color.BLACK);
            } else if (i == 12) {
                totalTime += 1f;
                materialBean.setDuration(1f);
                materialBean.setColorRes(Color.RED);
            } else {
                totalTime += 5f;
                materialBean.setDuration(5f);
                materialBean.setColorRes(getResources().getColor(R.color.colorAccent));
            }

            scrollData.add(materialBean);

        }

        mShadowDoubleSliderView.setMaterialData(scrollData, totalTime);
    }


    private void initListener() {
        mShadowDoubleSliderView.setOnEditScrollChangeListener(new ScrollCropView.OnScrollChangeListener() {
            @Override
            public void onMatchEditSelected(EditBean editBean) {

                Log.i(TAG, "onMatchEditSelected: ");
            }

            @Override
            public void onMatchEditChange(EditBean editBean) {

                Log.i(TAG, "onMatchEditSelected: ");

            }

            @Override
            public void onLeftSliderChange(int leftCurrent, int rightCurrent, int totalCurrent) {
                super.onLeftSliderChange(leftCurrent, rightCurrent, totalCurrent);
            }

            @Override
            public void onRightSliderChange(int leftCurrent, int rightCurrent, int totalCurrent) {
                super.onRightSliderChange(leftCurrent, rightCurrent, totalCurrent);
            }
        });

        stickerAdapter.setOnAdapterItemClickListener(new OnAdapterItemClickListener() {
            @Override
            public void OnAdapterItemClickListener(RecyclerView.ViewHolder holder, View view, int position) {

                if (position % 4 == 0) {
                    Drawable drawable = getResources().getDrawable(R.mipmap.ic_launcher);

                    Rect rect = new Rect();
                    rect.left = 0;
                    rect.right = drawable.getIntrinsicWidth();
                    rect.top = 0;
                    rect.bottom = drawable.getIntrinsicHeight();

                    StickerModel stickerModel = new StickerModel();
                    stickerModel.setAlpha(255);
                    stickerModel.setCanEdit(true);

                    mSvSticker.addSticker(getResources().getDrawable(R.mipmap.ic_launcher), stickerModel, new Matrix(), rect, true);
                } else {

                    StickerModel stickerModel = new StickerModel();
                    stickerModel.setAlpha(255);
                    stickerModel.setCanEdit(true);

                    addTextViewSticker(stickerModel, "来一个文字");

                }


            }
        });
    }

    /**
     * 添加文字类型的素材贴纸
     */
    public void addTextViewSticker(StickerModel stickerModel, String inputString) {
        TextView textView = StickerHelper.buildTextView(mContext, inputString, stickerModel);
        mSvSticker.addTextViewSticker(textView, stickerModel, new Matrix(), null, true);
    }

}
