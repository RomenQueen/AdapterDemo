package com.rq.adapter;


import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.rq.rvlibrary.BaseAdapter;
import com.rq.rvlibrary.BaseViewHolder;
import com.rq.rvlibrary.RecyclerUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView rv = findViewById(R.id.rv);
        rv.setLayoutManager(new RecyclerUtil().build());
        BaseAdapter adapter = new BaseAdapter(this, R.layout.item_example, ExampleViewHolder1.class, this);
        rv.setAdapter(adapter);
        List<String> debug = new ArrayList<>();
        debug.add("111");
        debug.add("222");
        debug.add("3333");
        debug.add("44424");
        debug.add("14421");
        debug.add("24422");
        debug.add("344233");
        debug.add("44424");
        debug.add("14421");
        debug.add("24422");
        debug.add("344233");
        debug.add("44424");
        debug.add("1678901");
        debug.add("2678902");
        debug.add("36789033");
        debug.add("4678904");
        adapter.addFootHolder(6666, FootHolder.class, R.layout.item_example, MainActivity.this);
        adapter.addHeadHolder(444, FootHolder.class, R.layout.item_example, MainActivity.this);
        adapter.setData(debug);
    }

    public class ExampleViewHolder1 extends BaseViewHolder<String> {
        public ExampleViewHolder1(View itemView) {
            super(itemView);
        }

        @Override
        public int inflateLayoutId() {
            return R.layout.item_example;
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
