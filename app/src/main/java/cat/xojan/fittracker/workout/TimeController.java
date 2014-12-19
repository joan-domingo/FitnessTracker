package cat.xojan.fittracker.workout;

import android.os.SystemClock;
import android.widget.Chronometer;

import java.util.Calendar;

import cat.xojan.fittracker.Constant;

public class TimeController {

    private Chronometer mChronometer;
    private long mSessionStart;
    private long mSegmentStart;
    private long mSessionFinish;

    private static TimeController instance = null;
    private long mTimeWhenPaused;

    public TimeController() {}

    public static TimeController getInstance() {
        if(instance == null) {
            instance = new TimeController();
        }
        return instance;
    }

    public void init(Chronometer chronometer) {
        mChronometer = chronometer;
        mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer cArg) {
                long t = SystemClock.elapsedRealtime() - cArg.getBase();
                int h = (int) (t / 3600000);
                int m = (int) (t - h * 3600000) / 60000;
                int s = (int) (t - h * 3600000 - m * 60000) / 1000;
                String hh = h < 10 ? "0" + h : h + "";
                String mm = m < 10 ? "0" + m : m + "";
                String ss = s < 10 ? "0" + s : s + "";
                cArg.setText(hh + ":" + mm + ":" + ss);
            }
        });
    }

    public void start() {
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();

        mSessionStart = mSegmentStart = Calendar.getInstance().getTimeInMillis();
    }

    public void lap() {
        //reset session time
        mSegmentStart = Calendar.getInstance().getTimeInMillis();

        //reset chrono
        mChronometer.setBase(SystemClock.elapsedRealtime());
    }

    public void pause() {
        mTimeWhenPaused = mChronometer.getBase() - SystemClock.elapsedRealtime();
        mSessionFinish = Calendar.getInstance().getTimeInMillis();
        mChronometer.stop();
    }

    public void resume() {
        mChronometer.setBase(SystemClock.elapsedRealtime() + mTimeWhenPaused);
        mChronometer.start();
    }

    public long getStartTime() {
        return mSessionStart;
    }

    public long getEndTime() {
        return mSessionFinish;
    }
}
