package com.rq.adapter;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.rq.rvlibrary.ActionPasser;
import com.rq.rvlibrary.BaseAdapter;
import com.rq.rvlibrary.BaseViewHolder;
import com.rq.rvlibrary.RecyclerUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ActionPasser {
    BaseAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView rv = findViewById(R.id.rv);
        rv.setLayoutManager(new RecyclerUtil().build());
        mAdapter = new BaseAdapter(this, R.layout.item_example, ExampleViewHolder1.class, this);
        rv.setAdapter(mAdapter);
        List<String> debug = new ArrayList<>();
        debug.add("111");
        debug.add("222");
        debug.add("3333");
        debug.add("44424");
        debug.add("55551");
        debug.add("666666666422");
        debug.add("77777777777344233");
        debug.add("88888888888844424");
        debug.add("99999999914421");
        debug.add("00000000000024422");
        debug.add("111111111344233");
        debug.add("222222244424");
        debug.add("333333331678901");
        debug.add("444442678902");
        debug.add("55555555555536789033");
        debug.add("6666666664678904");
//        mAdapter.addFootHolder(6666, FootHolder.class, R.layout.item_example, MainActivity.this);
//        mAdapter.addHeadHolder(444, FootHolder.class, R.layout.item_example, MainActivity.this);
        mAdapter.setData(debug);
        mAdapter.setDisplay(new BaseAdapter.DisplayOption<String>() {
            @Override
            public boolean show(String data, Object tag, int position) {
                return data.startsWith("1") || data.endsWith("3");
            }
        });

        showHeadViewHolder();
    }

    @Override
    public void onAction(int action, Object data) {
        HeadViewHolder.DebugData bean = (HeadViewHolder.DebugData) data;
        if (action == R.id.txt_left) {
            Toast.makeText(MainActivity.this, "点击了左边->" + (bean == null ? "null" : bean.realContent), Toast.LENGTH_SHORT).show();
        } else if (action == R.id.txt_right) {
            Toast.makeText(MainActivity.this, "点击了右边-> " + (bean == null ? "null" : bean.realContent), Toast.LENGTH_SHORT).show();
        }
    }

    private void showHeadViewHolder() {
        mAdapter.setActionPasser(this);
        mAdapter.setChildClick(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onClick(Object data, View view) {
                if (view.getId() == R.id.add_left) {
                    Log.e("MainActivity", "LINE(add_left):");
                    HeadViewHolder.DebugData left = new HeadViewHolder.DebugData(true, System.currentTimeMillis() + "");
                    mAdapter.setHeadHolder(0, left, HeadViewHolder.class, R.layout.item_head_view);
                } else if (view.getId() == R.id.add_right) {
                    Log.e("MainActivity", "LINE(add_right):");
                    HeadViewHolder.DebugData right = new HeadViewHolder.DebugData(false, System.currentTimeMillis() + "");
                    mAdapter.setHeadHolder(0, right, HeadViewHolder.class, R.layout.item_head_view);
                }
            }
        }, R.id.add_left, R.id.add_right);
        mAdapter.setHeadHolder(1, null, HeadViewHolder.class, R.layout.item_head_view);
    }

    public void click1(View view) {
        mAdapter.display(null);
    }

    public void click2(View view) {
        mAdapter.display("");
    }


    public class ExampleViewHolder1 extends BaseViewHolder<String> {

        public ExampleViewHolder1(View itemView) {
            super(itemView);
        }

        @Override
        public void fillData(int position, String o) {
            super.fillData(position, o);
            setTextToView(R.id.txt, o);
            Log.d("ExampleViewHolder1", position + " : " + o);
        }
    }

    public class FootHolder extends BaseViewHolder {
        public FootHolder(View itemView) {
            super(itemView);
        }

        @Override
        public int inflateLayoutId() {
            return R.layout.item_example;
        }

        @Override
        public void fillObject(@Nullable Object data) {
            super.fillObject(data);
            setTextToView(R.id.txt, "特殊视图，可以不用同一个Layout,示例只是省事，fillData -> fillObject : " + data);
        }
    }
}
