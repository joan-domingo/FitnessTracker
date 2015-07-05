package cat.xojan.fittracker.view.controller;

import android.os.SystemClock;
import android.widget.Chronometer;

import java.util.Calendar;

public class TimeController {

    private Chronometer mChronometer;
    private long mSessionStart;
    private long mSegmentStart;
    private long mSessionFinish;
    private long mWorkoutDuration;

    private long mTimeWhenPaused;
    private long mSegmentEnd;

    public TimeController() {
    }

    public void init(Chronometer chronometer) {
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
    }

    public void start() {
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();

        mSessionStart = mSegmentStart = Calendar.getInstance().getTimeInMillis();
        mWorkoutDuration = 0;
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

    public void pause() {
        mTimeWhenPaused = mChronometer.getBase() - SystemClock.elapsedRealtime();
        mSessionFinish = mSegmentEnd = Calendar.getInstance().getTimeInMillis();
        mWorkoutDuration = mWorkoutDuration + (mSegmentEnd - mSegmentStart);
        mChronometer.stop();
    }

    public void resume() {
        mSegmentStart = Calendar.getInstance().getTimeInMillis();
        mChronometer.setBase(SystemClock.elapsedRealtime() + mTimeWhenPaused);
        mChronometer.start();
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

    public void finish() {

    }

    public long getSegmentStartTime() {
        return mSegmentStart;
    }

    public long getSegmentTime() {
        return Calendar.getInstance().getTimeInMillis() - mSegmentStart;
    }

    public long getSegmentEndTime() {
        return mSegmentEnd;
    }
}
