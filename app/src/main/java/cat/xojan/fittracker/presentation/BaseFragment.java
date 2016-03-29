package cat.xojan.fittracker.presentation;

import android.support.v4.app.Fragment;

import com.squareup.leakcanary.RefWatcher;

import cat.xojan.fittracker.FitTrackerApp;
import cat.xojan.fittracker.injection.HasComponent;

public abstract class BaseFragment extends Fragment {

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = FitTrackerApp.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

    /**
     * Gets a component for dependency injection by its type.
     */
    @SuppressWarnings("unchecked")
    protected <C> C getComponent(Class<C> componentType) {
        return componentType.cast(((HasComponent<C>) getActivity()).getComponent());
    }
}
