package com.wanyueliang.stickerdemo.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.collection.SparseArrayCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public abstract class HeadFootBaseAdapter<T extends RecyclerView.ViewHolder, V> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int BASE_ITEM_TYPE_HEADER = 100000;
    private static final int BASE_ITEM_TYPE_FOOTER = 200000;

    protected Context mContext;
    protected String TAG = getClass().getSimpleName() + "HeadFootBaseAdapter";
    protected List<V> data;

    private T t;
    private SparseArrayCompat<View> headerViews = new SparseArrayCompat<>();
    private SparseArrayCompat<View> footerViews = new SparseArrayCompat<>();

    private int VIE_TYPE_SIMPLE = 0;

    public HeadFootBaseAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<V> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void addHeaderView(View headerView) {
        int key = findHeaderKeyByView(headerView);
        if (key == -1) {
            headerViews.put(headerViews.size() + BASE_ITEM_TYPE_HEADER, headerView);
            notifyItemInserted(0);
        }

    }

    public void addFooterView(View footerView) {
        footerViews.put(footerViews.size() + BASE_ITEM_TYPE_FOOTER, footerView);
        notifyItemInserted(getItemCount());

    }

    public int getHeaderViewSize() {
        return headerViews.size();
    }

    public int getFooterViewSize() {
        return footerViews.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (position < getHeaderViewSize()) {
            return headerViews.keyAt(position);
        }
        if (position >= getItemCount() - getFooterViewSize()) {
            return footerViews.keyAt(position - data.size() - headerViews.size());
        }
        return VIE_TYPE_SIMPLE;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (headerViews.get(viewType) != null) {
            return new HeaderViewHolder(headerViews.get(viewType));
        } else if (footerViews.get(viewType) != null) {
            return new FooterViewHolder(footerViews.get(viewType));
        } else {
            return onCreateItemViewHolder(parent, viewType);
        }

    }

    protected abstract T onCreateItemViewHolder(ViewGroup parent, int viewType);

    protected abstract void onBindItemViewHolder(T holder, final int position);

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof HeaderViewHolder) {

            HeaderViewHolder headFootViewHolder = (HeaderViewHolder) holder;
        } else if (holder instanceof FooterViewHolder) {
            FooterViewHolder headFootViewHolder = (FooterViewHolder) holder;
        } else {

            onBindItemViewHolder((T) holder, position - headerViews.size());

        }


    }

    public void customNotifyRangeItemInsert(int positionStart, int itemCount) {
        positionStart += getHeaderViewSize();
        notifyItemRangeInserted(positionStart, itemCount);
    }

    public void customNotifyItemInsert(int position) {
        position += getHeaderViewSize();

        notifyItemInserted(position);
    }

    public void customNotifyItemChang(int position) {
        position += getHeaderViewSize();

        notifyItemChanged(position);
    }

    public void customNotifyItemRemove(int position) {
        position += getHeaderViewSize();

        notifyItemRemoved(position);
    }

    public void customNotifyItemRangeChang(int positionStart, int itemCount) {
        positionStart += getHeaderViewSize();
        notifyItemRangeChanged(positionStart, itemCount);
    }

    public void customNotifyItemRangeChang(int positionStart) {
        positionStart += getHeaderViewSize();
        int count = getItemCount() - positionStart;
        notifyItemRangeChanged(positionStart, count);
    }


    public void deleteHeaderView(View view) {
        int key = findHeaderKeyByView(view);
        if (key != -1) {
            headerViews.remove(key);
        }
    }

    public void deleteFooterView(View view) {
        int key = findFooterKeyByView(view);
        if (key != -1) {
            footerViews.remove(key);
        }
    }

    private int findFooterKeyByView(View view) {
        for (int i = 0; i < footerViews.size(); i++) {
            int key = footerViews.keyAt(i);
            if (footerViews.get(key) == view) {
                return key;
            }
        }

        return -1;
    }

    private int findHeaderKeyByView(View view) {
        for (int i = 0; i < headerViews.size(); i++) {
            int key = headerViews.keyAt(i);
            if (headerViews.get(key) == view) {
                return key;
            }
        }

        return -1;
    }


    @Override
    public int getItemCount() {

        int size = data == null ? 0 : data.size();
        if (size == 0) {
            return 0;
        } else {
            return size
                    + headerViews.size()
                    + footerViews.size();
        }

    }

    public int getSimpleCount() {
        return data.size();
    }

    private static class FooterViewHolder extends RecyclerView.ViewHolder {


        private FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {


        private HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }


}
