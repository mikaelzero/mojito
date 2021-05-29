# mojito   ![Language](https://img.shields.io/badge/language-java-orange.svg) ![Language](https://img.shields.io/badge/language-kotlin-orange.svg) [![](https://jitpack.io/v/MikaelZero/mojito.svg)](https://jitpack.io/#MikaelZero/mojito)

## Demo Download

<img src="https://cdn.nlark.com/yuque/0/2020/png/252337/1592720888032-assets/web-upload/30908db5-767a-49f6-9564-5b8264b07c14.png" width="180">

## Video Preview

[SAMPLE VIDEO LINK](https://www.bilibili.com/video/BV1Df4y1y7Hq)

## GIf Preview


<img src="https://github.com/MikaelZero/Media/blob/master/mojito_gif_1.gif?raw=true" width="200"><img src="https://github.com/MikaelZero/Media/blob/master/mojito_gif_2.gif?raw=true" width="200"><img src="https://github.com/MikaelZero/Media/blob/master/mojito_gif_3.gif?raw=true" width="200">
<img src="https://github.com/MikaelZero/Media/blob/master/mojito_gif_4.gif?raw=true" width="200"><img src="https://github.com/MikaelZero/Media/blob/master/mojito_gif_5.gif?raw=true" width="200"><img src="https://github.com/MikaelZero/Media/blob/master/mojito_gif_6.gif?raw=true" width="200">



# Getting started

------

## Add the dependencies

```gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

implementation "com.github.mikaelzero.mojito:mojito:$mojito_version"
//support long image and gif with Sketch
implementation "com.github.mikaelzero.mojito:SketchImageViewLoader:$mojito_version"

//load with glide
implementation "com.github.mikaelzero.mojito:GlideImageLoader:$mojito_version"
//load with fresco
implementation "com.github.mikaelzero.mojito:FrescoImageLoader:$mojito_version"
```


## Initialize

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

## Start

```kotlin
Mojito.with(context)
    .urls(SourceUtil.getSingleImage())
    .views(singleIv)
    .start()
```

# Usage

## RecyclerView

```kotlin
Mojito.with(context)
    .urls(SourceUtil.getNormalImages())
    .position(position)
    .views(recyclerView, R.id.srcImageView)
    .autoLoadTarget(false)
    .setProgressLoader(object : InstanceLoader<IProgress> {
        override fun providerInstance(): IProgress {
            return DefaultPercentProgress()
        }
    })
    .setOnMojitoListener(object : SimpleMojitoViewCallback() {
        override fun onLongClick(fragmentActivity: FragmentActivity?, view: View, x: Float, y: Float, position: Int) {
            Toast.makeText(context, "long click", Toast.LENGTH_SHORT).show()
        }

        override fun onClick(view: View, x: Float, y: Float, position: Int) {
            Toast.makeText(context, "tap click", Toast.LENGTH_SHORT).show()
            }
        })
    .setIndicator(NumIndicator())
    .start()
```

## Single View

```kotlin
 Mojito.with(context)
    .urls(SourceUtil.getSingleImage())
    .views(singleIv)
    .start()
```

## No View

```kotlin
 Mojito.with(context)
    .urls(SourceUtil.getNormalImages())
    .start()
```

## Video View or Video/Image View

```kotlin
Mojito.with(context)
    .urls(SourceUtil.getVideoImages(), SourceUtil.getVideoTargetImages())
    .setMultiTargetEnableLoader(object : MultiTargetEnableLoader {
        override fun providerEnable(position: Int): Boolean {
            return position != 1
        }
    })
    .setMultiContentLoader(object : MultiContentLoader {
        override fun providerLoader(position: Int): ImageViewLoadFactory {
            return if (position == 1) {
                ArtLoadFactory()
            } else {
                SketchImageLoadFactory()
            }
        }
    })
    .position(position)
    .views(recyclerView, R.id.srcImageView)
    .start()
```

## Callback

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
| url(src,target) | set source urls and  target urls|
| position | position of click |
| views|  1. recylclerView,imageViewId <br> 2. single view <br> 3. multi views|
| autoLoadTarget |  default true,if you set target urls,and set autoLoadTarget(false)<br> you should load target url by yourself <br> and using  setFragmentCoverLoader() to custom click view|
| setProgressLoader|  only work when you set autoLoadTarget false|
| setIndicator | you can choose NumIndicator  or CircleIndexIndicator|
| setActivityCoverLoader |  custom cover layout|
|setMultiContentLoader | if you need both of video and image ,provider different  ImageViewLoadFactory|


## Thanks

[sketch](https://github.com/panpf/sketch)

[BigImageViewer](https://github.com/Piasy/BigImageViewer)