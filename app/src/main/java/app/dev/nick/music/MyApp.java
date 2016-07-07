package app.dev.nick.music;

import com.nick.scalpel.ScalpelApplication;
import com.nick.scalpel.annotation.opt.ContextConfiguration;

import dev.nick.imageloader.ImageLoader;
import dev.nick.imageloader.LoaderConfig;
import dev.nick.imageloader.cache.CachePolicy;
import dev.nick.imageloader.loader.network.NetworkPolicy;

@ContextConfiguration(xmlRes = 0)
public class MyApp extends ScalpelApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        ImageLoader.init(this, new LoaderConfig.Builder()
                .cachePolicy(CachePolicy.DEFAULT_CACHE_POLICY)
                .networkPolicy(NetworkPolicy.DEFAULT_NETWORK_POLICY)
                .cachingThreads(1)
                .loadingThreads(1)
                .debug(BuildConfig.DEBUG)
                .diskCacheEnabled(true)
                .memCacheEnabled(true)
                .build());
    }
}
