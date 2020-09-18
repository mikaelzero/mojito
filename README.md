# mojito

## Demo Download

<img src="https://cdn.nlark.com/yuque/0/2020/png/252337/1592720888032-assets/web-upload/30908db5-767a-49f6-9564-5b8264b07c14.png" width="180">

## Video Preview

[SAMPLE VIDEO LINK](https://www.bilibili.com/video/BV1Df4y1y7Hq)


## GIf Preview


<img src="https://cdn.nlark.com/yuque/0/2020/gif/252337/1592714120562-assets/web-upload/e39ca518-7035-429c-8489-e60d4775f5ea.gif" width="200"><img src="https://cdn.nlark.com/yuque/0/2020/gif/252337/1592714117035-assets/web-upload/5ca1cfd8-9e31-401a-8c3f-1f8f7b535470.gif" width="200"><img src="https://cdn.nlark.com/yuque/0/2020/gif/252337/1592714121590-assets/web-upload/693c4211-c6af-4fd6-98b7-4762401d32b9.gif" width="200">

</br>

<img src="https://cdn.nlark.com/yuque/0/2020/gif/252337/1592714113185-assets/web-upload/91658d67-be7a-491a-b841-dfbf0beb41d6.gif" width="200"><img src="https://cdn.nlark.com/yuque/0/2020/gif/252337/1592714121774-assets/web-upload/907c13e3-277b-4700-9e97-2bf33a1679eb.gif" width="200"><img src="https://cdn.nlark.com/yuque/0/2020/gif/252337/1592714119732-assets/web-upload/5667bf15-7f35-42b5-8ac3-1fa60fa1a2f3.gif" width="200">


# Getting started

------

## Add the dependencies

```gradle
allprojects {
    repositories {
        maven { url 'https://dl.bintray.com/mikaelzero/maven'}
    }
}

VERSION: 1.5.2

implementation 'net.mikaelzero.mojito:core:VERSION'

//support long image and gif with Sketch
implementation 'net.mikaelzero.mojito:SketchImageFactory:VERSION'

//load with fresco
implementation 'net.mikaelzero.mojito:FrescoImageLoader:VERSION'
//load with glide
implementation 'net.mikaelzero.mojito:GlideImageLoader:VERSION'

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

## Video View

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