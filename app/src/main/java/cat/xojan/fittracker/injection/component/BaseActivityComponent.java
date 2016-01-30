package cat.xojan.fittracker.injection.component;

import android.app.Activity;
import android.content.Context;

import cat.xojan.fittracker.domain.FitnessDataInteractor;
import cat.xojan.fittracker.injection.module.BaseActivityModule;
import cat.xojan.fittracker.presentation.BaseActivity;
import dagger.Component;

/**
 * A base component upon which fragment's components may depend.  Activity-level components
 * should extend this component.
 */
@PerActivity // Subtypes of BaseActivityComponent should be decorated with @PerActivity.
@Component(
        dependencies = AppComponent.class,
        modules = BaseActivityModule.class
)
public interface BaseActivityComponent {
    void inject(BaseActivity baseActivity);
    Activity activity(); // Expose the activity to sub-graphs
    Context context();
}
