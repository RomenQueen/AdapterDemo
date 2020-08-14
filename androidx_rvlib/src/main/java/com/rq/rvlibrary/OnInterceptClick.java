package com.rq.rvlibrary;

import android.view.View;

public interface OnInterceptClick {
    /**
     * ViewHolder 点击事件拦截器 返回 true 将会拦截 addOnItemClickListener 同一 ViewId 的回调
     * 所以先于 addOnItemClickListener 逻辑的执行
     */
    boolean intercept(Object object, View view, int position);

    //需要安装点击监听的视图id
    int[] clickIds();
}
