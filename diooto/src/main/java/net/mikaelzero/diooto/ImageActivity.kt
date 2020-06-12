package net.mikaelzero.diooto

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.gyf.barlibrary.ImmersionBar
import kotlinx.android.synthetic.main.activity_image.*
import net.mikaelzero.diooto.config.ContentViewOriginModel
import net.mikaelzero.diooto.config.DiootoConfig
import net.mikaelzero.diooto.interfaces.IIndicator
import net.mikaelzero.diooto.interfaces.IProgress

class ImageActivity : AppCompatActivity() {
    private lateinit var contentViewOriginModels: List<ContentViewOriginModel>
    private lateinit var diootoConfig: DiootoConfig
    private lateinit var imageViewPagerAdapter: ImageViewPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        ImmersionBar.with(this).fullScreen(true).init()
        setContentView(R.layout.activity_image)
        diootoConfig = intent.getParcelableExtra("config")!!
        indicatorLayout.visibility = diootoConfig.indicatorVisibility
        val currentPosition = diootoConfig.position
        contentViewOriginModels = diootoConfig.contentViewOriginModels
        val viewPagerBeans = mutableListOf<ViewPagerBean>()
        imageViewPagerAdapter = ImageViewPagerAdapter(viewPagerBeans, this)
        for (i in contentViewOriginModels.indices) {
            viewPagerBeans.add(
                ViewPagerBean(
                    diootoConfig.imageUrls[i], i,
                    contentViewOriginModels.size > 1 || diootoConfig.position != i,
                    contentViewOriginModels[i]
                )
            )
        }
        viewPager.adapter = imageViewPagerAdapter
        viewPager.setCurrentItem(currentPosition, false)
        if (contentViewOriginModels.size != 1) {
            iIndicator?.attach(indicatorLayout)
//            iIndicator?.onShow(mViewPager);
        }
    }

    fun finishView() {
        if (Mojito.onFinishListener != null) {
//            Diooto.onFinishListener.finish(fragmentList.get(mViewPager.getCurrentItem()).getDragDiootoView());
        }
        Mojito.onLoadPhotoBeforeShowBigImageListener = null
        Mojito.onShowToMaxFinishListener = null
        Mojito.onProvideViewListener = null
        Mojito.onFinishListener = null
        iIndicator = null
        iProgress = null
        finish()
        overridePendingTransition(0, 0)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            imageViewPagerAdapter.getFragment(viewPager.currentItem)?.backToMin()
            true
        } else super.onKeyDown(keyCode, event)
    }

    companion object {
        @JvmField
        var iIndicator: IIndicator? = null

        @JvmField
        var iProgress: IProgress? = null

        @JvmStatic
        fun startImageActivity(activity: Activity?, diootoConfig: DiootoConfig?) {
            Mojito.showImmediatelyFlag = true
            val intent = Intent(activity, ImageActivity::class.java)
            intent.putExtra("config", diootoConfig)
            activity?.startActivity(intent)
            activity?.overridePendingTransition(0, 0)
        }
    }
}