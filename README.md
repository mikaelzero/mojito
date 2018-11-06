# DragDiooto
微信图库效果,微信视频拖放效果


<img src="https://github.com/moyokoo/Media/blob/master/diooto1.gif?raw=true" height="500"/><img src="https://github.com/moyokoo/Media/blob/master/diooto2.gif?raw=true" height="500"/><img src="https://github.com/moyokoo/Media/blob/master/diooto3.gif?raw=true" height="500"/>

- 适配长图
- 适配GIF
- 适配视频

```java
dragDiooto = new DragDiooto(context)
        //图片地址
        .urls(normalImageUlr)
        //类型
        .type(DragDiooto.PHOTO)
        //点击位置
        .position(holder.getAdapterPosition())
        //点击的所有View
        .views(views)
        .loadPhotoBeforeShowBigImage(new DragDiooto.OnLoadPhotoBeforeShowBigImage() {
            @Override
            public void loadView(SketchImageView sketchImageView, int position) {
                sketchImageView.displayImage(normalImageUlr[holder.getAdapterPosition()]);
            }
        })
        .start();
```

```java
dragDiooto = new DragDiooto(context)
        .urls(normalImageUlr[position])
        .position(holder.getAdapterPosition())
        .views(holder.srcImageView)
        .type(DragDiooto.VIDEO)
        //提供视频View
        .onProvideVideoView(new DragDiooto.OnProvideVideoView() {
            @Override
            public View provideView() {
                return new VideoView(context);
            }
        })
        //显示视频加载之前的缩略图
        .loadPhotoBeforeShowBigImage(new DragDiooto.OnLoadPhotoBeforeShowBigImage() {
            @Override
            public void loadView(SketchImageView sketchImageView, int position) {
                sketchImageView.displayImage(normalImageUlr[holder.getAdapterPosition()]);
            }
        })
        //动画到最大化时的接口
        .onVideoLoadEnd(new DragDiooto.OnShowToMaxFinish() {
            @Override
            public void onShowToMax(DragDiootoView dragDiootoView) {
                VideoView videoView = (VideoView) dragDiootoView.getContentView();
                videoView.setControlPanel(new ControlPanel(context));
                videoView.setUp("http://bmob-cdn-982.b0.upaiyun.com/2017/02/23/266454624066f2b680707492a0664a97.mp4");
                videoView.start();
                //更新视频大小
                dragDiootoView.notifySize(1920, 1080);
                MediaPlayerManager.instance().setScreenScale(ScaleType.SCALE_CENTER_CROP);
            }
        })
        //到最小状态的接口
        .onFinish(new DragDiooto.OnFinish() {
            @Override
            public void finish(DragDiootoView dragDiootoView) {
                MediaPlayerManager.instance().releasePlayerAndView(context);
            }
        })
        .start();
```
