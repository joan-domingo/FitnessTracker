package cat.xojan.fittracker.presentation.home;

import cat.xojan.fittracker.data.entity.UserData;
import cat.xojan.fittracker.presentation.BasePresenter;

/**
 * Created by Joan on 24/01/2016.
 */
public class HomePresenter implements BasePresenter {

    private final UserData mUserData;

    public HomePresenter(UserData userData) {
        mUserData = userData;
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
