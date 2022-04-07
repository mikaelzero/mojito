# mojito   ![Language](https://img.shields.io/badge/language-java-orange.svg) ![Language](https://img.shields.io/badge/language-kotlin-orange.svg) [![](https://jitpack.io/v/MikaelZero/mojito.svg)](https://jitpack.io/#MikaelZero/mojito)

[English](https://github.com/MikaelZero/mojito/blob/master/README_en.md)

### 由于工作繁忙，关于该项目，暂时不做维护，如果有大佬想要完善，非常提倡大佬们能够提pull requests，有时间我会进行合并；该项目整体的架构设计还算ok，各位需要想要进行开发或者拓展也是非常方便的。

## 功能列表

- 支持Coil图片加载器
- 支持Glide图片加载器
- 支持Fresco图片加载器
- 支持视频图片混合、GIF、图片预览
- 支持拖拽关闭
- 支持自定义页面索引指示器、进度条、Cover
- 支持原图加载策略

## 二维码下载体验

<img src="https://cdn.nlark.com/yuque/0/2020/png/252337/1592720888032-assets/web-upload/30908db5-767a-49f6-9564-5b8264b07c14.png" width="180">

## 通过视频查看效果

[SAMPLE VIDEO LINK](https://www.bilibili.com/video/BV1Df4y1y7Hq)

## 动图效果

<img src="https://github.com/MikaelZero/Media/blob/master/mojito_gif_1.gif?raw=true" width="200"><img src="https://github.com/MikaelZero/Media/blob/master/mojito_gif_2.gif?raw=true" width="200"><img src="https://github.com/MikaelZero/Media/blob/master/mojito_gif_3.gif?raw=true" width="200">
<img src="https://github.com/MikaelZero/Media/blob/master/mojito_gif_4.gif?raw=true" width="200"><img src="https://github.com/MikaelZero/Media/blob/master/mojito_gif_5.gif?raw=true" width="200"><img src="https://github.com/MikaelZero/Media/blob/master/mojito_gif_6.gif?raw=true" width="200">

# 开始

------

## 添加 dependencies

```gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

implementation "com.github.mikaelzero.mojito:mojito:$mojito_version"
//support long image and gif with Sketch
implementation "com.github.mikaelzero.mojito:SketchImageViewLoader:$mojito_version"

//load with coil
implementation "com.github.mikaelzero.mojito:coilimageloader:$mojito_version"
//load with glide
implementation "com.github.mikaelzero.mojito:GlideImageLoader:$mojito_version"
//load with fresco
implementation "com.github.mikaelzero.mojito:FrescoImageLoader:$mojito_version"
```

## 初始化

```kotlin
// in your application
Mojito.initialize(
    GlideImageLoader.with(this),
    SketchImageLoadFactory()
)

//or

//YourMojitoConfig:IMojitoConfig
Mojito.initialize(
    GlideImageLoader.with(this),
    SketchImageLoadFactory(),
    YourMojitoConfig()
)
```

## 开始使用

```kotlin
Mojito.with(context)
    .urls(SourceUtil.getSingleImage())
    .views(singleIv)
    .start()
```

# 使用

## RecyclerView

```kotlin
binding.recyclerView.mojito(R.id.srcImageView) {
    urls(SourceUtil.getNormalImages())
    position(position)
    mojitoListener(
        onClick = { view, x, y, pos ->
            Toast.makeText(context, "tap click", Toast.LENGTH_SHORT).show()
        }
    )
    progressLoader {
        DefaultPercentProgress()
    }
    setIndicator(NumIndicator())
}
```

## 单个 View

```kotlin
binding.longHorIv.mojito(SourceUtil.getLongHorImage())
```

## 无 View

```kotlin
 Mojito.start(context) {
    urls(SourceUtil.getNormalImages())
}
```

## 视频 View or 视频/图片 混合View

```kotlin
Mojito.start(context) {
    urls(SourceUtil.getVideoImages(), SourceUtil.getVideoTargetImages())
    setMultiTargetEnableLoader(object : MultiTargetEnableLoader {
        override fun providerEnable(position: Int): Boolean {
            return position != 1
        }
    })
    setMultiContentLoader(object : MultiContentLoader {
        override fun providerLoader(position: Int): ImageViewLoadFactory {
            return if (position == 1) {
                ArtLoadFactory()
            } else {
                SketchImageLoadFactory()
            }
        }
    })
    position(position)
    views(recyclerView, R.id.srcImageView)
}
```

## Callback回调

```kotlin
 abstract class SimpleMojitoViewCallback : OnMojitoListener {
    // image click
    override fun onClick(view: View, x: Float, y: Float, position: Int) {

    }

    //image long press
    override fun onLongClick(fragmentActivity: FragmentActivity?, view: View, x: Float, y: Float, position: Int) {
    }

    //end of min image to max image 
    override fun onShowFinish(mojitoView: MojitoView, showImmediately: Boolean) {
    }

    //activity finish,backToMin,single click
    override fun onMojitoViewFinish() {
    }

    //when you drag your image 
    override fun onDrag(view: MojitoView, moveX: Float, moveY: Float) {
    }

    //the ratio of long image when you scroll
    override fun onLongImageMove(ratio: Float) {
    }
}
```

## API

| Name  | desc |
| :-----| :----: |
| url(src,target) |设置缩略图和原图url数据|
| position | 点击的位置 |
| views|  1. recylclerView,imageViewId <br> 2. single view <br> 3. multi views|
| autoLoadTarget |  默认为true，如果你设置了原图的url并且设置了autoLoadTarget(false)<br>你需要使用setFragmentCoverLoader来自定义view|
| setProgressLoader|  当你设置了 autoLoadTarget false 才会生效|
| setIndicator | 可以选择 NumIndicator 或者 CircleIndexIndicator|
| setActivityCoverLoader |  自定义Activity的覆盖层view|
|setMultiContentLoader | 如果使用视频和图片混合模式，需要设置 ImageViewLoadFactory|

## Thanks

[sketch](https://github.com/panpf/sketch)

[BigImageViewer](https://github.com/Piasy/BigImageViewer)
