package cat.xojan.fittracker.domain.interactor;

import cat.xojan.fittracker.data.entity.DistanceUnit;
import cat.xojan.fittracker.domain.repository.PreferencesRepository;
import rx.Observable;
import rx.Subscriber;

/**
 * unit data interactor.
 */
public class UnitDataInteractor {

    private PreferencesRepository mUnitDataRepository;

    public UnitDataInteractor(PreferencesRepository firstRunRepository) {
        mUnitDataRepository = firstRunRepository;
    }

    public Observable<DistanceUnit> getDistanceUnit() {
        return Observable.create(new Observable.OnSubscribe<DistanceUnit>() {
            @Override
            public void call(Subscriber<? super DistanceUnit> subscriber) {
                try {
                    DistanceUnit distanceUnit = mUnitDataRepository.getMeasureUnit();
                    subscriber.onNext(distanceUnit);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public Observable<DistanceUnit> setDistanceUnit(final DistanceUnit distanceUnit) {
        return Observable.create(new Observable.OnSubscribe<DistanceUnit>() {
            @Override
            public void call(Subscriber<? super DistanceUnit> subscriber) {
                try {
                    mUnitDataRepository.setMeasureUnit(distanceUnit);
                    subscriber.onNext(distanceUnit);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }
}
