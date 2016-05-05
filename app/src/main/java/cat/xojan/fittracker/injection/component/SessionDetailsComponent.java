package cat.xojan.fittracker.injection.component;

import cat.xojan.fittracker.injection.PerActivity;
import cat.xojan.fittracker.injection.module.BaseActivityModule;
import cat.xojan.fittracker.injection.module.SessionDetailsModule;
import cat.xojan.fittracker.presentation.sessiondetails.SessionDetailsActivity;
import dagger.Component;

@PerActivity
@Component(
        dependencies = AppComponent.class,
        modules = {
                BaseActivityModule.class,
                SessionDetailsModule.class
        }
)
public interface SessionDetailsComponent extends BaseActivityComponent {
    void inject(SessionDetailsActivity sessionDetailsActivity);
}
