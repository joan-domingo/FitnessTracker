package cat.xojan.fittracker;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import cat.xojan.fittracker.data.db.dao.DaoMaster;
import cat.xojan.fittracker.data.db.dao.DaoSession;
import cat.xojan.fittracker.injection.component.AppComponent;
import cat.xojan.fittracker.injection.component.DaggerAppComponent;
import cat.xojan.fittracker.injection.module.AppModule;
import io.fabric.sdk.android.Fabric;

public class FitTrackerApp extends Application {

    private static final String DB_NAME = "fittracker-db";

    private AppComponent mComponent;
    private RefWatcher mRefWatcher;
    private DaoSession mDaoSession;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        initDatabase();
        initInjector();
        initLeakDetection();
    }

    private void initDatabase() {
        DaoMaster.OpenHelper helper;
        if (BuildConfig.DEBUG) {
            helper = new DaoMaster.DevOpenHelper(this, DB_NAME, null);
        } else {
            helper = new DbHelper(this, DB_NAME, null);
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        mDaoSession = daoMaster.newSession();
    }

    private void initLeakDetection() {
        if (BuildConfig.DEBUG) {
            mRefWatcher = LeakCanary.install(this);
        }
    }

    private void initInjector() {
        mComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this, mDaoSession))
                .build();
    }

    public AppComponent getAppComponent() {
        return mComponent;
    }

    public static RefWatcher getRefWatcher(Context context) {
        FitTrackerApp application = (FitTrackerApp) context.getApplicationContext();
        return application.mRefWatcher;
    }

    private class DbHelper extends DaoMaster.OpenHelper {

        public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
