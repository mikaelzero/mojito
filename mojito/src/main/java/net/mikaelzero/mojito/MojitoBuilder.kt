package net.mikaelzero.mojito

import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import net.mikaelzero.mojito.bean.ActivityConfig
import net.mikaelzero.mojito.bean.ViewParams
import net.mikaelzero.mojito.interfaces.*
import net.mikaelzero.mojito.loader.FragmentCoverLoader
import net.mikaelzero.mojito.loader.InstanceLoader
import net.mikaelzero.mojito.loader.MultiContentLoader
import net.mikaelzero.mojito.ui.ImageMojitoActivity

/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/6/23 9:46 AM
 * @Description:
 */
class MojitoBuilder {
    private var originImageUrls: List<String>? = null
    private var targetImageUrls: List<String>? = null
    private var errorDrawableResIdList = hashMapOf<Int, Int>()
    private var viewParams: List<ViewParams>? = null
    private var position: Int = 0
    private var headerSize: Int = 0
    private var footerSize: Int = 0
    private var autoLoadTarget: Boolean = true

    fun originImageUrls(data: List<String>) = apply {
        this.originImageUrls = data
    }

    fun targetImageUrls(data: List<String>) = apply {
        this.targetImageUrls = data
    }

    fun viewParams(data: List<ViewParams>) = apply {
        this.viewParams = data
    }

    fun position(data: Int) = apply {
        this.position = data
    }

    fun headerSize(data: Int) = apply {
        this.headerSize = data
    }

    fun footerSize(data: Int) = apply {
        this.footerSize = data
    }

    fun autoLoadTarget(data: Boolean) = apply {
        this.autoLoadTarget = data
    }

    fun errorDrawableResId(pos: Int, res: Int) = apply {
        errorDrawableResIdList[pos] = res;

    }

    fun urls(imageUrl: String) = apply {
        this.originImageUrls = listOf(imageUrl)
    }

    fun urls(imageUrl: String, targetUrl: String) = apply {
        this.originImageUrls = listOf(imageUrl)
        this.targetImageUrls = listOf(targetUrl)
    }


    fun urls(imageUrls: List<String>?) = apply {
        this.originImageUrls = imageUrls
    }

    fun urls(imageUrls: List<String>?, targetImageUrls: List<String>?) = apply {
        this.originImageUrls = imageUrls
        this.targetImageUrls = targetImageUrls
    }

    fun position(position: Int, headerSize: Int = 0, footerSize: Int = 0) = apply {
        this.headerSize = headerSize
        this.footerSize = footerSize
        this.position = position
    }

    fun views(view: View?) = apply {
        val views = arrayOfNulls<View>(1)
        views[0] = view
        views(views)
    }

    fun views(views: Array<View?>) = apply {
        val list = mutableListOf<ViewParams>()
        for (imageView in views) {
            val imageBean = ViewParams()
            if (imageView == null) {
                imageBean.left = 0
                imageBean.top = 0
                imageBean.width = 0
                imageBean.height = 0
            } else {
                val location = IntArray(2)
                imageView.getLocationOnScreen(location)
                imageBean.left = location[0]
                imageBean.top = location[1]
                imageBean.width = imageView.width
                imageBean.height = imageView.height
            }
            list.add(imageBean)
        }
        this.viewParams = list
    }

    fun views(recyclerView: RecyclerView, @IdRes viewId: Int) = apply {
        val originImageViewList = mutableListOf<View?>()
        val childCount = recyclerView.childCount
        for (i in 0 until childCount) {
            val originImage = recyclerView.getChildAt(i).findViewById<View>(viewId)
            if (originImage != null) {
                originImageViewList.add(originImage)
            }
        }
        val layoutManager = recyclerView.layoutManager
        var firstPos = 0
        var lastPos = 0
        val totalCount = layoutManager!!.itemCount - this.headerSize - this.footerSize
        when (layoutManager) {
            is GridLayoutManager -> {
                firstPos = layoutManager.findFirstVisibleItemPosition()
                lastPos = layoutManager.findLastVisibleItemPosition()

            }
            is LinearLayoutManager -> {
                firstPos = layoutManager.findFirstVisibleItemPosition()
                lastPos = layoutManager.findLastVisibleItemPosition()
            }
            is StaggeredGridLayoutManager -> {
                val lastVisibleItemPositions = layoutManager.findLastVisibleItemPositions(null)
                val firstVisibleItemPositions = layoutManager.findFirstVisibleItemPositions(null)
                lastPos = getLastVisibleItem(lastVisibleItemPositions)
                firstPos = getFirstVisibleItem(firstVisibleItemPositions)
            }
        }
        firstPos = if (firstPos < this.headerSize) 0 else firstPos - this.headerSize
        lastPos = if (lastPos > totalCount) totalCount - 1 else lastPos - this.headerSize
        fillPlaceHolder(originImageViewList, totalCount, firstPos, lastPos)
        val views = arrayOfNulls<View>(originImageViewList.size)
        for (i in originImageViewList.indices) {
            views[i] = originImageViewList[i]
        }
        views(views)
    }

