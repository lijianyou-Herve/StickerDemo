package com.wanyueliang.stickerdemo.wediget.slider;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wanyueliang.stickerdemo.R;
import com.wanyueliang.stickerdemo.bean.EditBean;
import com.wanyueliang.stickerdemo.bean.MaterialBean;
import com.wanyueliang.stickerdemo.ui.adapter.MaterialAdapter;

import java.util.List;

/**
 * 组合了{@link RangeDoubleSliderView}和{@link RecyclerView}的功能，
 * 实现了双滑块区域的调整，滚动停止某个对应的编辑区域自动显示双滑块，开始滚动的时候隐藏
 * =================
 * 数据设置
 * {@link #setMaterialData(List, float)}
 * 设置底层预览条的数据集合和总的时间长度
 * {@link #setEditData(List)}
 * 设置阴影的数据集合，即添加的素材的信息集(除了是否是显示状态以外的{开始时间，结束时间，类型:文字/图片/水印。。。。})
 * {@link #notifyDataSetChange()}
 * 当EditBean的数据改变了之后，调用来刷新UI
 * =================
 * 监听回调
 * 所有的监听都集中到{@link OnScrollChangeListener}包含了
 * {@link RecyclerView.OnScrollListener} 和 {@link RangeDoubleSliderView.OnSliderChangerListener}
 * 的全部功能，需要实现的自己选择复写即可，必须实现的有
 * {@link OnScrollChangeListener#onMatchEditSelected(EditBean)}匹配到某一个区域之后的回调
 * {@link OnScrollChangeListener#onMatchEditChange(EditBean)}匹配到的区域某一个区域之后的回调
 * =================
 */
public class ScrollCropView extends FrameLayout {

    private final String TAG = getClass().getSimpleName();
    private Context mContext;
    /*View*/
    private TextView mTvCurrentTime;
    private TextView mTvTotalTime;
    private LinearLayout mLlMiddleLine;
    private RecyclerView mRvMaterial;
    private ShadowDoubleSliderView mDsSlider;
    private TextView mTvLeftTime;
    private TextView mTvRightTime;

    /*data*/
    private float mViewWidth;//整体的宽度
    private float editViewHeight;//核心数据----编辑的View的高度，recyclerView和DoubleSliderView
    private float relativeDuration = 5f;//核心数据----每一份editViewHeight对应的时间
    private float mTotalTime;//核心数据----总时间
    private float precisionValue = 100f;//核心数据----精度

    private float sliderWidth;//滑块的宽度的高度,默认为DoubleSliderView高度的三分之一 editViewHeight/3f
    private float scrollCurrentTime;//当前中心轴对应的时间
    private int mOffsetX;//记录RecyclerView总偏移量
    private EditBean showEditBean;//当前编辑的区域

    private MaterialAdapter materialAdapter;
    private List<MaterialBean> mData;
    private View headView;
    private View footerView;

    private List<EditBean> mEditBeans;//阴影相关
    private RangeDoubleSliderView.Builder builder;
    private boolean mLimitValue;//是否有限制
    private float mLeftLimitTime;//记录左边的限制时间
    private float mRightLimitTime;//记录右边的限制时间

    public ScrollCropView(Context context) {
        this(context, null);
    }

    public ScrollCropView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollCropView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mContext = getContext();
        LayoutInflater.from(mContext).inflate(R.layout.view_scroll_crop_view_layout, this, true);

        mTvCurrentTime = (TextView) findViewById(R.id.tv_current_time);
        mTvTotalTime = (TextView) findViewById(R.id.tv_total_time);

        mLlMiddleLine = (LinearLayout) findViewById(R.id.ll_middle_line);

        mRvMaterial = (RecyclerView) findViewById(R.id.rv_material);
        mDsSlider = (ShadowDoubleSliderView) findViewById(R.id.ds_slider);

