package net.mikaelzero.app.video

import android.content.Context
import android.graphics.RectF
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import coil.load
import com.bumptech.glide.Glide
import net.mikaelzero.app.MainActivity
import net.mikaelzero.app.R
import net.mikaelzero.mojito.interfaces.OnMojitoViewCallback
import net.mikaelzero.mojito.loader.ContentLoader
import net.mikaelzero.mojito.loader.OnLongTapCallback
import net.mikaelzero.mojito.loader.OnTapCallback
import org.salient.artplayer.conduction.PlayerState
import org.salient.artplayer.conduction.ScaleType
import org.salient.artplayer.player.SystemMediaPlayer

/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/6/21 3:48 PM
 * @Description:
 */
class ArtplayerLoadImpl : ContentLoader {
    lateinit var videoView: HackVideoView
    lateinit var coverIv: AppCompatImageView
    lateinit var frameLayout: View
    lateinit var progress: ProgressBar
    lateinit var context: Context
    var targetUrl: String? = null
    override val displayRect: RectF
        get() = RectF()

    override fun init(context: Context, originUrl: String, targetUrl: String?, onMojitoViewCallback: OnMojitoViewCallback?) {
        this.context = context
        this.targetUrl = targetUrl
        frameLayout = LayoutInflater.from(context).inflate(R.layout.video_layout, null)
        videoView = frameLayout.findViewById(R.id.hackVideoView)
        progress = frameLayout.findViewById(R.id.progress)
        coverIv = frameLayout.findViewById(R.id.coverIv)

        videoView.visibility = View.GONE
        coverIv.scaleType = ImageView.ScaleType.CENTER_CROP
        videoView.setScreenScale(ScaleType.DEFAULT)

        videoView.background = null


        when (MainActivity.loaderType) {
            MainActivity.LoaderType.Coil -> {
                videoView.cover.load(originUrl)
            }
            MainActivity.LoaderType.Glide -> {
                Glide.with(context).load(originUrl).into(videoView.cover)
            }
            MainActivity.LoaderType.Fresco -> {
                videoView.cover.setImageURI(Uri.parse(originUrl))
            }
        }

        if (targetUrl != null) {
            videoView.mediaPlayer = SystemMediaPlayer().apply {
                setDataSource(videoView.context, Uri.parse(targetUrl))
            }
        }
    }

    override fun providerView(): View {
        return frameLayout
    }

    override fun providerRealView(): View {
        return coverIv
    }

    override fun dispatchTouchEvent(isDrag: Boolean, isActionUp: Boolean, isDown: Boolean, isHorizontal: Boolean): Boolean {
        return false
    }

    override fun dragging(width: Int, height: Int, ratio: Float) {

    }

    override fun beginBackToMin(isResetSize: Boolean) {
        videoView.setScreenScale(ScaleType.SCALE_CENTER_CROP)
    }

    override fun backToNormal() {

    }

    override fun loadAnimFinish() {
        coverIv.scaleType = ImageView.ScaleType.FIT_CENTER
        videoView.visibility = View.VISIBLE
        videoView.mediaPlayer?.playerStateLD?.observe(context as LifecycleOwner, Observer {
            if (it.code == PlayerState.STARTED.code) {
                progress.visibility = View.GONE
                coverIv.visibility = View.INVISIBLE
            } else if (it.code == PlayerState.ERROR.code || it.code == PlayerState.COMPLETED.code) {
                progress.visibility = View.GONE
                coverIv.visibility = View.VISIBLE
            } else {
                progress.visibility = View.VISIBLE
            }
        })
    }

    override fun needReBuildSize(): Boolean {
        return false
    }

    override fun useTransitionApi(): Boolean {
        return false
    }

    override fun isLongImage(width: Int, height: Int): Boolean {
        return false
    }

    override fun onTapCallback(onTapCallback: OnTapCallback) {
        videoView.setOnClickListener {
            onTapCallback.onTap(it, 0f, 0f)
        }
    }

    override fun onLongTapCallback(onLongTapCallback: OnLongTapCallback) {

    }

    override fun pageChange(isHidden: Boolean) {
        if (isHidden) {
            videoView.pause()
        } else {
            if (!videoView.isPlaying) {
                if (videoView.playerState.code < PlayerState.PREPARED.code) {
                    loadInitUrl()
                }
                videoView.start()
            }
        }
    }


    private fun loadInitUrl() {
        videoView.prepare()

    }
}