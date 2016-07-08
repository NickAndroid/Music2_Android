package app.dev.nick.music.content.activity;

import android.annotation.TargetApi;
import android.graphics.Typeface;
import android.os.Build;
import android.view.WindowManager;

import com.nick.scalpel.ScalpelAutoActivity;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import app.dev.nick.music.R;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

public abstract class BaseActivity extends ScalpelAutoActivity implements BottomNavigation.OnMenuItemSelectionListener {

    private SystemBarTintManager mSystemBarTint;
    private boolean mTranslucentStatus;
    private boolean mTranslucentStatusSet;
    private boolean mTranslucentNavigation;
    private boolean mTranslucentNavigationSet;
    private BottomNavigation mBottomNavigation;

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mBottomNavigation = (BottomNavigation) findViewById(R.id.bottom_navigation);
        if (null != mBottomNavigation) {
            mBottomNavigation.setOnMenuItemClickListener(this);
            mBottomNavigation.setDefaultTypeface(Typeface.createFromAsset(getAssets(), "RobotoLight.ttf"));
        }
    }

    public BottomNavigation getBottomNavigation() {
        return mBottomNavigation;
    }

    public SystemBarTintManager getSystemBarTint() {
        if (null == mSystemBarTint) {
            mSystemBarTint = new SystemBarTintManager(this);
        }
        return mSystemBarTint;
    }

    public int getStatusBarHeight() {
        return getSystemBarTint().getConfig().getStatusBarHeight();
    }

    @TargetApi(19)
    public boolean hasTranslucentStatusBar() {
        if (!mTranslucentStatusSet) {
            if (Build.VERSION.SDK_INT >= 19) {
                mTranslucentStatus =
                        ((getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                                == WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            } else {
                mTranslucentStatus = false;
            }
            mTranslucentStatusSet = true;
        }
        return mTranslucentStatus;
    }

    @TargetApi(19)
    public boolean hasTranslucentNavigation() {
        if (!mTranslucentNavigationSet) {
            final SystemBarTintManager.SystemBarConfig config = getSystemBarTint().getConfig();
            if (Build.VERSION.SDK_INT >= 19) {
                boolean themeConfig =
                        ((getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
                                == WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

                mTranslucentNavigation = themeConfig && config.hasNavigtionBar() && config.isNavigationAtBottom()
                        && config.getNavigationBarHeight() > 0;
            }
            mTranslucentNavigationSet = true;
        }
        return mTranslucentNavigation;
    }
}
