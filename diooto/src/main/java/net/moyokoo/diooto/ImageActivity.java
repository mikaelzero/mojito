package net.moyokoo.diooto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import net.moyokoo.drag.R;

import java.util.ArrayList;
import java.util.List;

public class ImageActivity extends AppCompatActivity {
    private FixMultiViewPager mViewPager;
    private String[] imageUrls;
    List<ContentViewOriginModel> contentViewOriginModels;
    private int currentPosition;

    public static void startImageActivity(Activity activity, ImageView[] imageViews, String[] imageUrls) {
        startImageActivity(activity, imageViews, imageUrls, 0);
    }

    public static void startImageActivity(Activity activity, View[] views, String[] imageUrls, int currentPosition) {
        Intent intent = new Intent(activity, ImageActivity.class);
        ArrayList<ContentViewOriginModel> contentViewOriginModels = new ArrayList<>();
        for (View imageView : views) {
            ContentViewOriginModel imageBean = new ContentViewOriginModel();
            int location[] = new int[2];
            imageView.getLocationOnScreen(location);
            imageBean.left = location[0];
            imageBean.top = location[1];
            imageBean.width = imageView.getWidth();
            imageBean.height = imageView.getHeight();
            contentViewOriginModels.add(imageBean);
        }
        intent.putParcelableArrayListExtra("viewData", contentViewOriginModels);
        intent.putExtra("currentPosition", currentPosition);
        intent.putExtra("imageUrls", imageUrls);
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
        currentPosition = getIntent().getIntExtra("currentPosition", 0);
        imageUrls = getIntent().getStringArrayExtra("imageUrls");
        contentViewOriginModels = getIntent().getParcelableArrayListExtra("viewData");
        final List<Fragment> fragmentList = new ArrayList<>();
        for (int i = 0; i < contentViewOriginModels.size(); i++) {
            ImageFragment imageFragment = ImageFragment.newInstance(imageUrls[i], contentViewOriginModels.get(i));
            fragmentList.add(imageFragment);
        }
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
}
