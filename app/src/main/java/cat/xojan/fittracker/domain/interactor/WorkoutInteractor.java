package cat.xojan.fittracker.domain.interactor;

import com.fernandocejas.frodo.annotation.RxLogObservable;

import java.util.List;

import javax.inject.Inject;

import cat.xojan.fittracker.data.entity.Workout;
import cat.xojan.fittracker.domain.repository.WorkoutRepository;
import rx.Observable;
import rx.Subscriber;

/**
 * Workout data interactor.
 */
public class WorkoutInteractor {

    private final WorkoutRepository mRepository;

    @Inject
    public WorkoutInteractor(WorkoutRepository mRepository) {
        this.mRepository = mRepository;
    }

    @RxLogObservable(RxLogObservable.Scope.STREAM)
    public Observable<Void> saveWorkout(final Workout workout) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                try {
                    mRepository.saveWorkout(workout);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @RxLogObservable(RxLogObservable.Scope.STREAM)
    public Observable<List<Workout>> loadAllWorkouts() {
        return Observable.create(new Observable.OnSubscribe<List<Workout>>() {
            @Override
            public void call(Subscriber<? super List<Workout>> subscriber) {
                try {
                    List<Workout> workouts = mRepository.loadAllWorkouts();
                    subscriber.onNext(workouts);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }
}
