package com.rq.rvlibrary;

public class OnClickMaker {
    OnItemClickListener clickListener;
    int[] clickIds;

    public OnClickMaker(OnItemClickListener clickListener, int... ids) {
        this.clickListener = clickListener;
        this.clickIds = ids;
    }
}
