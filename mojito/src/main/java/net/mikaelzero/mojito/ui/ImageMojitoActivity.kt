package net.mikaelzero.mojito.ui

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.gyf.immersionbar.ImmersionBar
import net.mikaelzero.mojito.Mojito
import net.mikaelzero.mojito.bean.ActivityConfig
import net.mikaelzero.mojito.bean.FragmentConfig
import net.mikaelzero.mojito.bean.ViewPagerBean
import net.mikaelzero.mojito.bean.ViewParams
import net.mikaelzero.mojito.databinding.ActivityImageBinding
import net.mikaelzero.mojito.interfaces.*
import net.mikaelzero.mojito.loader.FragmentCoverLoader
import net.mikaelzero.mojito.loader.InstanceLoader
import net.mikaelzero.mojito.loader.MultiContentLoader
import net.mikaelzero.mojito.tools.DataWrapUtil
import java.lang.ref.WeakReference


class ImageMojitoActivity : AppCompatActivity(), IMojitoActivity {
    private lateinit var binding: ActivityImageBinding
    private var viewParams: List<ViewParams>? = null
    lateinit var activityConfig: ActivityConfig
    private lateinit var imageViewPagerAdapter: FragmentPagerAdapter
    val fragmentMap = hashMapOf<Int, ImageMojitoFragment?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        if (Mojito.mojitoConfig().transparentNavigationBar()) {
            ImmersionBar.with(this).transparentBar().init()
        } else {
            ImmersionBar.with(this).transparentStatusBar().init()
        }
        binding = ActivityImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.userCustomLayout.removeAllViews()
        activityCoverLoader?.apply {
            attach(this@ImageMojitoActivity)
            binding.userCustomLayout.addView(providerView())
        }

        if (DataWrapUtil.config == null) {
            finish()
            return
        }
        activityConfig = DataWrapUtil.get()!!
        val currentPosition = activityConfig.position
        viewParams = activityConfig.viewParams

        val viewPagerBeans = mutableListOf<ViewPagerBean>()
        if (activityConfig.originImageUrls == null) {
            finish()
            return
        }
        for (i in activityConfig.originImageUrls!!.indices) {
            var targetImageUrl: String? = null
            if (activityConfig.targetImageUrls != null) {
                if (i < activityConfig.targetImageUrls!!.size) {
                    targetImageUrl = activityConfig.targetImageUrls!![i]
                }
            }

            val model = when {
                viewParams == null -> {
                    null
                }
                i >= viewParams!!.size -> {
                    null
                }
                else -> {
                    viewParams?.get(i)
                }
            }
            viewPagerBeans.add(
                ViewPagerBean(
                    activityConfig.originImageUrls!![i],
                    targetImageUrl, i,
                    currentPosition != i,
                    model
                )
            )
        }
        imageViewPagerAdapter = object : FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            override fun getItem(position: Int): Fragment {
                val fragment = fragmentMap[position]
                return if (fragment == null) {
                    val fragmentConfig = FragmentConfig(
                        viewPagerBeans[position].url,
                        viewPagerBeans[position].targetUrl,
                        viewPagerBeans[position].viewParams,
                        position,
                        activityConfig.autoLoadTarget,
                        viewPagerBeans[position].showImmediately,
                        if (activityConfig.errorDrawableResIdList[position] != null) {
                            activityConfig.errorDrawableResIdList[position]!!
                        } else {
                            0
                        }
                    )
                    val imageFragment = ImageMojitoFragment.newInstance(fragmentConfig)
                    fragmentMap[position] = imageFragment
                    imageFragment
                } else {
                    fragment
                }
            }

            override fun getCount(): Int = viewPagerBeans.size
        }
        binding.viewPager.adapter = imageViewPagerAdapter
        binding.viewPager.setCurrentItem(currentPosition, false)

        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                activityCoverLoader?.pageChange(viewPagerBeans.size, position)
                onMojitoListener?.onViewPageSelected(position)
            }
        })
        activityCoverLoader?.pageChange(viewPagerBeans.size, activityConfig.position)
        if (!activityConfig.originImageUrls.isNullOrEmpty()) {
            iIndicator?.attach(binding.indicatorLayout)
            iIndicator?.onShow(binding.viewPager)
        }
        Mojito.currentActivity = WeakReference<ImageMojitoActivity>(this)
    }

    fun setViewPagerLock(isLock: Boolean) {
        binding.viewPager.isLocked = isLock
    }

    fun finishView() {
        progressLoader = null
        fragmentCoverLoader = null
        multiContentLoader = null
        iIndicator = null
        activityCoverLoader = null
        onMojitoListener = null
        viewParams = null
        fragmentMap.clear()
        Mojito.clean()
        finish()
        overridePendingTransition(0, 0)
    }

    fun backToMin() {
        (imageViewPagerAdapter.getItem(binding.viewPager.currentItem) as ImageMojitoFragment).backToMin()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            (imageViewPagerAdapter.getItem(binding.viewPager.currentItem) as ImageMojitoFragment).backToMin()
            true
        } else super.onKeyDown(keyCode, event)
    }

    companion object {
        var hasShowedAnimMap = hashMapOf<Int, Boolean>()
        var progressLoader: InstanceLoader<IProgress>? = null
        var fragmentCoverLoader: InstanceLoader<FragmentCoverLoader>? = null
        var multiContentLoader: MultiContentLoader? = null
        var iIndicator: IIndicator? = null
        var activityCoverLoader: ActivityCoverLoader? = null
        var onMojitoListener: OnMojitoListener? = null
    }

    override fun getCurrentFragment(): IMojitoFragment? {
        return imageViewPagerAdapter.getItem(binding.viewPager.currentItem) as IMojitoFragment?
    }

    override fun getContext(): Context {
        return this
    }
}