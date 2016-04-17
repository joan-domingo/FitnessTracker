package cat.xojan.fittracker.presentation.home;

import javax.inject.Inject;

import cat.xojan.fittracker.presentation.BasePresenter;

/**
 * Created by Joan on 24/01/2016.
 */
public class HomePresenter implements BasePresenter {

    @Inject
    public HomePresenter() {

    }

    @Override
    public void resume() {
        calculateDataOverview();
    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {

    }

    public void calculateDataOverview() {
    }
}
