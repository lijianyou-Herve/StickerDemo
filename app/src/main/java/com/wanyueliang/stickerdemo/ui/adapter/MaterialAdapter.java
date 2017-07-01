package com.wanyueliang.stickerdemo.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wanyueliang.stickerdemo.R;
import com.wanyueliang.stickerdemo.base.HeadFootBaseAdapter;
import com.wanyueliang.stickerdemo.bean.MaterialBean;


public class MaterialAdapter extends HeadFootBaseAdapter<MaterialAdapter.MaterialViewHolder, MaterialBean> {

    private float mViewHeight;
    private float mRelativeDuration = 5;

    public MaterialAdapter(Context mContext, float relativeDuration, float mViewHeight) {
        super(mContext);
        this.mRelativeDuration = relativeDuration;
        this.mViewHeight = mViewHeight;
    }

    @Override
    protected MaterialViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_scroll_material_layout, parent, false);
        return new MaterialViewHolder(view);
    }

    @Override
    protected void onBindItemViewHolder(MaterialViewHolder holder, int position) {
        MaterialBean materialBean = data.get(position);

        holder.itemView.setBackgroundColor(materialBean.getColorRes());

        holder.mTvDuration.setText(String.valueOf(materialBean.getDuration()));

        float width = materialBean.getDuration() / mRelativeDuration * mViewHeight;//5秒对应 90dp

        setViewWidth(holder.itemView, width, position);
    }

    private void setViewWidth(View view, float w, int position) {

        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        marginLayoutParams.width = (int) (w + 0.5f);
        Log.i(TAG, "setViewWidth: " + position + " ---height---=" + marginLayoutParams.width);
        view.setLayoutParams(marginLayoutParams);
    }

    class MaterialViewHolder extends RecyclerView.ViewHolder {
        private ImageView mIvMaterial;
        private TextView mTvDuration;

        MaterialViewHolder(View itemView) {
            super(itemView);
            mIvMaterial = (ImageView) itemView.findViewById(R.id.iv_material);
            mTvDuration = (TextView) itemView.findViewById(R.id.tv_duration);

        }
    }
}