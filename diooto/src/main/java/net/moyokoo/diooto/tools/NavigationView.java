package net.moyokoo.diooto.tools;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class NavigationView extends View {

    private Display mDisplay;
    private DisplayMetrics mDisplayMetrics;
    private Configuration mConfiguration;
    private int mDefaultBarSize;
    private int mBarSize;

    public NavigationView(Context context) {
        this(context, null, 0);
    }

    public NavigationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mDisplay = windowManager.getDefaultDisplay();
        mDisplayMetrics = new DisplayMetrics();
        Resources resources = getResources();
        mConfiguration = resources.getConfiguration();

        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        mDefaultBarSize = resources.getDimensionPixelSize(resourceId);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (isLandscape()) {
                mDisplay.getRealMetrics(mDisplayMetrics);
                mBarSize = mDisplayMetrics.widthPixels - getDisplayWidth(mDisplay);
                setMeasuredDimension(mBarSize, MeasureSpec.getSize(heightMeasureSpec));
            } else {
                mDisplay.getRealMetrics(mDisplayMetrics);
                mBarSize = mDisplayMetrics.heightPixels - getDisplayHeight(mDisplay);
                setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), mBarSize);
            }
        } else {
            setMeasuredDimension(0, 0);
        }
    }

    private static int getDisplayWidth(Display display) {
        Point point = new Point();
        display.getSize(point);
        return point.x;
    }

    private static int getDisplayHeight(Display display) {
        Point point = new Point();
        display.getSize(point);
        return point.y;
    }

    /**
     * Get the default height of navigation bar.
     */
    public int getDefaultBarSize() {
        return mDefaultBarSize;
    }

    /**
     * Get the height of navigation bar.
     */
    public int getBarSize() {
        return mBarSize;
    }

    /**
     * Whether landscape screen.
     */
    protected boolean isLandscape() {
        switch (mConfiguration.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE: {
                return true;
            }
            case Configuration.ORIENTATION_UNDEFINED:
            case Configuration.ORIENTATION_PORTRAIT:
            default: {
                return false;
            }
        }
    }
}