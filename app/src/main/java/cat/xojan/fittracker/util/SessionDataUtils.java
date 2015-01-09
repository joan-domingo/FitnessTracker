package cat.xojan.fittracker.util;

import android.content.Context;
import android.graphics.Typeface;
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

public class SessionDataUtils {

    public static void fillIntervalTable(View view, Context context, int numSegments, List<DataPoint> mLocationDataPoints,
                                         List<DataPoint> mDistanceDataPoints, List<DataPoint> mSpeedDataPoints) {
        LinearLayout intervalView = (LinearLayout) view.findViewById(R.id.session_intervals);
        intervalView.removeAllViews();

        for (int i = 0; i < numSegments; i++) {
            //1 - interval title
            TextView title = new TextView(context);
            title.setText(context.getText(R.string.interval) + " " + (i + 1));
            title.setTextSize(20);
            title.setTypeface(Typeface.DEFAULT_BOLD);

            intervalView.addView(title);

            //2- headers
            TableLayout intervalTable = new TableLayout(context);
            TableRow headersRow = new TableRow(context);

            headersRow.addView(createHeader(context, context.getText(R.string.time)));
            headersRow.addView(createHeader(context, context.getText(R.string.distance)));
            headersRow.addView(createHeader(context, context.getText(R.string.pace)));
            headersRow.addView(createHeader(context, context.getText(R.string.speed)));
            headersRow.addView(createHeader(context, context.getText(R.string.elevation_gain)));
            headersRow.addView(createHeader(context, context.getText(R.string.elevation_loss)));
            headersRow.addView(createHeader(context, context.getText(R.string.start)));
            headersRow.addView(createHeader(context, context.getText(R.string.end)));

            intervalTable.addView(headersRow);

            //3 - km intervals
            LatLng oldPosition = null;
            double distance = 0;
            int unitCounter = 1;
            long startTime = 0;
            float elevationGain = 0;
            float elevationLoss = 0;
            float oldAltitude = 0;
            String measureUnit = context.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE)
                    .getString(Constant.PREFERENCE_MEASURE_UNIT, "");

            for (DataPoint dp : mLocationDataPoints) {
                if (dp.getStartTime(TimeUnit.MILLISECONDS) >= mDistanceDataPoints.get(i).getStartTime(TimeUnit.MILLISECONDS) &&
                        dp.getStartTime(TimeUnit.MILLISECONDS) <= mDistanceDataPoints.get(i).getEndTime(TimeUnit.MILLISECONDS)) {

                    float currentAltitude = dp.getValue(Field.FIELD_ALTITUDE).asFloat();
                    LatLng currentPosition = new LatLng(dp.getValue(Field.FIELD_LATITUDE).asFloat(), dp.getValue(Field.FIELD_LONGITUDE).asFloat());

                    if (oldPosition != null) {
                        distance = distance + SphericalUtil.computeDistanceBetween(oldPosition, currentPosition);
                        float elevation = currentAltitude - oldAltitude;
                        if (elevation >= 0) {
                            elevationGain = elevationGain + elevation;
                        } else {
                            elevationLoss = elevationLoss + (-elevation);
                        }

                        if (measureUnit.equals(Constant.DISTANCE_MEASURE_MILE)) {
                            double miles = distance / 1609.344;
                            if (miles >= unitCounter) {
                                addRow(intervalTable, unitCounter + " " + context.getText(R.string.mi), startTime,
                                        dp.getEndTime(TimeUnit.MILLISECONDS), elevationGain, elevationLoss, context);
                                unitCounter++;
                                startTime = dp.getEndTime(TimeUnit.MILLISECONDS);
                                elevationGain = elevationLoss = 0;
                            }
                        } else {
                            double kms = distance / 1000;
                            if (kms >= unitCounter) {
                                addRow(intervalTable, unitCounter + " " + context.getText(R.string.km), startTime,
                                        dp.getEndTime(TimeUnit.MILLISECONDS), elevationGain, elevationLoss, context);
                                unitCounter++;
                                startTime = dp.getEndTime(TimeUnit.MILLISECONDS);
                                elevationGain = elevationLoss = 0;
                            }
                        }
                    } else {
                        startTime = dp.getStartTime(TimeUnit.MILLISECONDS);
                    }
                    oldPosition = currentPosition;
                    oldAltitude = currentAltitude;
                }
            }

            //4 - values
            TableRow valuesRow = new TableRow(context);

            valuesRow.addView(createValue(context, Utils.getTimeDifference(mDistanceDataPoints.get(i).getEndTime(TimeUnit.MILLISECONDS),
                    mDistanceDataPoints.get(i).getStartTime(TimeUnit.MILLISECONDS))));
            valuesRow.addView(createValue(context, Utils.getRightDistance(mDistanceDataPoints.get(i).getValue(Field.FIELD_DISTANCE).asFloat(), context)));
            valuesRow.addView(createValue(context, Utils.getRightPace(mSpeedDataPoints.get(i).getValue(Field.FIELD_SPEED).asFloat(), context)));
            valuesRow.addView(createValue(context, Utils.getRightSpeed(mSpeedDataPoints.get(i).getValue(Field.FIELD_SPEED).asFloat(), context)));
            valuesRow.addView(createValue(context, getSegmentElevationGain(mDistanceDataPoints.get(i).getStartTime(TimeUnit.MILLISECONDS),
                    mDistanceDataPoints.get(i).getEndTime(TimeUnit.MILLISECONDS), context, mLocationDataPoints)));
            valuesRow.addView(createValue(context, getSegmentElevationLoss(mDistanceDataPoints.get(i).getStartTime(TimeUnit.MILLISECONDS),
                    mDistanceDataPoints.get(i).getEndTime(TimeUnit.MILLISECONDS), context, mLocationDataPoints)));
            valuesRow.addView(createValue(context, Utils.millisToTime(mDistanceDataPoints.get(i).getStartTime(TimeUnit.MILLISECONDS))));
            valuesRow.addView(createValue(context, Utils.millisToTime(mDistanceDataPoints.get(i).getEndTime(TimeUnit.MILLISECONDS))));

            valuesRow.setBackgroundColor(context.getResources().getColor(R.color.grey));
            intervalTable.addView(valuesRow);

            //5 add table to view
            intervalView.addView(intervalTable);
        }
    }

    private static void addRow(TableLayout intervalTable, String unitCounter, long startTime, long endTime, float elevationGain, float elevationLoss,
                               Context context) {
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
        row.addView(createValue(context, Utils.getRightElevation(elevationGain, context)));
        row.addView(createValue(context, Utils.getRightElevation(elevationLoss, context)));
        row.addView(createValue(context, Utils.millisToTime(startTime)));
        row.addView(createValue(context, Utils.millisToTime(endTime)));
        intervalTable.addView(row);
    }

    private static View createValue(Context context, String text) {
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setPadding(10, 2, 10, 2);
        textView.setGravity(Gravity.CENTER);
//        textView.setTypeface(Typeface.DEFAULT_BOLD);

        return textView;
    }

    private static View createHeader(Context context, CharSequence text) {
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setPadding(10, 2, 10, 2);
        textView.setGravity(Gravity.CENTER);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setTypeface(Typeface.DEFAULT_BOLD);

        return textView;
    }

    private static String getSegmentElevationLoss(long startTime, long endTime, Context context, List<DataPoint> mLocationDataPoints) {
        boolean firsTime = true;
        float elevation = 0;
        float oldAltitude = 0;

        for (DataPoint dp : mLocationDataPoints) {
            if (dp.getStartTime(TimeUnit.MILLISECONDS) >= startTime && dp.getEndTime(TimeUnit.MILLISECONDS) <= endTime) {
                float currentAltitude = dp.getValue(Field.FIELD_ALTITUDE).asFloat();
                if (firsTime) {
                    firsTime = false;
                } else {
                    if (currentAltitude - oldAltitude < 0) {
                        elevation = elevation + (-(currentAltitude - oldAltitude));
                    }
                }
                oldAltitude = currentAltitude;
            }
        }

        return Utils.getRightElevation(elevation, context);
    }

    private static String getSegmentElevationGain(long startTime, long endTime, Context context, List<DataPoint> mLocationDataPoints) {
        boolean firsTime = true;
        float elevation = 0;
        float oldAltitude = 0;

        for (DataPoint dp : mLocationDataPoints) {
            if (dp.getStartTime(TimeUnit.MILLISECONDS) >= startTime && dp.getEndTime(TimeUnit.MILLISECONDS) <= endTime) {
                float currentAltitude = dp.getValue(Field.FIELD_ALTITUDE).asFloat();
                if (firsTime) {
                    firsTime = false;
                } else {
                    if (currentAltitude - oldAltitude >= 0) {
                        elevation = elevation + (currentAltitude - oldAltitude);
                    }
                }
                oldAltitude = currentAltitude;
            }
        }

        return Utils.getRightElevation(elevation, context);
    }
}
