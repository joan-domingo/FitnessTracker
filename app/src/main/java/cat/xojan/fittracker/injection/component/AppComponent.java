package cat.xojan.fittracker.injection.component;

import android.location.LocationManager;

import javax.inject.Singleton;

import cat.xojan.fittracker.data.entity.UserData;
import cat.xojan.fittracker.injection.module.AppModule;
import cat.xojan.fittracker.navigation.Navigator;
import cat.xojan.fittracker.presentation.BaseActivity;
import cat.xojan.fittracker.util.LocationFetcher;
import dagger.Component;

/**
 * A component whose lifetime is the life of the application.
 */
@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    void inject(BaseActivity baseActivity);

    //Exposed to sub-graphs.
    UserData userData();
    Navigator navigator();
    LocationManager locationManager();
    LocationFetcher locationFetcher();
}