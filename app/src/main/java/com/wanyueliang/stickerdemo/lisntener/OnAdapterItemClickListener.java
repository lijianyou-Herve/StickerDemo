package com.wanyueliang.stickerdemo.lisntener;

import android.support.v7.widget.RecyclerView;
import android.view.View;


public interface OnAdapterItemClickListener {

    void OnAdapterItemClickListener(RecyclerView.ViewHolder holder, View view, int position);
}
