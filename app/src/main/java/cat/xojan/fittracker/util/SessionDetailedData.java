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

import cat.xojan.fittracker.R;
import cat.xojan.fittracker.presentation.controller.DistanceController;
import cat.xojan.fittracker.presentation.presenter.UnitDataPresenter;

public class SessionDetailedData {

    private final UnitDataPresenter mUnitDataPresenter;
    private LinearLayout intervalView;
    private double mTotalDistance;
    private Context mContext;

    public SessionDetailedData(Context context, UnitDataPresenter unitDataPresenter) {
        mContext = context;
        mTotalDistance = 0;
        intervalView = new LinearLayout(mContext);
        mUnitDataPresenter = unitDataPresenter;
    }

    public LinearLayout getIntervalView() {
        return this.intervalView;
    }

    public double getTotalDistance() {
        return mTotalDistance;
    }

    public void readDetailedData(List<DataPoint> mLocationDataPoints, List<DataPoint> mSegmentDataPoints) {
        mTotalDistance = 0;
        intervalView = new LinearLayout(mContext);
        intervalView.setOrientation(LinearLayout.VERTICAL);

        for (int i = 0; i < mSegmentDataPoints.size(); i++) {
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

            headersRow.addView(createHeader(mContext, ""));
            headersRow.addView(createHeader(mContext, mContext.getText(R.string.time)));
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
            String measureUnit = mUnitDataPresenter.getMeasureUnit(mContext);

            for (DataPoint dp : mLocationDataPoints) {
                if (dp.getStartTime(TimeUnit.MILLISECONDS) >= mSegmentDataPoints.get(i).getStartTime(TimeUnit.MILLISECONDS) &&
                        dp.getStartTime(TimeUnit.MILLISECONDS) <= mSegmentDataPoints.get(i).getEndTime(TimeUnit.MILLISECONDS)) {

                    LatLng currentPosition = new LatLng(dp.getValue(Field.FIELD_LATITUDE).asFloat(), dp.getValue(Field.FIELD_LONGITUDE).asFloat());

                    if (oldPosition != null) {
                        distance = distance + SphericalUtil.computeDistanceBetween(oldPosition, currentPosition);
                        lastDistance = lastDistance + SphericalUtil.computeDistanceBetween(oldPosition, currentPosition);

                        if (measureUnit.equals(DistanceController.DISTANCE_MEASURE_MILE)) {
                            double miles = distance / 1609.344;
                            if (miles >= unitCounter) {
                                addRow(intervalTable, mContext.getText(R.string.mi) + " " + unitCounter, startTime,
                                        dp.getEndTime(TimeUnit.MILLISECONDS), mContext);
                                unitCounter++;
                                lastDistance = 0;
                                startTime = dp.getEndTime(TimeUnit.MILLISECONDS);
                            }
                        } else {
                            double kms = distance / 1000;
                            if (kms >= unitCounter) {
                                addRow(intervalTable, mContext.getText(R.string.km) + " " + unitCounter, startTime,
                                        dp.getEndTime(TimeUnit.MILLISECONDS), mContext);
                                unitCounter++;
                                lastDistance = 0;
                                startTime = dp.getEndTime(TimeUnit.MILLISECONDS);
                            }
                        }
                    } else {
                        startTime = dp.getStartTime(TimeUnit.MILLISECONDS);
                    }
                    oldPosition = currentPosition;
                }
            }
            //last value
            if (unitCounter > 1)
                addLastDetailedRow(intervalTable, lastDistance, startTime, mSegmentDataPoints.get(i).getEndTime(TimeUnit.MILLISECONDS), mContext);


            //4 - values
            double totalDistance = getTotalDistance(unitCounter - 1, lastDistance, mContext);
            mTotalDistance = mTotalDistance + totalDistance;
            TableRow valuesRow = new TableRow(mContext);
            valuesRow.addView(createValue(mContext, Utils.getRightDistance((float) totalDistance, mContext)));
            valuesRow.addView(createValue(mContext, Utils.getTimeDifference(mSegmentDataPoints.get(i).getEndTime(TimeUnit.MILLISECONDS),
                    mSegmentDataPoints.get(i).getStartTime(TimeUnit.MILLISECONDS))));
            double speed = totalDistance / ((mSegmentDataPoints.get(i).getEndTime(TimeUnit.MILLISECONDS) -
                    mSegmentDataPoints.get(i).getStartTime(TimeUnit.MILLISECONDS))/1000);
            valuesRow.addView(createValue(mContext, Utils.getRightPace((float) speed, mContext)));
            valuesRow.addView(createValue(mContext, Utils.getRightSpeed((float) speed, mContext)));
            valuesRow.addView(createValue(mContext, Utils.millisToTime(mSegmentDataPoints.get(i).getStartTime(TimeUnit.MILLISECONDS))));
            valuesRow.addView(createValue(mContext, Utils.millisToTime(mSegmentDataPoints.get(i).getEndTime(TimeUnit.MILLISECONDS))));

            valuesRow.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
            intervalTable.addView(valuesRow);

            //5 add table to view
            intervalView.addView(intervalTable);
        }
    }

    private double getTotalDistance(int unitCounter, double lastDistance, Context context) {
        String measureUnit = mUnitDataPresenter.getMeasureUnit(context);
        if (measureUnit.equals(DistanceController.DISTANCE_MEASURE_MILE)) {
            return ((1609.344 * unitCounter) + lastDistance);
        } else {
            return ((1000 * unitCounter) + lastDistance);
        }
    }

    private void addRow(TableLayout intervalTable, String unitCounter, long startTime, long endTime, Context context) {
        float seconds = endTime - startTime;
        seconds = seconds / 1000;

        String measureUnit = mUnitDataPresenter.getMeasureUnit(context);
        double speed;
        if (measureUnit.equals(DistanceController.DISTANCE_MEASURE_MILE)) {
            speed = 1609.344 / seconds;
        } else {
            speed = 1000 / seconds;
        }

        TableRow row = new TableRow(context);
        row.addView(createValue(context, String.valueOf(unitCounter)));
        row.addView(createValue(context, Utils.getTimeDifference(endTime, startTime)));
        row.addView(createValue(context, Utils.getRightPace((float) speed, context)));
        row.addView(createValue(context, Utils.getRightSpeed((float) speed, context)));
        row.addView(createValue(context, Utils.millisToTime(startTime)));
        row.addView(createValue(context, Utils.millisToTime(endTime)));
        intervalTable.addView(row);
    }

    private void addLastDetailedRow(TableLayout intervalTable, double distance, long startTime, long endTime, Context context) {
        float seconds = endTime - startTime;
        seconds = seconds / 1000;
        double speed = distance / seconds;

        TableRow row = new TableRow(context);
        row.addView(createValue(context, Utils.getRightDistance((float) distance, context)));
        row.addView(createValue(context, Utils.getTimeDifference(endTime, startTime)));
        row.addView(createValue(context, Utils.getRightPace((float) speed, context)));
        row.addView(createValue(context, Utils.getRightSpeed((float) speed, context)));
        row.addView(createValue(context, Utils.millisToTime(startTime)));
        row.addView(createValue(context, Utils.millisToTime(endTime)));
        intervalTable.addView(row);
    }

    private View createValue(Context context, String text) {
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setPadding(10, 2, 10, 2);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(context.getResources().getColor(R.color.detail_data));

        return textView;
    }

    private View createHeader(Context context, CharSequence text) {
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setPadding(10, 2, 10, 2);
        textView.setGravity(Gravity.CENTER);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setTextColor(context.getResources().getColor(R.color.detail_data));

        return textView;
    }
}
