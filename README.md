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
          mAdapter.addOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseViewHolder holder, Object bean, View view, int position) {
                Log.e("MainActivity", "onItemClick:bean ->" + bean);
                Log.e("MainActivity", "onItemClick:holder ->" + holder);
                Log.e("MainActivity", "onItemClick:view ->" + view);
                Log.e("MainActivity", "onItemClick:position ->" + position);
            }
        });
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

##### 2020-04-28更新：
1.移除 v4 v7 兼容版本，需要使用的下载androidX版本，然后通过编译按照编译器指示重新导包
2.移除 setChildClick 方法，统一使用 addOnItemClickListener
3.精简BaseAdapter类，加入子项数据获取方法，加入无数据空数据布局显示

### 未完待续...
