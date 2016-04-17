package cat.xojan.fittracker.data.entity;

/**
 * Entity Workout.
 */
public class Workout {

    private long id;
    private String title; //nullable
    private long workoutTime; //milliseconds
    private long startTime; //milliseconds
    private long endTime; //milliseconds
    private long distance; //meters
}
