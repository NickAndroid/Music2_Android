package app.dev.nick.music.content.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.nick.scalpel.annotation.binding.FindView;
import com.nick.scalpel.annotation.request.RequirePermission;

import java.util.ArrayList;
import java.util.List;

import app.dev.nick.music.FragmentController;
import app.dev.nick.music.R;
import app.dev.nick.music.annotation.GetLogger;
import app.dev.nick.music.content.fragment.TracksFragment;
import app.dev.nick.music.utils.ColorUtils;
import dev.nick.logger.Logger;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

@TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
@RequirePermission(permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET})
public class MainActivity extends BaseActivity implements BottomNavigation.OnMenuItemSelectionListener {

    @FindView(id = R.id.toolbar)
    Toolbar mToolbar;
    @FindView(id = R.id.fab)
    FloatingActionButton mFab;

    int[] mColors = new int[]{
            R.color.tab_tracks,
            R.color.tab_fav,
            R.color.tab_album,
            R.color.tab_list
    };

    FragmentController mController;

    @GetLogger
    Logger mLogger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getActivityLayoutResId());
        initializeUI(savedInstanceState);
    }

    protected int getActivityLayoutResId() {
        return R.layout.activity_main;
    }

    protected void initializeUI(final Bundle savedInstanceState) {

        setSupportActionBar(mToolbar);

        final int statusbarHeight = getStatusBarHeight();
        final boolean translucentStatus = hasTranslucentStatusBar();

        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.CoordinatorLayout01);

        if (translucentStatus) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) coordinatorLayout.getLayoutParams();
            params.topMargin = -statusbarHeight;

            params = (ViewGroup.MarginLayoutParams) mToolbar.getLayoutParams();
            params.topMargin = statusbarHeight;
        }

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        assert mFab != null;
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mFab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });

        List<Fragment> fragments = new ArrayList<>(4);
        fragments.add(new TracksFragment());
        fragments.add(new TracksFragment());
        fragments.add(new TracksFragment());
        fragments.add(new TracksFragment());

        mController = new FragmentController(getSupportFragmentManager(), fragments);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mController.setDefaultIndex(0);
        mController.setCurrent(0);
    }

    @Override
    public void onMenuItemSelect(final int itemId, final int position) {
        mLogger.funcEnter();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mController.setCurrent(position);
                int themeColor = getResources().getColor(mColors[position]);
                mToolbar.setBackgroundColor(themeColor);
                mFab.setColorFilter(themeColor);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(ColorUtils.colorBurn(themeColor));
                }
            }
        });
    }

    @Override
    public void onMenuItemReselect(@IdRes final int itemId, final int position) {
        mLogger.funcEnter();
        ((TracksFragment) mController.getCurrent()).scrollToTop();
    }
}
