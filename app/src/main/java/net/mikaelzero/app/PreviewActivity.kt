package net.mikaelzero.app

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_display.*
import net.mikaelzero.mojito.Mojito
import net.mikaelzero.mojito.MojitoView
import net.mikaelzero.mojito.impl.DefaultPercentProgress
import net.mikaelzero.mojito.impl.NumIndicator
import net.mikaelzero.mojito.impl.SimpleMojitoViewCallback
import net.mikaelzero.mojito.interfaces.IProgress
import net.mikaelzero.mojito.loader.InstanceLoader

class PreviewActivity : AppCompatActivity() {
    var context: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        setContentView(R.layout.activity_display)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        val adapter = ImageAdapter()
        adapter.setList(SourceUtil.getNormalImages())
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener { _, view, position ->
            Mojito.with(context)
                .urls(SourceUtil.getNormalImages())
                .position(position, headerSize = 1, footerSize = 1)
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

                    override fun onStartAnim(position: Int) {

                    }

                    override fun onShowFinish(mojitoView: MojitoView, showImmediately: Boolean) {
//                        recyclerView.getChildAt(position + 1).findViewById<View?>(R.id.srcImageView)?.visibility = View.GONE
                    }

                    override fun onMojitoViewFinish(pagePosition: Int) {
//                        for (x in 0 until recyclerView.childCount) {
//                            recyclerView.getChildAt(x).findViewById<View?>(R.id.srcImageView)?.visibility = View.VISIBLE
//                        }
                    }

                    override fun onViewPageSelected(position: Int) {
//                        for (x in 0 until recyclerView.childCount) {
//                            if ((position + 1) == x) {
//                                recyclerView.getChildAt(x).findViewById<View?>(R.id.srcImageView)?.visibility = View.GONE
//                            } else {
//                                recyclerView.getChildAt(x).findViewById<View?>(R.id.srcImageView)?.visibility = View.VISIBLE
//                            }
//                        }
                    }
                })
                .setIndicator(NumIndicator())
                .start()
        }
        adapter.addHeaderView(LayoutInflater.from(this).inflate(R.layout.header_layout, null))
        adapter.addFooterView(LayoutInflater.from(this).inflate(R.layout.header_layout, null))

        singleIv.addImg(SourceUtil.getSingleImage())
        longHorIv.addImg(SourceUtil.getLongHorImage())

        singleIv.setOnClickListener {
            Mojito.with(context)
                .urls(SourceUtil.getSingleImage())
                .views(singleIv)
                .setOnMojitoListener(object : SimpleMojitoViewCallback() {
                    override fun onLongClick(fragmentActivity: FragmentActivity?, view: View, x: Float, y: Float, position: Int) {
                        Toast.makeText(fragmentActivity, "long click", Toast.LENGTH_SHORT).show()
                    }
                })
                .start()
        }
        longHorIv.setOnClickListener {
            Mojito.with(context)
                .urls(SourceUtil.getLongHorImage())
                .views(longHorIv)
                .start()
        }

        noViewBtn.setOnClickListener {
            Mojito.with(context)
                .urls(SourceUtil.getSingleImage())
                .start()
        }
        noViewViewPagerBtn.setOnClickListener {
            Mojito.with(context)
                .urls(SourceUtil.getNormalImages())
                .setIndicator(NumIndicator())
                .start()
        }
    }
}