        mTvLeftTime = (TextView) findViewById(R.id.tv_left_time);
        mTvRightTime = (TextView) findViewById(R.id.tv_right_time);

    }

    /**
     * 界面生成，初始化数据
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            mViewWidth = w;
            editViewHeight = mDsSlider.getMeasuredHeight();
            Log.i(TAG, "onSizeChanged: editViewHeight=" + editViewHeight);
            initData();
            initListener();
        }
    }

    /*底层素材的数据*/
    public void setMaterialData(List<MaterialBean> materialBeans, float totalTime) {
        mDsSlider.hideSlider();
        this.mData = materialBeans;
        this.mTotalTime = totalTime;
        mTvTotalTime.setText(String.valueOf(balanceValue(mTotalTime)));
        if (materialAdapter != null) {
            materialAdapter.setData(mData);
            materialAdapter.notifyDataSetChanged();
        }
    }

    /*添加的素材区域数据*/
    public void setEditData(List<EditBean> editBeans) {
        mDsSlider.hideSlider();
        this.mEditBeans = editBeans;
        if (mDsSlider != null) {
            mDsSlider.setEditBeans(mEditBeans);
            mDsSlider.notifyDataChange();
        }
        //第一次进入，先匹配
        //检查是否有匹配到的区间
        checkMatching();
    }

    /*更新添加的素材区域数据*/
    public void notifyDataSetChange() {
        mDsSlider.hideSlider();
        if (mDsSlider != null) {
            mDsSlider.notifyDataChange();
        }
        //第一次进入，先匹配
        //检查是否有匹配到的区间
        checkMatching();
    }

    private void initData() {

        materialAdapter = new MaterialAdapter(mContext, relativeDuration, editViewHeight);//设置底层的素材预览图

        materialAdapter.setData(this.mData);

        mDsSlider.setEditBeans(mEditBeans);//设置编辑的元素

        mTvTotalTime.setText(String.valueOf(balanceValue(mTotalTime)));

        mRvMaterial.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));

        /*在RecyclerView添加前面和后面的占位View*/
        addPlaceholderView();

        mRvMaterial.setAdapter(materialAdapter);
        // 滑块的宽度等于滑块的高度/3f
        sliderWidth = editViewHeight / 3f;

        float totalCurrent = ((mViewWidth - sliderWidth * 2f) / editViewHeight) * relativeDuration;

        int sliderMinCurrent = (int) (1 * precisionValue);//为了精准度，设置的数值增大10倍
        int sliderTotalCurrent = (int) (totalCurrent * precisionValue + 0.5f);//为了精准度，设置的数值增大10倍

        builder = new RangeDoubleSliderView.Builder();
        builder.setMinCurrent(sliderMinCurrent)
                .setStartCurrent(sliderTotalCurrent / 2)//起点为中心轴
                .setDurationCurrent(sliderTotalCurrent / 2)//持续时间
                .setTotalCurrent(sliderTotalCurrent);

        mDsSlider.setBuilder(builder);//设置DoubleSliderView的参数

        mDsSlider.setLimitView(headView, footerView);//设置限制左右滑块的坐标相关的View

        //第一次进入，先匹配
        //检查是否有匹配到的区间
        checkMatching();
    }

    /**
     * 在RecyclerView添加前面和后面的占位View
     */
    private void addPlaceholderView() {
        headView = LayoutInflater.from(mContext).inflate(R.layout.item_placeholder_layout, mRvMaterial, false);
        setViewWidth(headView, mViewWidth / 2f);
        materialAdapter.addHeaderView(headView);

        footerView = LayoutInflater.from(mContext).inflate(R.layout.item_placeholder_layout, mRvMaterial, false);
        setViewWidth(footerView, mViewWidth / 2f);
        materialAdapter.addFooterView(footerView);
    }

    /*占位View的宽度为重宽度的一半*/
    private void setViewWidth(View view, float w) {

        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) view.getLayoutParams();
        marginLayoutParams.width = (int) (w + 0.5f);
        view.setLayoutParams(marginLayoutParams);
    }

    /*设置监听*/
    private void initListener() {

        mRvMaterial.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {//停止滑动的时候，寻找对应的View
                    //检查是否有匹配到的区间
                    checkMatching();
                } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    mDsSlider.hideSlider();//手动拖动的时候，隐藏滑块
                }

                if (mOnScrollChangeListener != null) {
                    mOnScrollChangeListener.onScrollStateChanged(recyclerView, newState);
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                Log.i(TAG, "onScrolled: " + headView.getX());

                mOffsetX += dx;//RecyclerView整体的滑动偏移量
                //得到中心轴对应的时间
                scrollCurrentTime = balanceValue(mOffsetX / editViewHeight * relativeDuration * precisionValue) / precisionValue;

                //设置中心轴对应的时间
                mTvCurrentTime.setText(String.valueOf(scrollCurrentTime));

                //使ShadowDoubleSliderViewView跟随滚动
                mDsSlider.setCustomScrollX(scrollCurrentTime, mOffsetX, dx);

                if (mOnScrollChangeListener != null) {
                    mOnScrollChangeListener.onScrolled(recyclerView, dx, dy);
                }
            }
        });

        mDsSlider.addOnSliderChangerListener(new RangeDoubleSliderView.OnSliderChangerListener() {
            @Override
            public void onStartTouch(int touchThumb) {

                if (mOnScrollChangeListener != null) {
                    mOnScrollChangeListener.onStartTouch(touchThumb);
                }
            }

            @Override
            public void onLeftSliderChange(int leftCurrent, int rightCurrent, int totalCurrent) {
                /*根据差值，计算起始的时间和结束的时间*/
                calculateTime(leftCurrent, rightCurrent);

                if (mOnScrollChangeListener != null) {
                    mOnScrollChangeListener.onLeftSliderChange(leftCurrent, rightCurrent, totalCurrent);
                }
            }

            @Override
            public void onRightSliderChange(int leftCurrent, int rightCurrent, int totalCurrent) {
                //根据差值计算时间
                calculateTime(leftCurrent, rightCurrent);

                if (mOnScrollChangeListener != null) {
                    mOnScrollChangeListener.onRightSliderChange(leftCurrent, rightCurrent, totalCurrent);
                }
            }

            @Override
            public void onStopTouch(int touchThumb) {
                //滑动结束，滚到到编辑的起始or结尾
                if (showEditBean != null) {
                    if (touchThumb == ShadowDoubleSliderView.LEFT_THUMB) {
                        seekTo(showEditBean.getStartTime());
                    }
                    if (touchThumb == ShadowDoubleSliderView.RIGHT_THUMB) {
                        seekTo(showEditBean.getEndTime());
                    }

                    if (mOnScrollChangeListener != null) {
                        mOnScrollChangeListener.onMatchEditChange(showEditBean);
                    }
                }

                if (mOnScrollChangeListener != null) {
                    mOnScrollChangeListener.onStopTouch(touchThumb);
                }

            }
        });

    }

    /**
     * 检查是否有匹配到的区间
     */
    private void checkMatching() {
        if (mEditBeans != null && mEditBeans.size() > 0) {
            int editSize = mEditBeans.size();
            float leftSeekTo;
            float rightSeekTo;
            for (int i = editSize - 1; i >= 0; i--) {
                EditBean editBean = mEditBeans.get(i);
                Log.i(TAG, "onTouchEvent:getStartTime= " + editBean.getStartTime());
                if (editBean.getStartTime() <= scrollCurrentTime && editBean.getEndTime() >= scrollCurrentTime) {
                    getLimitTime(editBean);
                    leftSeekTo = editBean.getStartTime() / relativeDuration * editViewHeight + mViewWidth / 2 - mOffsetX - sliderWidth;
                    rightSeekTo = leftSeekTo + (editBean.getEndTime() - editBean.getStartTime()) / relativeDuration * editViewHeight + sliderWidth;
                    Log.i(TAG, "触摸结束: leftSeekTo=" + leftSeekTo);
                    Log.i(TAG, "触摸结束: rightSeekTo=" + rightSeekTo);

                    if (editBean.isShow()) {
                        //Nothing
                        mDsSlider.showSlider();
                    } else {
                        if (showEditBean != null) {
                            showEditBean.setShow(false);
                        }
                        mDsSlider.showSlider();
                        editBean.setShow(true);
                        showEditBean = editBean;
                        mDsSlider.setTran(leftSeekTo, rightSeekTo);
                    }

                    break;
                }

                //如果没有匹配到的空间，则隐藏滑块和移除记录的选中的数据
                if (i == editSize - 1) {
                    if (showEditBean != null) {
                        showEditBean.setShow(false);
                        showEditBean = null;
                    }
                }

            }

            if (mOnScrollChangeListener != null) {
                mOnScrollChangeListener.onMatchEditSelected(showEditBean);
            }

        }
    }

    public void setLimit(boolean mLimitValue) {
        this.mLimitValue = mLimitValue;
        mDsSlider.setLimit(mLimitValue);
    }

    public boolean getLimit() {
        return mLimitValue;
    }

    private void getLimitTime(EditBean editBean) {
        if (!mLimitValue) {
            mDsSlider.setLimit(false);
            return;
        }

        int editSize = mEditBeans.size();

        if (!editBean.isShow()) {
            mLeftLimitTime = 0;
            mRightLimitTime = mTotalTime;
            for (int j = 0; j < editSize; j++) {
                EditBean tempBean = mEditBeans.get(j);
                if (tempBean != editBean) {
                    if (tempBean.getEndTime() < scrollCurrentTime && tempBean.getEndTime() > mLeftLimitTime) {
                        mLeftLimitTime = tempBean.getEndTime();
                    }

                    if (tempBean.getStartTime() > scrollCurrentTime && tempBean.getStartTime() < mRightLimitTime) {
                        mRightLimitTime = tempBean.getStartTime();
                    }
                }
            }
        }

        float left = (int) (mViewWidth / 2f + mLeftLimitTime / 5f * editViewHeight - mOffsetX) - sliderWidth;
        float right = (int) (mViewWidth / 2f + mRightLimitTime / 5f * editViewHeight - mOffsetX) + 1;

        mDsSlider.setLimitValue(left, right);

        Log.i(TAG, "限制时间最小值: leftLimitTime=" + mLeftLimitTime);
        Log.i(TAG, "限制时间最大值: rightLimitTime=" + mRightLimitTime);
    }

    public float getLeftLimitTime() {

        int editSize = mEditBeans.size();

        mLeftLimitTime = 0;
        for (int j = 0; j < editSize; j++) {
            EditBean tempBean = mEditBeans.get(j);
            if (tempBean.getEndTime() < scrollCurrentTime && tempBean.getEndTime() > mLeftLimitTime) {
                mLeftLimitTime = tempBean.getEndTime();
            }

        }

        Log.i(TAG, "限制时间最小值: leftLimitTime=" + mLeftLimitTime);

        return mLeftLimitTime;
    }

    public float getRightLimitTime() {

        int editSize = mEditBeans.size();

        mRightLimitTime = mTotalTime;
        for (int j = 0; j < editSize; j++) {
            EditBean tempBean = mEditBeans.get(j);

            if (tempBean.getStartTime() > scrollCurrentTime && tempBean.getStartTime() < mRightLimitTime) {
                mRightLimitTime = tempBean.getStartTime();
            }
        }

        Log.i(TAG, "限制时间最大值: rightLimitTime=" + mRightLimitTime);

        return mRightLimitTime;

    }


    /*使中心轴滚动到对应的时间*/
    public void seekTo(float currentTime) {
        mRvMaterial.scrollBy((int) (-(scrollCurrentTime - currentTime) * editViewHeight / relativeDuration), 0);

    }

    /*平衡误差*/
    private int balanceValue(float value) {
        if (value % 0.5f != 0) {
            value += 0.5f;
        } else {
            //Nothing
            Log.i(TAG, "balanceValue: 有等于0的value=" + value);
        }
        return (int) value;
    }

    public float getCurrentTime() {
        return scrollCurrentTime;
    }

    public EditBean getShowEditBean() {
        return showEditBean;
    }

    /**
     * 计算，调整为滑动对应的时间
     */
    private void calculateTime(int leftCurrent, int rightCurrent) {
        float sliderOffsetX = (mDsSlider.getLeftTranX() - mViewWidth / 2f + sliderWidth);//计算和中心的偏移量差值
        float offsetTime = sliderOffsetX / editViewHeight * relativeDuration + scrollCurrentTime;//计算和中心的时间差值

        float leftTimeValue = balanceValue((offsetTime * precisionValue + leftCurrent)) / precisionValue;//计算其实时间
        float rightTimeValue = balanceValue(leftTimeValue * precisionValue + rightCurrent - leftCurrent) / precisionValue;//结束时间

        Log.i(TAG, "onLeftSliderChange: leftTimeValue=" + leftTimeValue);
        Log.i(TAG, "onLeftSliderChange: rightTimeValue=" + rightTimeValue);
        if (rightTimeValue != 20) {
            Log.i(TAG, "onLeftSliderChange: 不等于20 rightTimeValue=" + rightTimeValue);
        }

        mTvLeftTime.setText(String.valueOf(leftTimeValue));
        mTvRightTime.setText(String.valueOf(rightTimeValue));

        if (showEditBean != null) {
            showEditBean.setStartTime(leftTimeValue);
            showEditBean.setEndTime(rightTimeValue);
            mDsSlider.invalidate();
        }

    }

    private OnScrollChangeListener mOnScrollChangeListener;

    public void setOnEditScrollChangeListener(OnScrollChangeListener onScrollChangeListener) {
        this.mOnScrollChangeListener = onScrollChangeListener;
    }

    /**
     * 为了可能的需要，该回调结合了{@link RecyclerView.OnScrollListener} 和 {@link RangeDoubleSliderView.OnSliderChangerListener}
     * 并且实现了接口的方法，但是没有做处理。外部有需要的可以选择复写特定的方法来处理
     */
    public abstract static class OnScrollChangeListener extends RecyclerView.OnScrollListener implements RangeDoubleSliderView.OnSliderChangerListener {

        //匹配到对应的编辑区域
        public abstract void onMatchEditSelected(EditBean editBean);

        public abstract void onMatchEditChange(EditBean editBean);

        @Override
        public void onStartTouch(int touchThumb) {
        }

        @Override
        public void onLeftSliderChange(int leftCurrent, int rightCurrent, int totalCurrent) {
        }

        @Override
        public void onRightSliderChange(int leftCurrent, int rightCurrent, int totalCurrent) {
        }

        @Override
        public void onStopTouch(int touchThumb) {
        }
    }

}