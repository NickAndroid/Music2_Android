package app.dev.nick.music.content.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.File;
import java.util.List;

import app.dev.nick.music.R;
import app.dev.nick.music.content.activity.BaseActivity;
import app.dev.nick.music.model.Track;
import app.dev.nick.music.utils.MediaUtils;
import dev.nick.imageloader.ImageLoader;
import dev.nick.imageloader.display.DisplayOption;
import dev.nick.imageloader.display.animator.FadeInImageAnimator;
import dev.nick.logger.LoggerManager;
import it.sephiroth.android.library.bottomnavigation.BottomBehavior;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

public class BaseFragment extends Fragment {

    RecyclerView mRecyclerView;

    static String mArtworkUri = "content://media/external/audio/albumart";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.RecyclerView01);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LoggerManager.getLogger(getClass()).funcEnter();


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
                            params.bottomMargin -= navigationHeight;
                        } else {
                            totalHeight = navigation.getNavigationHeight();
                        }

                        createAdater(totalHeight);
                    } else {
                        params.bottomMargin -= navigationHeight;
                        createAdater(navigationHeight);
                    }
                    mRecyclerView.requestLayout();
                }
            });
        } else {
            final ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mRecyclerView.getLayoutParams();
            params.bottomMargin -= navigationHeight;
            createAdater(navigationHeight);
        }
    }

    private void createAdater(int height) {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(new Adapter(getContext(), height, MediaUtils.getTrackList(getActivity())));
    }

    public void scrollToTop() {
        mRecyclerView.smoothScrollToPosition(0);
    }

    private class Adapter extends RecyclerView.Adapter<TwoLinesViewHolder> {
        private final int navigationHeight;
        private final List<Track> data;

        public Adapter(final Context context, final int navigationHeight, List<Track> data) {
            this.navigationHeight = navigationHeight;
            this.data = data;
        }

        @Override
        public TwoLinesViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            final View view = LayoutInflater.from(getContext()).inflate(R.layout.simple_card_item, parent, false);
            final TwoLinesViewHolder holder = new TwoLinesViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final TwoLinesViewHolder holder, final int position) {
            if (position == getItemCount() - 1) {
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
                            .imageQuality(DisplayOption.ImageQuality.RAW)
                            .imageAnimator(new FadeInImageAnimator())
                            .build());
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    static class TwoLinesViewHolder extends RecyclerView.ViewHolder {

        final TextView title;
        final TextView description;
        final ImageView imageView;
        final int marginBottom;

        public TwoLinesViewHolder(final View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(android.R.id.title);
            description = (TextView) itemView.findViewById(android.R.id.text1);
            imageView = (ImageView) itemView.findViewById(android.R.id.icon);
            marginBottom = ((ViewGroup.MarginLayoutParams) itemView.getLayoutParams()).bottomMargin;
        }
    }
}
