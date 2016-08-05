# 欢迎使用 TvRecyclerView

首先感谢lucasr开发出杰出的作品[TwoWayView](https://github.com/lucasr/twoway-view),**TvRecyclerView**就是在[TwoWayView](https://github.com/lucasr/twoway-view)的基础上进行的延伸，即：

> * 修复了一些小bug
> * 针对TV端的特性进行了适配与开发

### 效果

![](https://github.com/zhousuqiang/TvRecyclerView/blob/master/images/img_spannable.png)

![](https://github.com/zhousuqiang/TvRecyclerView/blob/master/images/img_staggered.png)

![](https://github.com/zhousuqiang/TvRecyclerView/blob/master/images/img_grid.png)

![](https://github.com/zhousuqiang/TvRecyclerView/blob/master/images/img_list.png)

### Android Studio 集成

```java
compile 'com.tv.boost:tv-recyclerview:1.0.0'
```

### 特性

- [x] 支持横/竖排列
    ```java
    android:orientation="horizontal"
    ```

- [x] 支持布局指定LayoutManager
    ```java
    app:tv_layoutManager="SpannableGridLayoutManager"
    ```

- [x] 支持设置选中Item边缘距离/居中
    ```java
    setSelectedItemAtCentered(boolean isCentered)
    setSelectedItemOffset(int offsetStart, int offsetEnd)
    ```

- [x] 支持设置横竖间距
    ```java
    setSpacingWithMargins(int verticalSpacing, int horizontalSpacing)
    ```

- [x] Item监听回调
    ```java
    mRecyclerView.setOnItemListener(new TvRecyclerView.OnItemListener() {
        @Override
        public void onItemPreSelected(TvRecyclerView parent, View itemView, int position) {
                
        }

        @Override
        public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                
        }

        @Override
        public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                
        }
    });
    ```


### 更详细的使用请见exmaple

------


作者 [owen](https://github.com/zhousuqiang)
