package net.moyokoo.drag;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.miaoyongjun.administrator.mvideo.R;

import net.moyokoo.diooto.DragDiooto;
import net.moyokoo.diooto.DragDiooto2;
import net.moyokoo.diooto.DragDiootoView;
import net.moyokoo.diooto.FixMultiViewPager;
import net.moyokoo.diooto.ImageFragment;

import org.salient.artplayer.MediaPlayerManager;
import org.salient.artplayer.ScaleType;
import org.salient.artplayer.VideoView;
import org.salient.artplayer.ui.ControlPanel;

import java.util.ArrayList;
import java.util.List;

import me.panpf.sketch.Sketch;
import me.panpf.sketch.SketchImageView;

public class DisplayActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    String[] longImageUrl = new String[]{
            "https://ww4.sinaimg.cn/bmiddle/61e7945bly1fwnpjo7er0j215o6u77o1.jpg",
            "http://wx3.sinaimg.cn/large/9f780829ly1fwvwhq9cg3j2cn40e2npj.jpg",
            "https://wx2.sinaimg.cn/mw600/6d239c49ly1fwsvs7rtocj20k3cmpkjs.jpg",
            "https://wx1.sinaimg.cn/mw600/71038334gy1fwv2i5084aj20b42wigqi.jpg",
            "https://wx3.sinaimg.cn/large/8378206bly1fvf2j96kryj20dc7uhkjq.jpg",
            "https://wx4.sinaimg.cn/large/0075aoetgy1fwkmjmcl67j30b3cmchdw.jpg",
            "https://wx1.sinaimg.cn/mw600/71038334gy1fwv2i5084aj20b42wigqi.jpg",
            "https://wx3.sinaimg.cn/large/8378206bly1fvf2j96kryj20dc7uhkjq.jpg",
            "https://wx4.sinaimg.cn/large/0075aoetgy1fwkmjmcl67j30b3cmchdw.jpg"
    };
    String[] normalImageUlr = new String[]{
            "http://bmob-cdn-982.b0.upaiyun.com/2017/02/24/98754a6a401d5c48806b2b3863e32bed.jpg",
            "https://github.com/moyokoo/Media/blob/master/complete_android_fragment_lifecycle.png?raw=true",
            "http://bmob-cdn-982.b0.upaiyun.com/2017/02/24/f387251e4038bf4380169a6c5e5d64f9.jpg",
            "http://bmob-cdn-982.b0.upaiyun.com/2017/02/24/f387251e4038bf4380169a6c5e5d64f9.jpg",
            "https://n.sinaimg.cn/tech/transform/520/w180h340/20181105/piAX-hnknmqw9902121.gif",
            "http://bmob-cdn-982.b0.upaiyun.com/2017/02/24/98754a6a401d5c48806b2b3863e32bed.jpg",
            "http://bmob-cdn-982.b0.upaiyun.com/2017/02/24/98754a6a401d5c48806b2b3863e32bed.jpg",
            "http://bmob-cdn-982.b0.upaiyun.com/2017/02/24/98754a6a401d5c48806b2b3863e32bed.jpg",
            "http://bmob-cdn-982.b0.upaiyun.com/2017/02/24/98754a6a401d5c48806b2b3863e32bed.jpg"
    };
    Context context;
    int activityPosition;
    DragDiooto dragDiooto;
    DragDiootoView drPhotoDioView;
    FixMultiViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_display);
        drPhotoDioView = findViewById(R.id.drPhotoDioView);
        viewPager = findViewById(R.id.viewPager);
        activityPosition = getIntent().getIntExtra("position", 0);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Diooto");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setAdapter(new MainAdapter());

        findViewById(R.id.backdrop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sketch.with(DisplayActivity.this).getConfiguration().getDiskCache().clear();
                Sketch.with(DisplayActivity.this).getConfiguration().getBitmapPool().clear();
                Sketch.with(DisplayActivity.this).getConfiguration().getMemoryCache().clear();
            }
        });

        viewPager.setVisibility(View.GONE);
        List<Fragment> mDragDiootoViews = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            mDragDiootoViews.add(new ImageFragment());
        }
    }

    public static void newIntent(Activity activity, Bundle bundle) {
        Intent intent = new Intent(activity, DisplayActivity.class);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = null;
            View view = LayoutInflater.from(
                    DisplayActivity.this).inflate(R.layout.item_grid, parent,
                    false);
            int size = getResources().getDisplayMetrics().widthPixels / 3 - 16;
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(size, size);
            int padding = 16;
            lp.setMargins(padding, padding, padding, padding);
            view.setLayoutParams(lp);
            holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.srcImageView.displayImage(normalImageUlr[position]);
            holder.srcImageView.setShowGifFlagEnabled(R.drawable.ic_gif);
            holder.srcImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View srcView) {

                    int size = mRecyclerView.getChildCount();
                    View[] views = new View[size];
                    int[] realWidths = new int[size];
                    int[] realHeights = new int[size];
                    for (int i = 0; i < size; i++) {
                        ImageView recyImageView = mRecyclerView.getChildAt(i).findViewById(R.id.srcImageView);
                        views[i] = recyImageView;
                        realWidths[i] = 1920;
                        realHeights[i] = 720;
                    }
                    //显示的数量时根据提供的View数量来决定的,在recyclerView中
                    // 会出现有些view无法通过mRecyclerView.getChildCount()得到,其余View大小请自行计算
                    if (activityPosition == 3) {
                        //加载视频
                        dragDiooto = new DragDiooto(context)
                                .urls(normalImageUlr[position])
                                .position(holder.getAdapterPosition())
                                .views(holder.srcImageView)
                                .type(DragDiooto.VIDEO)
                                //提供视频View
                                .onProvideVideoView(new DragDiooto.OnProvideVideoView() {
                                    @Override
                                    public View provideView() {
                                        return new VideoView(context);
                                    }
                                })
                                //显示视频加载之前的缩略图
                                .loadPhotoBeforeShowBigImage(new DragDiooto.OnLoadPhotoBeforeShowBigImage() {
                                    @Override
                                    public void loadView(SketchImageView sketchImageView, int position) {
                                        sketchImageView.displayImage(normalImageUlr[holder.getAdapterPosition()]);
                                    }
                                })
                                //动画到最大化时的接口
                                .onVideoLoadEnd(new DragDiooto.OnShowToMaxFinish() {
                                    @Override
                                    public void onShowToMax(DragDiootoView dragDiootoView) {
                                        VideoView videoView = (VideoView) dragDiootoView.getContentView();
                                        videoView.setControlPanel(new ControlPanel(context));
                                        videoView.setUp("http://bmob-cdn-982.b0.upaiyun.com/2017/02/23/266454624066f2b680707492a0664a97.mp4");
                                        videoView.start();
                                        dragDiootoView.notifySize(1920, 1080);
                                        MediaPlayerManager.instance().setScreenScale(ScaleType.SCALE_CENTER_CROP);
                                    }
                                })
                                //到最小状态的接口
                                .onFinish(new DragDiooto.OnFinish() {
                                    @Override
                                    public void finish(DragDiootoView dragDiootoView) {
                                        MediaPlayerManager.instance().releasePlayerAndView(context);
                                    }
                                })
                                .start();
                    } else if (activityPosition == 1) {
                        //加载单张图片
                        dragDiooto = new DragDiooto(context)
                                .urls(normalImageUlr[position])
                                .type(DragDiooto.PHOTO)
                                .position(0)
                                .views(views[holder.getAdapterPosition()])
                                .loadPhotoBeforeShowBigImage(new DragDiooto.OnLoadPhotoBeforeShowBigImage() {
                                    @Override
                                    public void loadView(SketchImageView sketchImageView, int position) {
                                        sketchImageView.displayImage(normalImageUlr[holder.getAdapterPosition()]);
                                    }

                                })
                                .start();
                    } else {
                        DragDiooto2 dragDiooto = new DragDiooto2(context)
//                                .urls(activityPosition == 2 ? longImageUrl : normalImageUlr)
                                .urls(normalImageUlr)
                                .type(DragDiooto.PHOTO)
                                .position(holder.getAdapterPosition())
                                .views(views)
                                .loadPhotoBeforeShowBigImage(new DragDiooto2.OnLoadPhotoBeforeShowBigImage() {
                                    @Override
                                    public void loadView(SketchImageView sketchImageView, int position) {
                                        sketchImageView.displayImage(normalImageUlr[holder.getAdapterPosition()]);
                                    }
                                })
                                .start(getSupportFragmentManager());
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return normalImageUlr.length;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            SketchImageView srcImageView;

            public MyViewHolder(View view) {
                super(view);
                srcImageView = view.findViewById(R.id.srcImageView);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaPlayerManager.instance().pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaPlayerManager.instance().releasePlayerAndView(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return MediaPlayerManager.instance().backPress() || (dragDiooto != null && dragDiooto.handleKeyDown(keyCode))
                || super.onKeyDown(keyCode, event);
    }
}
