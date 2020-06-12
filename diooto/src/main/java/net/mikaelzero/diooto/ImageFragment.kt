package net.mikaelzero.diooto

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_image.*
import net.mikaelzero.diooto.Mojito.Companion.contentLoader
import net.mikaelzero.diooto.Mojito.Companion.imageLoader
import net.mikaelzero.diooto.Mojito.Companion.imageViewFactory
import net.mikaelzero.diooto.config.ContentViewOriginModel
import net.mikaelzero.diooto.interfaces.ImageViewFactory
import net.mikaelzero.diooto.loader.DefaultImageCallback
import net.mikaelzero.diooto.loader.ImageLoader
import java.io.File

class ImageFragment : Fragment() {
    var contentViewOriginModel: ContentViewOriginModel? = null
    var url: String? = null
    var showView: View? = null
    var position = 0
    var showImmediately = false
    private var mImageLoader: ImageLoader? = null
    private var mViewFactory: ImageViewFactory? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (context == null || activity == null) {
            return
        }
        mImageLoader = imageLoader()
        mViewFactory = imageViewFactory()
        if (arguments != null) {
            url = arguments!!.getString("url")
            position = arguments!!.getInt("position")
            showImmediately = arguments!!.getBoolean("showImmediately")
            contentViewOriginModel = arguments!!.getParcelable("model")
        }
        ImageActivity.iProgress?.attach(position, loadingLayout)
        loadingLayout.visibility = View.GONE
        val imageContentViewImpl = contentLoader()
        dragDiootoView.setContentLoader(imageContentViewImpl)
        showView = imageContentViewImpl?.providerRealView()
//        dragDiootoView.setOnShowFinishListener(new DragDiootoView.OnShowFinishListener() {
//            @Override
//            public void showFinish(DragDiootoView view, boolean showImmediately) {
//                if (type == DiootoConfig.VIDEO) {
//                    loadingLayout.setVisibility(View.VISIBLE);
//                    if (ImageActivity.iProgress != null) {
//                        ImageActivity.iProgress.onStart(position);
//                    }
//                    if (Diooto.onShowToMaxFinishListener != null) {
//                        Diooto.onShowToMaxFinishListener.onShowToMax(dragDiootoView,
//                                (SubsamplingScaleImageView) dragDiootoView.getContentParentView().getChildAt(1),
//                                ImageActivity.iProgress.getProgressView(position));
//                    }
//                } else if (type == DiootoConfig.PHOTO && view.getContentView() instanceof SubsamplingScaleImageView && !unClickPosHasCache) {
//                    loadImage(true);
//                }
//            }
//        });
        dragDiootoView.setOnDragListener { view1: MojitoView?, moveX: Float, moveY: Float ->
            if (ImageActivity.iIndicator != null) {
                ImageActivity.iIndicator!!.move(moveX, moveY)
            }
        }
        //        DiskCache diskCache = Sketch.with(getContext()).getConfiguration().getDiskCache();
//        unClickPosHasCache = type == DiootoConfig.PHOTO && !((ImageActivity) getActivity()).isNeedAnimationForClickPosition(position) && diskCache.exist(url);
//        if (unClickPosHasCache) {
//            ((ImageActivity) getActivity()).refreshNeedAnimationForClickPosition();
//            loadImage(false);
//        } else {
//            dragDiootoView.putData(contentViewOriginModel.getLeft(), contentViewOriginModel.getTop(), contentViewOriginModel.getWidth(), contentViewOriginModel.getHeight());
//            //如果显示的点击的position  则进行动画处理
//            dragDiootoView.show(!shouldShowAnimation);
//        }
        dragDiootoView.setOnFinishListener {
            if (context is ImageActivity) {
                (context as ImageActivity).finishView()
            }
            Mojito.onFinishListener?.finish(dragDiootoView)
        }
        dragDiootoView.setOnReleaseListener { isToMax: Boolean, isToMin: Boolean ->
            ImageActivity.iIndicator?.fingerRelease(isToMax, isToMin)
        }
        mImageLoader?.loadImage(
            showView.hashCode(),
            Uri.parse(url),
            object : DefaultImageCallback() {
                override fun onSuccess(image: File) {
                    mViewFactory!!.loadSillContent(showView!!, Uri.fromFile(image))
                    val options = BitmapFactory.Options()
                    options.inJustDecodeBounds = true
                    BitmapFactory.decodeFile(image.absolutePath, options)
                    val h = options.outHeight
                    val w = options.outWidth
                    dragDiootoView.putData(
                        contentViewOriginModel!!.getLeft(), contentViewOriginModel!!.getTop(),
                        contentViewOriginModel!!.getWidth(), contentViewOriginModel!!.getHeight(),
                        w, h
                    )
                    dragDiootoView.show(showImmediately && !Mojito.showImmediatelyFlag)
                    Mojito.showImmediatelyFlag = false
                }
            })
    }

    private fun loadWithoutCache(needReCheckCache: Boolean) {

//        loadRequest = Sketch.with(getContext()).load(url, new LoadListener() {
//            @Override
//            public void onStarted() {
//                if (!needReCheckCache) {
//                    loadingLayout.setVisibility(View.VISIBLE);
//                    if (ImageActivity.iProgress != null) {
//                        ImageActivity.iProgress.onStart(position);
//                    }
//                }
//
//            }
//
//            @Override
//            public void onCompleted(@NonNull LoadResult result) {
//                if (!needReCheckCache) {
//                    loadingLayout.setVisibility(View.GONE);
//                    if (ImageActivity.iProgress != null) {
//                        ImageActivity.iProgress.onFinish(position);
//                    }
//                }
//                if (result.getGifDrawable() != null) {
//                    result.getGifDrawable().followPageVisible(true, true);
//                }
//                int w = result.getBitmap().getWidth();
//                int h = result.getBitmap().getHeight();
//                dragDiootoView.notifySize(w, h);
//                sketchImageView.displayImage(url);
//                unClickPosHasCache = true;
//            }
//
//            @Override
//            public void onError(@NonNull ErrorCause cause) {
//                if (!needReCheckCache) {
//                    if (ImageActivity.iProgress != null) {
//                        ImageActivity.iProgress.onFailed(position);
//                    }
//                }
//            }
//
//            @Override
//            public void onCanceled(@NonNull CancelCause cause) {
//            }
//        }).downloadProgressListener(new DownloadProgressListener() {
//            @Override
//            public void onUpdateDownloadProgress(int totalLength, int completedLength) {
//                if (!needReCheckCache) {
//                    loadingLayout.setVisibility(View.VISIBLE);
//                    int ratio = (int) (completedLength / (float) totalLength * 100);
//                    if (ImageActivity.iProgress != null) {
//                        ImageActivity.iProgress.onProgress(position, ratio);
//                    }
//                }
//            }
//        }).commit();
    }

    fun backToMin() {
        dragDiootoView?.backToMin()
    }

    companion object {
        fun newInstance(
            url: String?,
            position: Int,
            shouldShowAnimation: Boolean,
            contentViewOriginModel: ContentViewOriginModel?
        ): ImageFragment {
            val args = Bundle()
            args.putString("url", url)
            args.putInt("position", position)
            args.putBoolean("showImmediately", shouldShowAnimation)
            args.putParcelable("model", contentViewOriginModel)
            val fragment = ImageFragment()
            fragment.arguments = args
            return fragment
        }
    }
}