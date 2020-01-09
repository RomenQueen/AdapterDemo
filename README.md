### AdapterDemo
RecyclerView 万能适配器

#### (一)androidX 使用

项目 gradle.properties
```
android.useAndroidX=true
android.enableJetifier=true
```

项目 build.gradle
```
implementation 'com.rq.adapter:androidx_rvlib:1.0.0'
```

#### (二)v7 使用

项目 build.gradle
```
implementation 'com.rq.adapter:android_rvlib:1.0.0'
```

注:两个版本的使用方法，基本一致，只是导入的支持包不一样，根据项目具体使用的包调整,
，后续更新以及使用细则皆以 androidx_rvlib 最新包作说明
适配器主要思路是一个业务(同一种Item)内容需要新建一个ViewHolder 继承自 BaseViewHolder,
BaseAdapter 则只需要实例化一个即可，常规使用直接 new BaseAdapter(Context,int,Class)即可
具体的视图业务在ViewHolder 的 fillData(int position, Object data) 方法即可，如以下代码：
```
 public class ExampleViewHolder extends BaseViewHolder<String> {

        public ExampleViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void fillData(int position, String data) {
            super.fillData(position, data);
            setTextToView(R.id.txt, data);
        }
    }
```


# 使用细则：
<br/>
1.BaseAdapter 的构造函数中，最后一个参数innerClassContext为可选参数 为 Object... 的都是针对使用内部ViewHolder类的，因为反射需要
注意要为 public * ,若ViewHolder为单独的文件或者静态内部类则无需传入此参数
<br/>
2.数据填充调用setData()即可，添加数据则调用addData();
<br/>
3.BaseAdapter.setDisplay() 设置数据本地展示条件，BaseAdapter.display(Object rule)根据规则
内容展示数据，null-清除展示条件，全部展示;
4.添加点击事件<br/>主类逻辑
```
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
```
HeadViewHolder代码
```
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
```

### 未完待续...
