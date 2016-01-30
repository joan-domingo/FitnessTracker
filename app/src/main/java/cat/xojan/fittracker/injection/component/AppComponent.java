package cat.xojan.fittracker.injection.component;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import cat.xojan.fittracker.FitTrackerApp;
import cat.xojan.fittracker.injection.module.AppModule;
import cat.xojan.fittracker.presentation.BaseActivity;
import dagger.Component;

/**
 * A component whose lifetime is the life of the application.
 */
@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    // Field injections of any dependencies of the DemoApplication
    void inject(BaseActivity baseActivity);
}
