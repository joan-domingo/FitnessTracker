package cat.xojan.fittracker.util;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cat.xojan.fittracker.Constant;
import cat.xojan.fittracker.R;

public class SessionDetailedDataLoader extends AsyncTask<List<DataPoint>, Void, LinearLayout> {

    private final View mView;
    private final Context mContext;
    private final int mNumSegments;

    public SessionDetailedDataLoader(View view, Context context, int numSegments) {
        mView = view;
        mContext = context;
        mNumSegments = numSegments;
    }

    @Override
    protected LinearLayout doInBackground(List<DataPoint>... params) {
        if (params == null) {
            return null;
        }
        if (params[0] == null || params[1] == null || params[2] == null) {
            return null;
        }
        List<DataPoint> mLocationDataPoints = params[0];
        List<DataPoint> mDistanceDataPoints = params[1];
        List<DataPoint> mSpeedDataPoints = params[2];

        LinearLayout intervalView = new LinearLayout(mContext);
        intervalView.setOrientation(LinearLayout.VERTICAL);

        for (int i = 0; i < mNumSegments; i++) {
            //1 - interval title
            TextView title = new TextView(mContext);
            title.setText(mContext.getText(R.string.interval) + " " + (i + 1));
            title.setTextSize(20);
            title.setTypeface(Typeface.DEFAULT_BOLD);
            title.setTextColor(mContext.getResources().getColor(R.color.detail_data));

            intervalView.addView(title);

            //2- headers
            TableLayout intervalTable = new TableLayout(mContext);
            TableRow headersRow = new TableRow(mContext);

            headersRow.addView(createHeader(mContext, mContext.getText(R.string.time)));
            headersRow.addView(createHeader(mContext, mContext.getText(R.string.distance)));
            headersRow.addView(createHeader(mContext, mContext.getText(R.string.pace)));
            headersRow.addView(createHeader(mContext, mContext.getText(R.string.speed)));
            headersRow.addView(createHeader(mContext, mContext.getText(R.string.start_interval)));
            headersRow.addView(createHeader(mContext, mContext.getText(R.string.end_interval)));

            intervalTable.addView(headersRow);

            //3 - km intervals
            LatLng oldPosition = null;
            double distance = 0;
            double lastDistance = 0;
            int unitCounter = 1;
            long startTime = 0;
            long endTime = 0;
            String measureUnit = mContext.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE)
                    .getString(Constant.PREFERENCE_MEASURE_UNIT, "");

            for (DataPoint dp : mLocationDataPoints) {
                if (dp.getStartTime(TimeUnit.MILLISECONDS) >= mDistanceDataPoints.get(i).getStartTime(TimeUnit.MILLISECONDS) &&
                        dp.getStartTime(TimeUnit.MILLISECONDS) <= mDistanceDataPoints.get(i).getEndTime(TimeUnit.MILLISECONDS)) {

                    LatLng currentPosition = new LatLng(dp.getValue(Field.FIELD_LATITUDE).asFloat(), dp.getValue(Field.FIELD_LONGITUDE).asFloat());

                    if (oldPosition != null) {
                        distance = distance + SphericalUtil.computeDistanceBetween(oldPosition, currentPosition);
                        lastDistance = lastDistance + SphericalUtil.computeDistanceBetween(oldPosition, currentPosition);
                        endTime = dp.getEndTime(TimeUnit.MILLISECONDS);

                        if (measureUnit.equals(Constant.DISTANCE_MEASURE_MILE)) {
                            double miles = distance / 1609.344;
                            if (miles >= unitCounter) {
                                addRow(intervalTable, unitCounter + " " + mContext.getText(R.string.mi), startTime,
                                        dp.getEndTime(TimeUnit.MILLISECONDS), mContext);
                                unitCounter++;
                                lastDistance = 0;
                                startTime = dp.getEndTime(TimeUnit.MILLISECONDS);
                            }
                        } else {
                            double kms = distance / 1000;
                            if (kms >= unitCounter) {
                                addRow(intervalTable, unitCounter + " " + mContext.getText(R.string.km), startTime,
                                        dp.getEndTime(TimeUnit.MILLISECONDS), mContext);
                                unitCounter++;
                                lastDistance = 0;
                                startTime = dp.getEndTime(TimeUnit.MILLISECONDS);
                            }
                        }
                    } else {
                        startTime = dp.getStartTime(TimeUnit.MILLISECONDS);
                        endTime = dp.getEndTime(TimeUnit.MILLISECONDS);
                    }
                    oldPosition = currentPosition;
                }
            }
            //last value
            addLastDetailedRow(intervalTable, lastDistance, startTime, endTime, mContext);

            //4 - values
            TableRow valuesRow = new TableRow(mContext);

            valuesRow.addView(createValue(mContext, Utils.getTimeDifference(mDistanceDataPoints.get(i).getEndTime(TimeUnit.MILLISECONDS),
                    mDistanceDataPoints.get(i).getStartTime(TimeUnit.MILLISECONDS))));
            valuesRow.addView(createValue(mContext, Utils.getRightDistance(mDistanceDataPoints.get(i).getValue(Field.FIELD_DISTANCE).asFloat(), mContext)));
            valuesRow.addView(createValue(mContext, Utils.getRightPace(mSpeedDataPoints.get(i).getValue(Field.FIELD_SPEED).asFloat(), mContext)));
            valuesRow.addView(createValue(mContext, Utils.getRightSpeed(mSpeedDataPoints.get(i).getValue(Field.FIELD_SPEED).asFloat(), mContext)));
            valuesRow.addView(createValue(mContext, Utils.millisToTime(mDistanceDataPoints.get(i).getStartTime(TimeUnit.MILLISECONDS))));
            valuesRow.addView(createValue(mContext, Utils.millisToTime(endTime)));

            valuesRow.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
            intervalTable.addView(valuesRow);

            //5 add table to view
            intervalView.addView(intervalTable);
        }

