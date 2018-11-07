package net.moyokoo.diooto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;

import net.moyokoo.drag.R;

import java.util.ArrayList;
import java.util.List;

public class ImageActivity extends AppCompatActivity {
    private FixMultiViewPager mViewPager;
    List<ContentViewOriginModel> contentViewOriginModels;
    List<ImageFragment> fragmentList;
    ContentViewConfig contentViewConfig;

    public static void startImageActivity(Activity activity, ContentViewConfig contentViewConfig) {
        Intent intent = new Intent(activity, ImageActivity.class);
        intent.putExtra("config", contentViewConfig);
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN;
        uiFlags |= 0x00001000;
        getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        setContentView(R.layout.activity_image);
        mViewPager = findViewById(R.id.viewPager);
        contentViewConfig = getIntent().getParcelableExtra("config");
        int currentPosition = contentViewConfig.getPosition();
        String[] imageUrls = contentViewConfig.getImageUrls();
        contentViewOriginModels = contentViewConfig.getContentViewOriginModels();
        fragmentList = new ArrayList<>();
        for (int i = 0; i < contentViewOriginModels.size(); i++) {
            ImageFragment imageFragment = ImageFragment.newInstance(
                    imageUrls[i], i, contentViewConfig.getType(), contentViewOriginModels.get(i)
            );
            fragmentList.add(imageFragment);
        }
        mViewPager.setOffscreenPageLimit(contentViewOriginModels.size());
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        });
        mViewPager.setCurrentItem(currentPosition);
    }

    public void finishView() {
        if (DragDiooto.onFinish != null) {
            DragDiooto.onFinish.finish(fragmentList.get(mViewPager.getCurrentItem()).getDragDiootoView());
        }
        DragDiooto.onLoadPhotoBeforeShowBigImage = null;
        DragDiooto.onShowToMaxFinish = null;
        DragDiooto.onProvideVideoView = null;
        DragDiooto.onFinish = null;
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            fragmentList.get(mViewPager.getCurrentItem()).backToMin();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
