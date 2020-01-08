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

注:两个版本的使用方法，基本一致，只是导入的支持包不一样，根据项目具体使用的包调整
<br/>以androidx_rvlib为主，后续更新以及使用细则皆以该包为准
<br/>适配器主要思路是一个业务(同一种Item)内容需要新建一个ViewHolder 继承自 BaseViewHolder,
<br/>BaseAdapter 则只需要新建一个即可，常规用法新建时使用 new BaseAdapter(Context,int,Class)即可
<br/>具体的视图业务在ViewHolder 的 fillData(int position, Object data) 方法即可，如以下代码：
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

1.BaseAdapter 的构造函数中，最后一栏为 Object 的都是针对使用内部ViewHolder类的，因为反射需要
  注意要为 public * ,若ViewHolder为单独的文件或者静态内部类则无需传入此参数

### 未完待续...
