package cat.xojan.fittracker.view.presenter;

public class WorkoutPresenter {

    private boolean mIsFirstLocation;

    public WorkoutPresenter() {
        mIsFirstLocation = true;
    }

    public boolean getIsFirstLocation() {
        return mIsFirstLocation;
    }

    private void setIsFirstLocation(boolean b) {
        mIsFirstLocation = b;
    }

    public void gotFirstLocation() {
        setIsFirstLocation(false);
    }
}
