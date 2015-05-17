package cat.xojan.fittracker.workout.controller;

import android.os.SystemClock;
import android.widget.Chronometer;

import java.util.Calendar;

public class TimeController {

    private long mSessionStart;
    private long mSegmentStart;
    private long mSessionFinish;
    private long mWorkoutDuration;

    private long mTimeWhenPaused;
    private long mSegmentEnd;

    private static TimeController instance;
    private Chronometer mChronometer;

    public static TimeController getInstance() {
        if (instance == null) {
            instance = new TimeController();
        }
        return instance;
    }

    public void setSessionStart() {
        mSessionStart = mSegmentStart = Calendar.getInstance().getTimeInMillis();
        mWorkoutDuration = 0;
    }

    public void initChronometer(Chronometer chronometer) {
        mChronometer = chronometer;
        mChronometer.setOnChronometerTickListener(cArg -> {
            long t = SystemClock.elapsedRealtime() - cArg.getBase();
            int h = (int) (t / 3600000);
            int m = (int) (t - h * 3600000) / 60000;
            int s = (int) (t - h * 3600000 - m * 60000) / 1000;
            String hh = h < 10 ? "0" + h : h + "";
            String mm = m < 10 ? "0" + m : m + "";
            String ss = s < 10 ? "0" + s : s + "";
            cArg.setText(hh + ":" + mm + ":" + ss);
        });
        mChronometer.setText("00:00");
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();
    }


    public void lapFinish() {
        mSegmentEnd = Calendar.getInstance().getTimeInMillis();
        mWorkoutDuration = mWorkoutDuration + (mSegmentEnd - mSegmentStart);
    }

    public void lapStart() {
        mSegmentStart = mSegmentEnd;
        //reset chrono
        mChronometer.setBase(SystemClock.elapsedRealtime());
    }

    public void finish() {

    }

    public void resume() {
        mSegmentStart = Calendar.getInstance().getTimeInMillis();
        mChronometer.setBase(SystemClock.elapsedRealtime() + mTimeWhenPaused);
        mChronometer.start();
    }

    public void pause() {
        mTimeWhenPaused = mChronometer.getBase() - SystemClock.elapsedRealtime();
        mSessionFinish = mSegmentEnd = Calendar.getInstance().getTimeInMillis();
        mWorkoutDuration = mWorkoutDuration + (mSegmentEnd - mSegmentStart);
        mChronometer.stop();
    }

    public long getSegmentStartTime() {
        return mSegmentStart;
    }

    public long getSegmentEndTime() {
        return mSegmentEnd;
    }

    public long getSessionStartTime() {
        return mSessionStart;
    }

    public long getSessionEndTime() {
        return mSessionFinish;
    }

    public long getSessionWorkoutTime() {
        return mWorkoutDuration;
    }
}
