package app.dev.nick.music;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.nick.scalpel.Scalpel;
import com.nick.scalpel.ScalpelApplication;
import com.nick.scalpel.annotation.opt.ContextConfiguration;
import com.nick.scalpel.core.FieldWirer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import app.dev.nick.music.annotation.GetLogger;
import dev.nick.imageloader.ImageLoader;
import dev.nick.imageloader.LoaderConfig;
import dev.nick.imageloader.cache.CachePolicy;
import dev.nick.imageloader.loader.network.NetworkPolicy;
import dev.nick.logger.Logger;
import dev.nick.logger.LoggerManager;

import static com.nick.scalpel.core.utils.ReflectionUtils.getField;
import static com.nick.scalpel.core.utils.ReflectionUtils.makeAccessible;
import static com.nick.scalpel.core.utils.ReflectionUtils.setField;

@ContextConfiguration(xmlRes = 0)
public class MyApp extends ScalpelApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectAll()
                .build());

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

    @Override
    protected void onConfigScalpel(Scalpel scalpel) {
        super.onConfigScalpel(scalpel);
        scalpel.addFieldWirer(new LoggerWirer());
    }

    class LoggerWirer implements FieldWirer {

        @Override
        public Class<? extends Annotation> annotationClass() {
            return GetLogger.class;
        }

        @Override
        public void wire(Activity activity, Field field) {
            wire(activity.getApplicationContext(), activity, field);
        }

        @Override
        public void wire(Fragment fragment, Field field) {
            wire(fragment.getActivity().getApplicationContext(), fragment, field);
        }

        @Override
        public void wire(Service service, Field field) {
            wire(service.getApplicationContext(), service, field);
        }

        @Override
        public void wire(Context context, Object object, Field field) {
            makeAccessible(field);
            Object fieldObject = getField(field, object);
            if (fieldObject != null) return;
            GetLogger getLogger = field.getAnnotation(GetLogger.class);
            Class clz = getLogger.clz();
            setField(field, object, LoggerManager.getLogger(clz));
        }

        @Override
        public void wire(View root, Object object, Field field) {
            wire(root.getContext(), object, field);
        }
    }
}
