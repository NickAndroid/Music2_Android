package app.dev.nick.music.content.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.nick.scalpel.Scalpel;
import com.nick.scalpel.ScalpelAutoFragment;
import com.nick.scalpel.annotation.binding.FindView;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.File;
import java.util.List;

import app.dev.nick.music.MyApp;
import app.dev.nick.music.R;
import app.dev.nick.music.annotation.GetLogger;
import app.dev.nick.music.content.activity.BaseActivity;
import app.dev.nick.music.control.TabAction;
import app.dev.nick.music.model.Track;
import app.dev.nick.music.utils.MediaUtils;
import dev.nick.eventbus.Event;
import dev.nick.eventbus.EventBus;
import dev.nick.imageloader.ImageLoader;
import dev.nick.imageloader.display.DisplayOption;
import dev.nick.imageloader.display.animator.FadeInImageAnimator;
import dev.nick.logger.Logger;
import dev.nick.logger.LoggerManager;
import it.sephiroth.android.library.bottomnavigation.BottomBehavior;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

public class TracksFragment extends ScalpelAutoFragment {

    @FindView(id = R.id.recycler_view)
    RecyclerView mRecyclerView;

    @GetLogger(clz = TracksFragment.class)
    Logger mLogger;

    static String mArtworkUri = "content://media/external/audio/albumart";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        BaseActivity activity = (BaseActivity) getActivity();
        final SystemBarTintManager.SystemBarConfig config = activity.getSystemBarTint().getConfig();

        final int navigationHeight;
        final int actionbarHeight;

        if (activity.hasTranslucentNavigation()) {
            navigationHeight = config.getNavigationBarHeight();
        } else {
            navigationHeight = 0;
        }

        if (activity.hasTranslucentStatusBar()) {
            actionbarHeight = config.getActionBarHeight();
        } else {
            actionbarHeight = 0;
        }

        final BottomNavigation navigation = activity.getBottomNavigation();
        if (null != navigation) {
            navigation.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    navigation.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    final CoordinatorLayout.LayoutParams coordinatorLayoutParams =
                            (CoordinatorLayout.LayoutParams) navigation.getLayoutParams();

                    final CoordinatorLayout.Behavior behavior = coordinatorLayoutParams.getBehavior();
                    final ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mRecyclerView.getLayoutParams();

                    if (behavior instanceof BottomBehavior) {
                        final boolean scrollable = ((BottomBehavior) behavior).isScrollable();

                        int totalHeight;

                        if (scrollable) {
                            totalHeight = navigationHeight;
                        } else {
                            totalHeight = navigation.getNavigationHeight();
                        }
                        createAdapter(totalHeight);
                    } else {
                        params.bottomMargin -= navigationHeight;
                        createAdapter(navigationHeight);
                    }
                    mRecyclerView.requestLayout();
                }
            });
        } else {
            final ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mRecyclerView.getLayoutParams();
            params.bottomMargin -= navigationHeight;
            createAdapter(navigationHeight);
        }
    }

    private void createAdapter(int height) {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(new Adapter(height, MediaUtils.getTrackList(getActivity())));
    }

    public void scrollToTop() {
        mRecyclerView.smoothScrollToPosition(0);
    }

    private class Adapter extends RecyclerView.Adapter<TwoLinesViewHolder> {

        private final int navigationHeight;
        private final List<Track> data;

        public Adapter(final int navigationHeight, List<Track> data) {
            this.navigationHeight = navigationHeight;
            this.data = data;
        }

        @Override
        public TwoLinesViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            final View view = LayoutInflater.from(getContext()).inflate(R.layout.simple_card_item, parent, false);
            return new TwoLinesViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final TwoLinesViewHolder holder, final int position) {
            if (position == getItemCount() - 1) {
                LoggerManager.getLogger(MyApp.class).funcEnter();
                ((ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams()).bottomMargin = holder.marginBottom + navigationHeight;
            } else {
                ((ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams()).bottomMargin = holder.marginBottom;
            }

            final Track item = data.get(position);
            holder.title.setText(item.getTitle());
            holder.description.setText(item.getArtist());
            holder.imageView.setImageBitmap(null);

            String uri = mArtworkUri + File.separator + item.getAlbumId();

            ImageLoader.getInstance().displayImage(uri, holder.imageView,
                    new DisplayOption.Builder()
                            .imageQuality(DisplayOption.ImageQuality.FIT_VIEW)
                            .imageAnimator(new FadeInImageAnimator())
                            .build());
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    static class TwoLinesViewHolder extends RecyclerView.ViewHolder {

        @FindView(id = android.R.id.title)
        TextView title;
        @FindView(id = android.R.id.text1)
        TextView description;
        @FindView(id = android.R.id.icon)
        ImageView imageView;
        int marginBottom;

        public TwoLinesViewHolder(final View itemView) {
            super(itemView);
            Scalpel.getInstance().wire(itemView, this);
            marginBottom = ((ViewGroup.MarginLayoutParams) itemView.getLayoutParams()).bottomMargin;
        }
    }
}
