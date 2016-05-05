package cat.xojan.fittracker.domain.repository;

import java.util.List;

import cat.xojan.fittracker.data.entity.Location;
import cat.xojan.fittracker.data.entity.Workout;

/**
 * Workout Repository logic. Writes and reads {@link Workout} data.
 */
public interface WorkoutRepository {
    /**
     * Stores a {@link Workout}.
     */
    void saveWorkout(Workout workout);

    /**
     * Returns a {@link Workout} based on its id.
     */
    Workout loadWorkout(long workoutId);

    /**
     * Returns the list of all {@link Workout}.
     */
    List<Workout> loadAllWorkouts();

    /**
     * Remove the specified {@link Workout}.
     */
    void removeWorkout(Workout workout);

    /**
     * Store workout {@link Location}.
     */
    void saveLocations(List<Location> locations);
}
