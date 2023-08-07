package com.wanyueliang.stickerdemo.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.wanyueliang.stickerdemo.R;
import com.wanyueliang.stickerdemo.base.HeadFootBaseAdapter;
import com.wanyueliang.stickerdemo.bean.MaterialBean;
import com.wanyueliang.stickerdemo.lisntener.OnAdapterItemClickListener;
import com.wanyueliang.stickerdemo.wediget.image.ShapeView;


public class StickerAdapter extends HeadFootBaseAdapter<StickerAdapter.MaterialViewHolder, MaterialBean> {

    private OnAdapterItemClickListener mOnAdapterItemClickListener;

    public StickerAdapter(Context mContext) {
        super(mContext);
    }

    private ShapeView.Shape[] mShape = {ShapeView.Shape.circle, ShapeView.Shape.square, ShapeView.Shape.Rectangle, ShapeView.Shape.triangle};

    @Override
    protected MaterialViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_sticker_layout, parent, false);
        return new MaterialViewHolder(view);
    }

    @Override
    protected void onBindItemViewHolder(final MaterialViewHolder holder, final int position) {

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnAdapterItemClickListener != null) {
                    mOnAdapterItemClickListener.OnAdapterItemClickListener(holder, v, position);
                }
            }
        });

        holder.mShapeView.setShape(mShape[position % 4]);

    }

    public void setOnAdapterItemClickListener(OnAdapterItemClickListener onAdapterItemClickListener) {
        this.mOnAdapterItemClickListener = onAdapterItemClickListener;
    }

    class MaterialViewHolder extends RecyclerView.ViewHolder {
        private ShapeView mShapeView;

        MaterialViewHolder(View itemView) {
            super(itemView);
            mShapeView = (ShapeView) itemView.findViewById(R.id.shapeView);
        }
    }
}