        return intervalView;
    }

    private static void addRow(TableLayout intervalTable, String unitCounter, long startTime, long endTime, Context context) {
        float seconds = endTime - startTime;
        seconds = seconds / 1000;

        String measureUnit = context.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE)
                .getString(Constant.PREFERENCE_MEASURE_UNIT, "");
        double speed;
        if (measureUnit.equals(Constant.DISTANCE_MEASURE_MILE)) {
            speed = 1609.344 / seconds;
        } else {
            speed = 1000 / seconds;
        }

        TableRow row = new TableRow(context);
        row.addView(createValue(context, Utils.getTimeDifference(endTime, startTime)));
        row.addView(createValue(context, String.valueOf(unitCounter)));
        row.addView(createValue(context, Utils.getRightPace((float) speed, context)));
        row.addView(createValue(context, Utils.getRightSpeed((float) speed, context)));
        row.addView(createValue(context, Utils.millisToTime(startTime)));
        row.addView(createValue(context, Utils.millisToTime(endTime)));
        intervalTable.addView(row);
    }

    private static void addLastDetailedRow(TableLayout intervalTable, double distance, long startTime, long endTime, Context context) {
        float seconds = endTime - startTime;
        seconds = seconds / 1000;
        double speed = distance / seconds;

        TableRow row = new TableRow(context);
        row.addView(createValue(context, Utils.getTimeDifference(endTime, startTime)));
        row.addView(createValue(context, Utils.getRightDistance((float) distance, context)));
        row.addView(createValue(context, Utils.getRightPace((float) speed, context)));
        row.addView(createValue(context, Utils.getRightSpeed((float) speed, context)));
        row.addView(createValue(context, Utils.millisToTime(startTime)));
        row.addView(createValue(context, Utils.millisToTime(endTime)));
        intervalTable.addView(row);
    }

    private static View createValue(Context context, String text) {
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setPadding(10, 2, 10, 2);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(context.getResources().getColor(R.color.detail_data));

        return textView;
    }

    private static View createHeader(Context context, CharSequence text) {
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setPadding(10, 2, 10, 2);
        textView.setGravity(Gravity.CENTER);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setTextColor(context.getResources().getColor(R.color.detail_data));

        return textView;
    }

    @Override
    protected void onPostExecute(LinearLayout intervalView) {
        LinearLayout detailedDataView = (LinearLayout) mView.findViewById(R.id.session_intervals);
        detailedDataView.removeAllViews();

        detailedDataView.addView(intervalView);
    }
}
