package net.mikaelzero.mojito

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.gyf.barlibrary.ImmersionBar
import kotlinx.android.synthetic.main.activity_image.*
import net.mikaelzero.mojito.bean.ContentViewOriginModel
import net.mikaelzero.mojito.bean.ConfigBean
import net.mikaelzero.mojito.bean.ViewPagerBean

class ImageActivity : AppCompatActivity() {
    private var contentViewOriginModels: List<ContentViewOriginModel>? = null
    private lateinit var configBean: ConfigBean
    private lateinit var imageViewPagerAdapter: FragmentPagerAdapter
    val fragmentMap = hashMapOf<Int, ImageFragment?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        ImmersionBar.with(this).fullScreen(true).init()
        setContentView(R.layout.activity_image)
        configBean = intent.getParcelableExtra("config")!!
        val currentPosition = configBean.position ?: 0
        contentViewOriginModels = configBean.contentViewOriginModels
        if (contentViewOriginModels == null) {
            finish()
        }
        val viewPagerBeans = mutableListOf<ViewPagerBean>()
        for (i in contentViewOriginModels!!.indices) {
            viewPagerBeans.add(
                ViewPagerBean(
                    configBean.originImageUrls!![i],
                    configBean.targetImageUrls?.get(i), i,
                    contentViewOriginModels!!.size > 1 || configBean.position != i,
                    contentViewOriginModels!![i]
                )
            )
        }
        imageViewPagerAdapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                val fragment = fragmentMap[position]
                return if (fragment == null) {
                    val imageFragment = ImageFragment.newInstance(
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
            (imageViewPagerAdapter.getItem(viewPager.currentItem) as ImageFragment).backToMin()
            true
        } else super.onKeyDown(keyCode, event)
    }

    companion object {
        fun startImageActivity(activity: Activity?, configBean: ConfigBean?) {
            Mojito.showImmediatelyFlag = true
            val intent = Intent(activity, ImageActivity::class.java)
            intent.putExtra("config", configBean)
            activity?.startActivity(intent)
            activity?.overridePendingTransition(0, 0)
        }
    }
}