# DragDiooto 

[![](https://jitpack.io/v/moyokoo/Diooto.svg)](https://jitpack.io/#moyokoo/Diooto)

> Weibo, WeChat gallery effect, WeChat video drag and drop effect, adapt status bar, screen rotation, full screen, long picture, GIF, video


<img src="https://github.com/moyokoo/Media/blob/master/diooto1.gif?raw=true" height="500"/><img src="https://github.com/moyokoo/Media/blob/master/diooto2.gif?raw=true" height="500"/><img src="https://github.com/moyokoo/Media/blob/master/diooto3.gif?raw=true" height="500"/>

- Automatically update image size
- Customizable LoadingView
- Customizable Indicator
- Adapt status bar
- Adapt screen rotation
- Adapt to full screen
- Adapt to long map
- Adapt to GIF
- Adapter video

##### Usage

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
//Picture mode
Diooto diooto = new Diooto(context)
        .urls(normalImageUlr)
        //Picture or video
        .type(DiootoConfig.PHOTO)
        //position of click ,if you have headerView，you should call .position(holder.getAdapterPosition(),headSize) headSize : headView size
        .position(holder.getAdapterPosition())
        //use recylcerview automatic recognition(need viewId of item layout)  use view array by yourself
        .views(mRecyclerView,R.id.srcImageView)
        //set selector CircleIndexIndicator of default  implement IIndicator inteerface customize
        .setIndicator(new CircleIndexIndicator())
        //set progress style  DefaultProgress of default  implement IProgress inteerface customize
        .setProgress(new DefaultProgress())
        //show image before load origin image  if you use Glide load imageview at recyclerview,you should use Glide here
        .loadPhotoBeforeShowBigImage((sketchImageView, position12) -> sketchImageView.displayImage(normalImageUlr[holder.getAdapterPosition()]))
        .start();
```

##### video not provide automatic recognition size ,you should do it by yourself

For a better experience,you should hide image after video has prepared,Like this:

```java
simpleControlPanel.setOnVideoPreparedListener(() -> {
        sketchImageView.setVisibility(View.GONE);
        progressView.setVisibility(View.GONE);
```

another way is hide at callback `onVideoLoadEnd`
PS:you should hide image and progress by yourself

```java
//Video Mode
Diooto diooto = new Diooto(context)
        .urls(normalImageUlr[position])
        .position(holder.getAdapterPosition())
        .views(holder.srcImageView)
        .type(DiootoConfig.VIDEO)
        //provide video view ,only object
        .onProvideVideoView(() -> new VideoView(context))
        //show thumbnail before video prepared
        .loadPhotoBeforeShowBigImage((sketchImageView, position13) -> sketchImageView.displayImage(normalImageUlr[holder.getAdapterPosition()]))
        //callback of animator to max,hide ui here
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
        //callback of min state
        .onFinish(dragDiootoView -> MediaPlayerManager.instance().releasePlayerAndView(context))
        .start();
```

long image/gif from [sketch](https://github.com/panpf/sketch)


