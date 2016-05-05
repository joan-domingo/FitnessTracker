package cat.xojan.fittracker.injection.component;

import cat.xojan.fittracker.injection.PerActivity;
import cat.xojan.fittracker.injection.module.BaseActivityModule;
import cat.xojan.fittracker.injection.module.HomeModule;
import cat.xojan.fittracker.presentation.history.HistoryFragment;
import cat.xojan.fittracker.presentation.home.HomeActivity;
import cat.xojan.fittracker.presentation.home.HomeFragment;
import cat.xojan.fittracker.presentation.home.SettingsFragment;
import dagger.Component;

@PerActivity
@Component(
        dependencies = AppComponent.class,
        modules = {
                BaseActivityModule.class,
                HomeModule.class
        }
)
public interface HomeComponent extends BaseActivityComponent {
    void inject(HomeActivity homeActivity);
    void inject(HomeFragment homeFragment);
    void inject(HistoryFragment historyFragment);
    void inject(SettingsFragment settingsFragment);
}
