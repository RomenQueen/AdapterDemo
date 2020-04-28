package com.rq.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.rq.rvlibrary.BaseAdapter;
import com.rq.rvlibrary.BaseViewHolder;
import com.rq.rvlibrary.RecyclerUtil;
import com.rq.rvlibrary.ViewDataGetter;

public class EditListActivity extends Activity {

    BaseAdapter mAdapter;
    RecyclerView rv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_editlist);
        mAdapter = new BaseAdapter(this, R.layout.item_add_product, ViewHolder.class, this);
        rv = findViewById(R.id.recycler);
        rv.setLayoutManager(new RecyclerUtil().build());
        rv.setAdapter(mAdapter);
        mAdapter.addData(false);
    }

    public void onResult(View view) {
        String res = "查看结果\n";
        res += new Gson().toJson(mAdapter.getItemInput());
        ((TextView) view).setText(res);
    }

    public void addItem(View view) {
        mAdapter.addData(false);
    }

    public class ViewHolder extends BaseViewHolder implements ViewDataGetter {
        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public int inflateLayoutId() {
            return R.layout.item_add_product;
        }

        @Override
        public void fillData(int position, Object o) {
            super.fillData(position, o);
            setTextToView(R.id.tv_tip_1, "文字 " + position);
            setTextToView(R.id.tv_tip_2, "数字 " + position);
        }

        @Override
        public Object getViewData() {
            return "   " + getMPosition() + ".内容1：" + getTextFromView(R.id.et_1) + "  内容2：" + getTextFromView(R.id.et_2);
        }
    }

}
