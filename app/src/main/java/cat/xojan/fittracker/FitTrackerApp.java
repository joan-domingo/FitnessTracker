package cat.xojan.fittracker;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import cat.xojan.fittracker.injection.component.AppComponent;
import cat.xojan.fittracker.injection.component.DaggerAppComponent;
import cat.xojan.fittracker.injection.module.AppModule;
import io.fabric.sdk.android.Fabric;

public class FitTrackerApp extends Application {

    private AppComponent mComponent;
    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        initInjector();
        initLeakDetection();
    }

    private void initLeakDetection() {
        if (BuildConfig.DEBUG) {
            refWatcher = LeakCanary.install(this);
        }
    }

    private void initInjector() {
        mComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public AppComponent getAppComponent() {
        return mComponent;
    }

    public static RefWatcher getRefWatcher(Context context) {
        FitTrackerApp application = (FitTrackerApp) context.getApplicationContext();
        return application.refWatcher;
    }
}
