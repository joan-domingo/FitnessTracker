package cat.xojan.fittracker.result;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.sql.Time;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cat.xojan.fittracker.Constant;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.Utils;
import cat.xojan.fittracker.googlefit.FitnessController;
import cat.xojan.fittracker.session.SessionListFragment;
import cat.xojan.fittracker.workout.DistanceController;
import cat.xojan.fittracker.workout.ElevationController;
import cat.xojan.fittracker.workout.MapController;
import cat.xojan.fittracker.workout.SpeedController;
import cat.xojan.fittracker.workout.TimeController;

public class ResultFragment extends Fragment {

    private GoogleMap mMap;
    private EditText mDescription;
    private EditText mName;
    private static View view;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_result, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }
        setHasOptionsMenu(true);
        showProgressBar(true);
        mMap = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.result_map)).getMap();

        Button save = (Button) view.findViewById(R.id.result_button_save);
        Button exit = (Button) view.findViewById(R.id.result_button_exit);
        mName = (EditText) view.findViewById(R.id.result_name);
        mDescription = (EditText) view.findViewById(R.id.result_description);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FitnessController.getInstance().saveSession(getActivity(), mName.getText().toString(), mDescription.getText().toString());
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new SessionListFragment())
                        .commit();
            }
        });

        initMap();
        setContent();

        new LocationReader(getActivity()) {

            public void onResult(String cityName) {
                if (!TextUtils.isEmpty(cityName)) {
                    mName.setText(Utils.millisToDay(TimeController.getInstance().getSessionEndTime()) + " " + getText(R.string.workout));
                    mDescription.setText(FitnessController.getInstance().getFitnessActivity() + " " + getText(R.string.workout) + " @ " + cityName);
                } else {
                    mName.setText(Utils.millisToDay(TimeController.getInstance().getSessionEndTime()) + " " + getText(R.string.workout));
                    mDescription.setText(FitnessController.getInstance().getFitnessActivity() + " " + getText(R.string.workout));
                }
                showProgressBar(false);
            }

        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, MapController.getInstance().getLastLocation());

        return view;
    }

    private void setContent() {
        ((TextView) view.findViewById(R.id.fragment_result_total_time)).setText(Utils.getTimeDifference(TimeController.getInstance().getSessionEndTime(),TimeController.getInstance().getSessionStartTime()));
        ((TextView) view.findViewById(R.id.fragment_result_start)).setText(Utils.millisToTime(TimeController.getInstance().getSessionStartTime()));
        ((TextView) view.findViewById(R.id.fragment_result_end)).setText(Utils.millisToTime(TimeController.getInstance().getSessionEndTime()));
        ((TextView) view.findViewById(R.id.fragment_result_total_distance)).setText(Utils.getRightDistance(DistanceController.getInstance().getSessionDistance(), getActivity()));
        ((TextView) view.findViewById(R.id.fragment_result_total_elevation_gain)).setText(Utils.getRightElevation(ElevationController.getInstance().getTotalElevationGain(), getActivity()));
        ((TextView) view.findViewById(R.id.fragment_result_total_elevation_loss)).setText(Utils.getRightElevation(ElevationController.getInstance().getTotalElevationLoss(), getActivity()));
        float speed = DistanceController.getInstance().getSessionDistance() / (TimeController.getInstance().getSessionTotalTime() / 1000);
        ((TextView) view.findViewById(R.id.fragment_result_total_pace)).setText(Utils.getRightPace(speed, getActivity()));
        ((TextView) view.findViewById(R.id.fragment_result_total_speed)).setText(Utils.getRightSpeed(speed, getActivity()));

        fillIntervalTable();
    }

    private void showProgressBar(boolean b) {
        if (b) {
            (view.findViewById(R.id.fragment_result_loading_spinner)).setVisibility(View.VISIBLE);
            (view.findViewById(R.id.fragment_result_content)).setVisibility(View.GONE);
        } else {
            (view.findViewById(R.id.fragment_result_content)).setVisibility(View.VISIBLE);
            (view.findViewById(R.id.fragment_result_loading_spinner)).setVisibility(View.GONE);
        }
    }

    private void initMap() {
        //init google map
        mMap.clear();
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        for (PolylineOptions plO : MapController.getInstance().getPolylines()) {
            mMap.addPolyline(plO);
        }

        for (MarkerOptions mO : MapController.getInstance().getMarkers()) {
            mMap.addMarker(mO);
        }


        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(MapController.getInstance().getBounds(), 40));
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menu.clear();
    }

    private void fillIntervalTable() {
        LinearLayout intervalView = (LinearLayout) view.findViewById(R.id.fragment_result_intervals);
        intervalView.removeAllViews();
        int numSegments = FitnessController.getInstance().getNumSegments();
        List<DataPoint> locationDataPoints = FitnessController.getInstance().getLocationDataPoints();
        List<DataPoint> distanceDataPoints = FitnessController.getInstance().getDistanceDataPoints();
        List<DataPoint> speedDataPoints = FitnessController.getInstance().getSpeedDataPoints();

        for (int i = 0; i < numSegments; i++) {
            //1 - interval title
            TextView title = new TextView(getActivity());
            title.setText(getText(R.string.interval) + " " + (i + 1));
            title.setTextSize(20);
            title.setTypeface(Typeface.DEFAULT_BOLD);

            intervalView.addView(title);

            //2- headers
            TableLayout intervalTable = new TableLayout(getActivity());
            TableRow headersRow = new TableRow(getActivity());

            headersRow.addView(createHeader(getText(R.string.time)));
            headersRow.addView(createHeader(getText(R.string.distance)));
            headersRow.addView(createHeader(getText(R.string.pace)));
            headersRow.addView(createHeader(getText(R.string.speed)));
            headersRow.addView(createHeader(getText(R.string.start)));
            headersRow.addView(createHeader(getText(R.string.end)));
            headersRow.addView(createHeader(getText(R.string.elevation_gain)));
            headersRow.addView(createHeader(getText(R.string.elevation_loss)));

            intervalTable.addView(headersRow);

            //3 - km intervals
            LatLng oldPosition = null;
            double distance = 0;
            int unitCounter = 1;
            long startTime = 0;
            float elevationGain = 0;
            float elevationLoss = 0;
            float oldAltitude = 0;
            String measureUnit = getActivity().getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE)
                    .getString(Constant.PREFERENCE_MEASURE_UNIT, "");

            for (DataPoint dp : locationDataPoints) {
                if (dp.getStartTime(TimeUnit.MILLISECONDS) >= distanceDataPoints.get(i).getStartTime(TimeUnit.MILLISECONDS) &&
                        dp.getEndTime(TimeUnit.MILLISECONDS) <= distanceDataPoints.get(i).getEndTime(TimeUnit.MILLISECONDS)) {
                    float currentAltitude = dp.getValue(Field.FIELD_ALTITUDE).asFloat();
                    LatLng currentPosition = new LatLng(dp.getValue(Field.FIELD_LATITUDE).asFloat(), dp.getValue(Field.FIELD_LONGITUDE).asFloat());
                    if (startTime == 0)
                        startTime = dp.getStartTime(TimeUnit.MILLISECONDS);
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
                                addRow(intervalTable, unitCounter + " " + Constant.DISTANCE_MEASURE_MILE, startTime,
                                        dp.getEndTime(TimeUnit.MILLISECONDS), elevationGain, elevationLoss);
                                unitCounter++;
                                startTime = dp.getEndTime(TimeUnit.MILLISECONDS);
                                elevationGain = elevationLoss = 0;
                            }
                        } else {
                            double kms = distance / 1000;
                            if (kms >= unitCounter) {
                                addRow(intervalTable, unitCounter + " " + Constant.DISTANCE_MEASURE_KM, startTime,
                                        dp.getEndTime(TimeUnit.MILLISECONDS), elevationGain, elevationLoss);
                                unitCounter++;
                                startTime = dp.getEndTime(TimeUnit.MILLISECONDS);
                                elevationGain = elevationLoss = 0;
                            }
                        }
                    }
                    oldPosition = currentPosition;
                    oldAltitude = currentAltitude;
                }
            }

            //4 - values
            TableRow valuesRow = new TableRow(getActivity());

            valuesRow.addView(createValue(Utils.getTimeDifference(distanceDataPoints.get(i).getEndTime(TimeUnit.MILLISECONDS),
                    distanceDataPoints.get(i).getStartTime(TimeUnit.MILLISECONDS))));
            valuesRow.addView(createValue(Utils.getRightDistance(distanceDataPoints.get(i).getValue(Field.FIELD_DISTANCE).asFloat(), getActivity())));
            valuesRow.addView(createValue(Utils.getRightPace(speedDataPoints.get(i).getValue(Field.FIELD_SPEED).asFloat(), getActivity())));
            valuesRow.addView(createValue(Utils.getRightSpeed(speedDataPoints.get(i).getValue(Field.FIELD_SPEED).asFloat(), getActivity())));
            valuesRow.addView(createValue(Utils.millisToTime(distanceDataPoints.get(i).getStartTime(TimeUnit.MILLISECONDS))));
            valuesRow.addView(createValue(Utils.millisToTime(distanceDataPoints.get(i).getEndTime(TimeUnit.MILLISECONDS))));
            valuesRow.addView(createValue(getSegmentElevationGain(distanceDataPoints.get(i).getStartTime(TimeUnit.MILLISECONDS),
                    distanceDataPoints.get(i).getEndTime(TimeUnit.MILLISECONDS))));
            valuesRow.addView(createValue(getSegmentElevationLoss(distanceDataPoints.get(i).getStartTime(TimeUnit.MILLISECONDS),
                    distanceDataPoints.get(i).getEndTime(TimeUnit.MILLISECONDS))));

            valuesRow.setBackgroundColor(getResources().getColor(R.color.grey));
            intervalTable.addView(valuesRow);

            //5 add table to view
            intervalView.addView(intervalTable);
        }
    }

    private void addRow(TableLayout intervalTable, String unitCounter, long startTime, long endTime, float elevationGain, float elevationLoss) {
        long timeInMillis = endTime - startTime;
        long timeInSeconds = timeInMillis / 1000;

        String measureUnit = getActivity().getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE)
                .getString(Constant.PREFERENCE_MEASURE_UNIT, "");
        double speed;
        if (measureUnit.equals(Constant.DISTANCE_MEASURE_MILE)) {
            speed = 1609.344 / timeInSeconds;
        } else {
            speed = 1000 / timeInSeconds;
        }

        TableRow row = new TableRow(getActivity());
        row.addView(createValue(Utils.getTimeDifference(endTime, startTime)));
        row.addView(createValue(String.valueOf(unitCounter)));
        row.addView(createValue(Utils.getRightPace((float) speed, getActivity())));
        row.addView(createValue(Utils.getRightSpeed((float) speed, getActivity())));
        row.addView(createValue(Utils.millisToTime(startTime)));
        row.addView(createValue(Utils.millisToTime(endTime)));
        row.addView(createValue(Utils.getRightElevation(elevationGain, getActivity())));
        row.addView(createValue(Utils.getRightElevation(elevationLoss, getActivity())));
        intervalTable.addView(row);
    }

    private View createHeader(CharSequence text) {
        TextView textView = new TextView(getActivity());
        textView.setText(text);
        textView.setPadding(10, 2, 10, 2);
        textView.setGravity(Gravity.CENTER);
        textView.setTypeface(Typeface.DEFAULT_BOLD);

        return textView;
    }

    private View createValue(String text) {
        TextView textView = new TextView(getActivity());
        textView.setText(text);
        textView.setPadding(10, 2, 10, 2);
        textView.setGravity(Gravity.CENTER);

        return textView;
    }

    private String getSegmentElevationLoss(long startTime, long endTime) {
        boolean firsTime = true;
        float elevation = 0;
        float oldAltitude = 0;

        for (DataPoint dp : FitnessController.getInstance().getLocationDataPoints()) {
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

        return Utils.getRightElevation(elevation, getActivity());
    }

    private String getSegmentElevationGain(long startTime, long endTime) {
        boolean firsTime = true;
        float elevation = 0;
        float oldAltitude = 0;

        for (DataPoint dp : FitnessController.getInstance().getLocationDataPoints()) {
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

        return Utils.getRightElevation(elevation, getActivity());
    }
}