package cat.xojan.fittracker.injection.component;

import cat.xojan.fittracker.injection.PerActivity;
import cat.xojan.fittracker.injection.module.BaseActivityModule;
import cat.xojan.fittracker.injection.module.SplashModule;
import cat.xojan.fittracker.presentation.splash.SplashActivity;
import dagger.Component;

@PerActivity
@Component(
        dependencies = AppComponent.class,
        modules = {
                BaseActivityModule.class,
                SplashModule.class
        }
)
public interface SplashComponent extends BaseActivityComponent {
    void inject(SplashActivity splashActivity);
}
