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

import com.ncapdevi.fragnav.FragNavController;
import com.nick.scalpel.Scalpel;
import com.nick.scalpel.annotation.request.RequirePermission;

import java.util.ArrayList;
import java.util.List;

import app.dev.nick.music.BuildConfig;
import app.dev.nick.music.utils.ColorUtils;
import app.dev.nick.music.content.fragment.HeadlessFragment;
import app.dev.nick.music.content.fragment.HelloFragment;
import app.dev.nick.music.R;
import app.dev.nick.music.content.fragment.SettingsFragment;
import app.dev.nick.music.content.fragment.TracksFragment;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

@TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
@RequirePermission(permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET})
public class MainActivity extends BaseActivity implements BottomNavigation.OnMenuItemSelectionListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    FragNavController controller = null;
    Toolbar toolbar;

    int[] mColors = new int[]{
            R.color.tab_tracks,
            R.color.tab_fav,
            R.color.tab_album,
            R.color.tab_list
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BottomNavigation.DEBUG = BuildConfig.DEBUG;

        setContentView(getActivityLayoutResId());

        Scalpel.getInstance().wire(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final int statusbarHeight = getStatusBarHeight();
        final boolean translucentStatus = hasTranslucentStatusBar();

        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.CoordinatorLayout01);

        if (translucentStatus) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) coordinatorLayout.getLayoutParams();
            params.topMargin = -statusbarHeight;

            params = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
            params.topMargin = statusbarHeight;
        }

        initializeUI(savedInstanceState);
    }

    protected int getActivityLayoutResId() {
        return R.layout.activity_main;
    }

    protected void initializeUI(final Bundle savedInstanceState) {
        final FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        assert floatingActionButton != null;
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        List<Fragment> fragments = new ArrayList<>(4);
        fragments.add(new HeadlessFragment());
        fragments.add(new TracksFragment());
        fragments.add(new SettingsFragment());
        fragments.add(new HelloFragment());
        controller = new FragNavController(getSupportFragmentManager(), R.id.container, fragments);
    }

    @Override
    protected void onStart() {
        super.onStart();
        controller.switchTab(FragNavController.TAB1);
    }

    @Override
    public void onMenuItemSelect(final int itemId, final int position) {
        getBottomNavigation().getBadgeProvider().remove(itemId);
        controller.switchTab(position);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ColorUtils.colorBurn(getResources().getColor(mColors[position])));
            toolbar.setBackgroundColor(getResources().getColor(mColors[position]));
        }
    }

    @Override
    public void onMenuItemReselect(@IdRes final int itemId, final int position) {
    }
}
