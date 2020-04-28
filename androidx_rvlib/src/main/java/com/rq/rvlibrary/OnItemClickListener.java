package com.rq.rvlibrary;

import android.view.View;

public interface OnItemClickListener<DATA> {
    void onItemClick(BaseViewHolder holder, DATA bean, View view, int position);
}