    /**
     * @return Last visible item position for staggeredGridLayoutManager
     */
    private fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
        var maxSize = 0
        for (position in lastVisibleItemPositions) {
            if (position > maxSize) {
                maxSize = position
            }
        }
        return maxSize
    }

    /**
     * @return First visible item position for staggeredGridLayoutManager
     */
    private fun getFirstVisibleItem(firstVisibleItemPositions: IntArray): Int {
        var minSize = 0
        if (firstVisibleItemPositions.isNotEmpty()) {
            minSize = firstVisibleItemPositions[0]
            for (position in firstVisibleItemPositions) {
                if (position < minSize) {
                    minSize = position
                }
            }
        }
        return minSize
    }

    /**
     * fill recycleView
     */
    private fun fillPlaceHolder(originImageList: MutableList<View?>, totalCount: Int, firstPos: Int, lastPos: Int) {
        if (firstPos > 0) {
            for (pos in firstPos downTo 1) {
                originImageList.add(0, null)
            }
        }
        if (lastPos < totalCount) {
            for (i in totalCount - 1 - lastPos downTo 1) {
                originImageList.add(null)
            }
        }
    }


    inline fun mojitoListener(
        crossinline onStartAnim: (position: Int) -> Unit = {},
        crossinline onClick: (view: View, x: Float, y: Float, position: Int) -> Unit = { _, _, _, _ -> },
        crossinline onLongClick: (fragmentActivity: FragmentActivity?, view: View, x: Float, y: Float, position: Int) -> Unit = { _, _, _, _, _ -> },
        crossinline onShowFinish: (mojitoView: MojitoView, showImmediately: Boolean) -> Unit = { _, _ -> },
        crossinline onMojitoViewFinish: (pagePosition: Int) -> Unit = {},
        crossinline onDrag: (view: MojitoView, moveX: Float, moveY: Float) -> Unit = { _, _, _ -> },
        crossinline onLongImageMove: (ratio: Float) -> Unit = {},
        crossinline onViewPageSelected: (position: Int) -> Unit = {},
    ) = setOnMojitoListener(object : OnMojitoListener {
        override fun onStartAnim(position: Int) = onStartAnim(position)

        override fun onClick(view: View, x: Float, y: Float, position: Int) = onClick(view, x, y, position)

        override fun onLongClick(fragmentActivity: FragmentActivity?, view: View, x: Float, y: Float, position: Int) = onLongClick(fragmentActivity, view, x, y, position)

        override fun onShowFinish(mojitoView: MojitoView, showImmediately: Boolean) = onShowFinish(mojitoView, showImmediately)

        override fun onMojitoViewFinish(pagePosition: Int) = onMojitoViewFinish(pagePosition)

        override fun onDrag(view: MojitoView, moveX: Float, moveY: Float) = onDrag(view, moveX, moveY)

        override fun onLongImageMove(ratio: Float) = onLongImageMove(ratio)

        override fun onViewPageSelected(position: Int) = onViewPageSelected(position)

    })

    fun setOnMojitoListener(target: OnMojitoListener?) = apply {
        ImageMojitoActivity.onMojitoListener = target
    }

    inline fun progressLoader(
        crossinline providerInstance: () -> IProgress
    ) = setProgressLoader(object : InstanceLoader<IProgress> {
        override fun providerInstance(): IProgress = providerInstance()
    })


    fun setProgressLoader(loader: InstanceLoader<IProgress>) = apply {
        ImageMojitoActivity.progressLoader = loader
    }

    inline fun fragmentCoverLoader(
        crossinline providerInstance: () -> FragmentCoverLoader
    ) = setFragmentCoverLoader(object : InstanceLoader<FragmentCoverLoader> {
        override fun providerInstance(): FragmentCoverLoader = providerInstance()
    })

    fun setFragmentCoverLoader(loader: InstanceLoader<FragmentCoverLoader>) = apply {
        ImageMojitoActivity.fragmentCoverLoader = loader
    }

    inline fun multiContentLoader(
        crossinline providerLoader: (position: Int) -> ImageViewLoadFactory,
        crossinline providerEnableTargetLoad: (position: Int) -> Boolean,
    ) = setMultiContentLoader(object : MultiContentLoader {
        override fun providerLoader(position: Int): ImageViewLoadFactory = providerLoader(position)

        override fun providerEnableTargetLoad(position: Int): Boolean = providerEnableTargetLoad(position)
    })

    fun setMultiContentLoader(loader: MultiContentLoader) = apply {
        ImageMojitoActivity.multiContentLoader = loader

    }

    fun setActivityCoverLoader(on: ActivityCoverLoader) = apply {
        ImageMojitoActivity.activityCoverLoader = on

    }

    fun setIndicator(on: IIndicator?) = apply {
        ImageMojitoActivity.iIndicator = on
    }

    fun build(): ActivityConfig {
        return ActivityConfig(
            originImageUrls = this.originImageUrls,
            targetImageUrls = this.targetImageUrls,
            viewParams = this.viewParams,
            position = this.position,
            headerSize = this.headerSize,
            footerSize = this.footerSize,
            autoLoadTarget = this.autoLoadTarget,
            errorDrawableResIdList = this.errorDrawableResIdList
        )
    }
}