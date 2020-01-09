package com.rq.adapter;

import android.view.View;

import androidx.annotation.Nullable;

import com.rq.rvlibrary.BaseViewHolder;

public class HeadViewHolder extends BaseViewHolder implements View.OnClickListener {
    public static class DebugData {
        public DebugData(boolean isLeft, String realContent) {
            this.isLeft = isLeft;
            this.realContent = realContent;
        }

        boolean isLeft = false;
        String realContent;
    }

    public HeadViewHolder(View itemView) {
        super(itemView);
        itemView.findViewById(R.id.txt_left).setOnClickListener(this);
        itemView.findViewById(R.id.txt_right).setOnClickListener(this);
    }

    @Override
    public int inflateLayoutId() {
        return R.layout.item_head_view;
    }

    @Override
    public void fillObject(@Nullable Object data) {
        super.fillObject(data);
        if (data instanceof DebugData) {
            DebugData bean = (DebugData) data;
            if (bean.isLeft) {
                setTextToView(R.id.txt_left, bean.realContent);
            } else {
                setTextToView(R.id.txt_right, bean.realContent);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (mActionPasser != null) {
            mActionPasser.onAction(view.getId(), getData());
        }
    }
}
