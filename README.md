# DragDiooto 

[![](https://jitpack.io/v/moyokoo/Diooto.svg)](https://jitpack.io/#moyokoo/Diooto)

[English](https://github.com/moyokoo/Diooto/blob/master/README_English.md)

> 微博,微信图库效果,微信视频拖放效果,适配状态栏 、屏幕旋转 、全屏 、长图、GIF、视频


<img src="https://github.com/moyokoo/Media/blob/master/diooto1.gif?raw=true" height="500"/><img src="https://github.com/moyokoo/Media/blob/master/diooto2.gif?raw=true" height="500"/><img src="https://github.com/moyokoo/Media/blob/master/diooto3.gif?raw=true" height="500"/>

- 自动更新图片大小
- 可定制LoadingView
- 可定制Indicator
- 适配状态栏
- 适配屏幕旋转
- 适配全屏
- 适配长图
- 适配GIF
- 适配视频

##### 使用

```java
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}

dependencies {
	 implementation 'com.github.moyokoo:Diooto:${version}'
}
```



```java
//图片模式
Diooto diooto = new Diooto(context)
        .urls(normalImageUlr)
        //图片或者视频
        .type(DiootoConfig.PHOTO)
        //点击的位置 如果你的RecyclerView有头部View  则使用 .position(holder.getAdapterPosition(),headSize) headSize为头部布局数量
        .position(holder.getAdapterPosition())
        //可以传recylcerview自动识别(需要传在item布局中的viewId)  也可以手动传view数组
        .views(mRecyclerView,R.id.srcImageView)
        //设置选择器 默认CircleIndexIndicator  可实现IIndicator接口自定义
        .setIndicator(new CircleIndexIndicator())
        //设置进度条样式  默认DefaultProgress 可实现IProgress接口自定义
        .setProgress(new DefaultProgress())
        //在显示原图之前显示的图片  如果你列表使用Glide加载  这里也使用Glide加载
        .loadPhotoBeforeShowBigImage((sketchImageView, position12) -> sketchImageView.displayImage(normalImageUlr[holder.getAdapterPosition()]))
        .start();
```

##### 视频播放本身不提供自动识别大小的功能,视频部分全有开发者自己决定

为了更好的体验,你应该在视频完全加载之后再将图片隐藏,比如这样

```java
simpleControlPanel.setOnVideoPreparedListener(() -> {
        sketchImageView.setVisibility(View.GONE);
        progressView.setVisibility(View.GONE);
```

如果你没法判断,那你可以直接在`onVideoLoadEnd`接口的回调中直接隐藏
注意:你需要手动隐藏图片以及加载条

```java
//视频模式
Diooto diooto = new Diooto(context)
        .urls(normalImageUlr[position])
        .position(holder.getAdapterPosition())
        .views(holder.srcImageView)
        .type(DiootoConfig.VIDEO)
        //提供视频View 注意这里只需要提供对象
        .onProvideVideoView(() -> new VideoView(context))
        //显示视频加载之前的缩略图
        .loadPhotoBeforeShowBigImage((sketchImageView, position13) -> sketchImageView.displayImage(normalImageUlr[holder.getAdapterPosition()]))
        //动画到最大化时的接口
        .onVideoLoadEnd((dragDiootoView, sketchImageView,progressView) -> {
            VideoView videoView = (VideoView) dragDiootoView.getContentView();
            ControlPanel simpleControlPanel = new ControlPanel(context);
            simpleControlPanel.setOnClickListener(v -> dragDiootoView.backToMin());
            simpleControlPanel.setOnVideoPreparedListener(() -> {
                sketchImageView.setVisibility(View.GONE);
                progressView.setVisibility(View.GONE);
        });
            videoView.setControlPanel(simpleControlPanel);
            videoView.setUp("http://bmob-cdn-982.b0.upaiyun.com/2017/02/23/266454624066f2b680707492a0664a97.mp4");
            videoView.start();
            dragDiootoView.notifySize(1920, 1080);
            MediaPlayerManager.instance().setScreenScale(ScaleType.SCALE_CENTER_CROP);
        })
        //到最小状态的接口
        .onFinish(dragDiootoView -> MediaPlayerManager.instance().releasePlayerAndView(context))
        .start();
```

长图/gif 等图片处理方案来自 [sketch](https://github.com/panpf/sketch)


