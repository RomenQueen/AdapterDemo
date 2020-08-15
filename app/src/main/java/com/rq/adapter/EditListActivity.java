package com.rq.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.rq.rvlibrary.BaseAdapter;
import com.rq.rvlibrary.BaseViewHolder;
import com.rq.rvlibrary.OnInterceptClick;
import com.rq.rvlibrary.OnItemClickListener;
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
        mAdapter.addOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseViewHolder holder, Object bean, View view, int position) {
                if (view.getId() == R.id.btn_add) {
                    Toast.makeText(EditListActivity.this, "点击了添加", Toast.LENGTH_LONG).show();
                } else if (view.getId() == R.id.btn_cut) {
                    Toast.makeText(EditListActivity.this, "点击了减少", Toast.LENGTH_LONG).show();
                }
            }
        }, R.id.btn_add, R.id.btn_cut);
    }

    public void onResult(View view) {
        String res = "查看结果\n";
        res += new Gson().toJson(mAdapter.getItemInput());
        ((TextView) view).setText(res);
    }

    public void addItem(View view) {
        mAdapter.addData(false);
    }

    public class ViewHolder extends BaseViewHolder implements ViewDataGetter, OnInterceptClick {
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

        @Override
        public boolean intercept(Object object, View view, int position) {
            if (view.getId() == R.id.btn_add) {
                setTextToView(R.id.tv_num, ((Integer.parseInt(((TextView) getItemView(R.id.tv_num)).getText().toString()) + 1) + ""));
                return true;
            } else if (view.getId() == R.id.btn_cut) {
                setTextToView(R.id.tv_num, ((Integer.parseInt(((TextView) getItemView(R.id.tv_num)).getText().toString()) - 1) + ""));
                return false;
            }
            return false;
        }

        @Override
        public int[] clickIds() {
            return new int[]{R.id.btn_add, R.id.btn_cut};
        }
    }

}
