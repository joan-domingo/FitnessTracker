package cat.xojan.fittracker.injection.component;

import cat.xojan.fittracker.injection.PerActivity;
import cat.xojan.fittracker.injection.module.BaseActivityModule;
import cat.xojan.fittracker.injection.module.StartupModule;
import cat.xojan.fittracker.presentation.startup.StartupActivity;
import dagger.Component;

@PerActivity
@Component(
        dependencies = AppComponent.class,
        modules = {
                BaseActivityModule.class,
                StartupModule.class
        }
)
public interface StartupComponent extends BaseActivityComponent {
    void inject(StartupActivity startupActivity);
}
