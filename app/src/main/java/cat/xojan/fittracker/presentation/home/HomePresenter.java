package cat.xojan.fittracker.presentation.home;

import android.widget.RadioGroup;

import javax.inject.Inject;

import cat.xojan.fittracker.R;
import cat.xojan.fittracker.data.entity.DistanceUnit;
import cat.xojan.fittracker.domain.interactor.UnitDataInteractor;
import cat.xojan.fittracker.presentation.BasePresenter;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Presenter for {@link HomeActivity} and its fragments.
 */
public class HomePresenter implements BasePresenter {

    private final UnitDataInteractor mUnitDataInteractor;
    private Subscription mDistanceSubscription;
    private UnitChangeListener mUnitChangeListener;

    public interface UnitChangeListener {
        /**
         * Update distance unit.
         */
        void updateDistanceUnit(DistanceUnit distanceUnit);
    }

    @Inject
    public HomePresenter(UnitDataInteractor unitDataInteractor) {
        mUnitDataInteractor = unitDataInteractor;
    }

    public void listenToDistanceUnitUpdates(UnitChangeListener unitChangeListener) {
        mUnitChangeListener = unitChangeListener;
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {
        if (mDistanceSubscription != null) {
            mDistanceSubscription.unsubscribe();
        }
        mUnitChangeListener = null;
    }

    void getDistanceUnit(final RadioGroup distanceRadioGroup) {
        mDistanceSubscription = mUnitDataInteractor.getDistanceUnit()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<DistanceUnit>() {
                @Override
                public void call(DistanceUnit distanceUnit) {
                    switch (distanceUnit) {
                        case KILOMETER:
                            distanceRadioGroup.check(R.id.kilometers);
                            break;
                        default:
                            distanceRadioGroup.check(R.id.miles);
                            break;
                    }
                }
            });
    }

    public void setDistanceUnit(DistanceUnit distanceUnit) {
        mDistanceSubscription = mUnitDataInteractor.setDistanceUnit(distanceUnit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DistanceUnitSubscriber());
    }

    /**
     * Distance unit subscriber.
     */
    private class DistanceUnitSubscriber extends Subscriber<DistanceUnit> {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(DistanceUnit distanceUnit) {
            mUnitChangeListener.updateDistanceUnit(distanceUnit);
        }
    }
}
