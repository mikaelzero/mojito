package net.mikaelzero.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gyf.barlibrary.ImmersionBar;

import net.mikaelzero.mojito.ImageActivity;
import net.mikaelzero.mojito.Mojito;
import net.mikaelzero.mojito.interfaces.CircleIndexIndicator;

import org.jetbrains.annotations.NotNull;
import org.salient.artplayer.MediaPlayerManager;

import java.util.Arrays;

public class DisplayActivity extends AppCompatActivity {
    WrapRecyclerView mRecyclerView;
    String[] longImageUrl = new String[]{
            "https://cdn.nlark.com/yuque/0/2020/jpeg/252337/1591856982603-assets/web-upload/c9072e47-5ce0-4a5f-ab5c-212d1bca3bc9.jpeg",
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
            "https://cdn.nlark.com/yuque/0/2020/jpeg/252337/1592042332605-assets/web-upload/1af8e4c0-bf8b-410a-bfff-a16fec01ccb5.jpeg",
            "https://cdn.nlark.com/yuque/0/2020/jpeg/252337/1591710912974-assets/web-upload/1e6325b7-4e26-443f-98f8-aa3925222ea1.jpeg",
            "https://cdn.nlark.com/yuque/0/2020/jpeg/252337/1588042170204-assets/web-upload/48a5152a-5024-43fd-bd50-796d6f284e77.jpeg",
            "https://cdn.nlark.com/yuque/0/2020/jpeg/252337/1592042333257-assets/web-upload/dfe8a4eb-9872-444b-b2a5-83378f467915.jpeg",
            "https://cdn.nlark.com/yuque/0/2020/jpeg/252337/1591753659216-assets/web-upload/2c772338-b6b6-4173-a830-202831511172.jpeg",
            "https://cdn.nlark.com/yuque/0/2020/jpeg/252337/1592042333210-assets/web-upload/8d20ed3d-1472-47c9-a2e6-da96e6019299.jpeg",
            "https://cdn.nlark.com/yuque/0/2020/gif/252337/1592042334187-assets/web-upload/29de7d66-d904-439e-b547-1bdc58934b50.gif",
            "https://cdn.nlark.com/yuque/0/2020/jpeg/252337/1592042333165-assets/web-upload/cde12f44-07bb-46aa-ab7d-0ced4783b2ee.jpeg",
//            "https://cdn.nlark.com/yuque/0/2020/gif/252337/1592042334373-assets/web-upload/d44ddb2e-f51f-4495-aa58-178de673d066.gif"
            "https://cdn.nlark.com/yuque/0/2020/jpeg/252337/1591856982603-assets/web-upload/c9072e47-5ce0-4a5f-ab5c-212d1bca3bc9.jpeg",
//            "https://cdn.nlark.com/yuque/0/2020/jpeg/252337/1592057985345-assets/web-upload/c2fe2b62-5519-4129-856e-ba19428a508a.jpeg",
    };
    Context context;
    int activityPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ImmersionBar.with(this).fullScreen(true).init();
        setContentView(R.layout.activity_display);
        activityPosition = getIntent().getIntExtra("position", 0);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Mojito");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setAdapter(new MainAdapter());
        mRecyclerView.addHeaderView(LayoutInflater.from(this).inflate(R.layout.adapter_header, null));
        mRecyclerView.addFooterView(LayoutInflater.from(this).inflate(R.layout.adapter_footer, null));
        Mojito.prefetch(normalImageUlr);
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
            View view = LayoutInflater.from(DisplayActivity.this).inflate(R.layout.item_grid, parent, false);
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
            Glide.with(context).load(normalImageUlr[position]).into(holder.srcImageView);
            holder.srcImageView.setOnClickListener(srcView -> {

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
                if (activityPosition == 3) {
                    // TODO 加载视频
                } else if (activityPosition == 1) {
                    //加载单张图片
                    Mojito.with(context)
                            .urls(normalImageUlr[position])
                            .position(0)
                            .views(views[holder.getAdapterPosition()])
                            .start();
                } else {
                    Mojito.with(context)
                            .urls(Arrays.asList(activityPosition == 2 ? longImageUrl : normalImageUlr))
                            .position(holder.getAdapterPosition(), 1)
                            .views(mRecyclerView, R.id.srcImageView)
                            .setOnLongPressListener((view, x, y, position1) -> {
                                Toast.makeText(context, "长按长按长按", Toast.LENGTH_SHORT).show();
                            })
                            .start();
                }
            });
        }

        @Override
        public int getItemCount() {
            return normalImageUlr.length;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView srcImageView;

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

}
