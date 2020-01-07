### AdapterDemo
RecyclerView 万能适配器

#### (一)androidX 使用

项目 gradle.properties
android.useAndroidX=true
android.enableJetifier=true

项目 build.gradle
implementation 'com.rq.adapter:androidx_rvlib:1.0.0'

#### (二)v7 使用

项目 build.gradle
implementation 'com.rq.adapter:android_rvlib:1.0.0'

注:两个版本的使用方法
基本一致，只是导入的支持包不一样，根据项目具体使用的包调整


# 使用细则：
  参照 MainActivity 查看 BaseAdapter，注释写的很详细

1.BaseAdapter 的构造函数中，最后一栏为 Object 的都是针对使用内部ViewHolder类的，因为反射需要
  注意要为 public * ,若ViewHolder为单独的文件则无需传入此参数

### 未完待续...
