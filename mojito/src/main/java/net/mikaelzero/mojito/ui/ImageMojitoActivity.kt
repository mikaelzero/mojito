package net.mikaelzero.mojito.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.activity_image.*
import net.mikaelzero.mojito.Mojito
import net.mikaelzero.mojito.R
import net.mikaelzero.mojito.bean.ContentViewOriginModel
import net.mikaelzero.mojito.bean.ConfigBean
import net.mikaelzero.mojito.bean.ViewPagerBean
import net.mikaelzero.mojito.interfaces.IMojitoActivity
import net.mikaelzero.mojito.interfaces.IMojitoFragment

class ImageMojitoActivity : AppCompatActivity(), IMojitoActivity {
    private var contentViewOriginModels: List<ContentViewOriginModel>? = null
    private lateinit var configBean: ConfigBean
    private lateinit var imageViewPagerAdapter: FragmentPagerAdapter
    val fragmentMap = hashMapOf<Int, ImageMojitoFragment?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        ImmersionBar.with(this).fullScreen(true).init()
        setContentView(R.layout.activity_image)

        userCustomLayout.removeAllViews()
        Mojito.coverLayoutLoader?.let {
            Mojito.coverLayoutLoader?.attach(this)
            userCustomLayout.addView(Mojito.coverLayoutLoader!!.providerView())
        }

        configBean = intent.getParcelableExtra("config")!!
        val currentPosition = configBean.position ?: 0
        contentViewOriginModels = configBean.contentViewOriginModels
        if (contentViewOriginModels == null) {
            finish()
        }
        val viewPagerBeans = mutableListOf<ViewPagerBean>()
        for (i in contentViewOriginModels!!.indices) {
            var targetImageUrl: String? = null
            if (configBean.targetImageUrls != null) {
                if (i < configBean.targetImageUrls!!.size) {
                    targetImageUrl = configBean.targetImageUrls!![i]
                }
            }
            viewPagerBeans.add(
                ViewPagerBean(
                    configBean.originImageUrls!![i],
                    targetImageUrl, i,
                    contentViewOriginModels!!.size > 1 || configBean.position != i,
                    contentViewOriginModels!![i]
                )
            )
        }
        imageViewPagerAdapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                val fragment = fragmentMap[position]
                return if (fragment == null) {
                    val imageFragment = ImageMojitoFragment.newInstance(
                        viewPagerBeans[position].url,
                        viewPagerBeans[position].targetUrl,
                        position,
                        viewPagerBeans[position].showImmediately,
                        viewPagerBeans[position].contentViewOriginModel
                    )
                    fragmentMap[position] = imageFragment
                    imageFragment
                } else {
                    fragment
                }
            }

            override fun getCount(): Int = viewPagerBeans.size
        }
        viewPager.adapter = imageViewPagerAdapter
        viewPager.setCurrentItem(currentPosition, false)
        Mojito.coverLayoutLoader?.pageChange(
            imageViewPagerAdapter.getItem(viewPager.currentItem) as IMojitoFragment,
            viewPagerBeans.size, currentPosition
        )
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                Mojito.coverLayoutLoader?.pageChange(
                    imageViewPagerAdapter.getItem(viewPager.currentItem) as IMojitoFragment,
                    viewPagerBeans.size, position
                )
            }

        })
        if (contentViewOriginModels!!.size > 1) {
            Mojito.iIndicator?.attach(indicatorLayout)
            Mojito.iIndicator?.onShow(viewPager)
        }
    }

    fun setViewPagerLock(isLock: Boolean) {
        viewPager.isLocked = isLock
    }

    fun finishView() {
        Mojito.clean()
        finish()
        overridePendingTransition(0, 0)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            (imageViewPagerAdapter.getItem(viewPager.currentItem) as ImageMojitoFragment).backToMin()
            true
        } else super.onKeyDown(keyCode, event)
    }

    companion object {
        var showImmediatelyFlag = true
        fun startImageActivity(activity: Activity?, configBean: ConfigBean?) {
            showImmediatelyFlag = true
            val intent = Intent(activity, ImageMojitoActivity::class.java)
            intent.putExtra("config", configBean)
            activity?.startActivity(intent)
            activity?.overridePendingTransition(0, 0)
        }
    }

    override fun getCurrentFragment(): IMojitoFragment {
        return imageViewPagerAdapter.getItem(viewPager.currentItem) as IMojitoFragment
    }

    override fun getContext(): Context {
        return this
    }
}