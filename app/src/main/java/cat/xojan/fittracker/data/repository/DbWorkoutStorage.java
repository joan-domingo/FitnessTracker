package cat.xojan.fittracker.data.repository;

import java.util.List;

import javax.inject.Inject;

import cat.xojan.fittracker.data.db.dao.WorkoutDao;
import cat.xojan.fittracker.data.entity.Workout;
import cat.xojan.fittracker.domain.repository.WorkoutRepository;

/**
 * Reads/Writes {@link Workout} data from Database.
 */
public class DbWorkoutStorage implements WorkoutRepository {

    private final WorkoutDao mWorkoutDao;

    @Inject
    public DbWorkoutStorage(WorkoutDao workoutDao) {
        mWorkoutDao = workoutDao;
    }

    @Override
    public void saveWorkout(Workout workout) {
        mWorkoutDao.insert(workout);
    }

    @Override
    public Workout loadWorkout(long workoutId) {
        return mWorkoutDao.load(workoutId);
    }

    @Override
    public List<Workout> loadAllWorkouts() {
        return mWorkoutDao.loadAll();
    }

    @Override
    public void removeWorkout(Workout workout) {
        mWorkoutDao.delete(workout);
    }
}
