package cat.xojan.fittracker.session;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cat.xojan.fittracker.Constant;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.Utils;
import cat.xojan.fittracker.googlefit.FitnessController;

public class SessionFragment extends Fragment {

    private static Handler handler;
    private Button mDeleteSessionButton;
    private int mNumSegments;
    private List<DataPoint> mDistanceDataPoints;
    private List<DataPoint> mSpeedDataPoints;
    private List<DataPoint> mLocationDataPoints;

    public static Handler getHandler() {
        return handler;
    }
    private ProgressBar mProgressBar;
    private LinearLayout mSessionView;
    private Session mSession;
    private List<DataSet> mDataSets;

    private static View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_session, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }
        setHasOptionsMenu(true);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressBar = (ProgressBar) view.findViewById(R.id.fragment_loading_spinner);
        mSessionView = (LinearLayout) view.findViewById(R.id.fragment_session_container);
        mDeleteSessionButton = (Button) view.findViewById(R.id.fragment_button_delete_session);
        showProgressBar(true);
        /**
         * session contains:
         * name, description, identifier, package name, activity type, start time, end time
         */

        Bundle bundle = this.getArguments();
        String sessionId = bundle.getString(Constant.PARAMETER_SESSION_ID, "");
        long startTime = bundle.getLong(Constant.PARAMETER_START_TIME, 0);
        long endTime = bundle.getLong(Constant.PARAMETER_END_TIME, 0);

        FitnessController.getInstance().readSession(sessionId, startTime, endTime);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Constant.MESSAGE_SINGLE_SESSION_READ:
                        mSession = FitnessController.getInstance().getSingleSession();
                        mDataSets = FitnessController.getInstance().getSingleSessionDataSets();
                        fillViewContent(view);
                        break;
                    case Constant.MESSAGE_SESSION_DELETED:
                        showProgressBar(false);
                        getActivity().getSharedPreferences(Constant.PACKAGE_SPECIFIC_PART, Context.MODE_PRIVATE)
                                .edit().putBoolean(Constant.PARAMETER_RELOAD_LIST, true).apply();
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, new SessionListFragment())
                                .commit();
                }
            }
        };

        mDeleteSessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.delete_session)
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                FitnessController.getInstance().deleteSession(mSession);
                                showProgressBar(true);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                // Create the AlertDialog object and return it
                builder.create().show();
            }
        });

        return view;
    }

    private void fillViewContent(View view) {
        if (mSession.getAppPackageName().equals(Constant.PACKAGE_SPECIFIC_PART))
            mDeleteSessionButton.setVisibility(View.VISIBLE);
        else
            mDeleteSessionButton.setVisibility(View.GONE);

        ((TextView) view.findViewById(R.id.fragment_session_name)).setText(mSession.getName());
        ((TextView)view.findViewById(R.id.fragment_session_description)).setText(mSession.getDescription());
        ((TextView)view.findViewById(R.id.fragment_session_date)).setText(Utils.millisToDate(mSession.getStartTime(TimeUnit.MILLISECONDS)));
        ((TextView)view.findViewById(R.id.fragment_session_start)).setText(Utils.millisToTime(mSession.getStartTime(TimeUnit.MILLISECONDS)));
        ((TextView)view.findViewById(R.id.fragment_session_end)).setText(Utils.millisToTime(mSession.getEndTime(TimeUnit.MILLISECONDS)));
        ((TextView)view.findViewById(R.id.fragment_session_total_time)).setText(Utils.getTimeDifference(mSession.getEndTime(TimeUnit.MILLISECONDS), mSession.getStartTime(TimeUnit.MILLISECONDS)));

        mNumSegments = 0;
        mDistanceDataPoints = null;
        mSpeedDataPoints = null;
        mLocationDataPoints = null;

        for (DataSet ds : mDataSets) {
            if (ds.getDataType().equals(DataType.AGGREGATE_ACTIVITY_SUMMARY)) {
                if (ds.getDataPoints() != null && ds.getDataPoints().size() > 0) {
                    mNumSegments = ds.getDataPoints().get(0).getValue(Field.FIELD_NUM_SEGMENTS).asInt();
                }
            } else if (ds.getDataType().equals(DataType.AGGREGATE_SPEED_SUMMARY)) {
                if (ds.getDataPoints() != null && ds.getDataPoints().size() > 0) {

                    String speed = Utils.getRightSpeed(ds.getDataPoints().get(0).getValue(Field.FIELD_AVERAGE).asFloat(), getActivity().getBaseContext());
                    ((TextView) view.findViewById(R.id.fragment_session_total_speed)).setText(speed);

                    String pace = Utils.getRightPace(ds.getDataPoints().get(0).getValue(Field.FIELD_AVERAGE).asFloat(), getActivity().getBaseContext());
                    ((TextView) view.findViewById(R.id.fragment_session_total_pace)).setText(pace);
                }
            } else if (ds.getDataType().equals(DataType.TYPE_DISTANCE_DELTA)) {
                mDistanceDataPoints = ds.getDataPoints();
            } else if (ds.getDataType().equals(DataType.TYPE_SPEED)) {
                mSpeedDataPoints = ds.getDataPoints();
            } else if (ds.getDataType().equals(DataType.TYPE_LOCATION_SAMPLE)) {
                mLocationDataPoints = ds.getDataPoints();
            }
        }

        float totalDistance = 0;
        for (DataPoint dp : mDistanceDataPoints) {
            totalDistance = totalDistance + dp.getValue(Field.FIELD_DISTANCE).asFloat();
        }
        ((TextView)view.findViewById(R.id.fragment_session_total_distance)).setText(Utils.getRightDistance(totalDistance, getActivity()));

        if (mLocationDataPoints != null && mLocationDataPoints.size() > 0) {
            fillMap(true);
        } else {
            fillMap(false);
        }

        fillIntervalTable(view);

        showProgressBar(false);
    }

    private void fillMap(boolean fillMap) {
         MapFragment mapFragment = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.fragment_session_map));
        if (fillMap) {
            mapFragment.getView().setVisibility(View.VISIBLE);
            final GoogleMap map = mapFragment.getMap();
            map.clear();
            map.setMyLocationEnabled(false);
            map.getUiSettings().setZoomControlsEnabled(false);
            map.getUiSettings().setMyLocationButtonEnabled(false);

            LatLng oldPosition = null;
            final LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            float elevationGain = 0;
            float elevationLoss = 0;
            float oldAltitude = 0;

            for (DataPoint dp : mLocationDataPoints) {
                //elevation
                float currentAltitude = dp.getValue(Field.FIELD_ALTITUDE).asFloat();

                //position
                LatLng currentPosition = new LatLng(dp.getValue(Field.FIELD_LATITUDE).asFloat(), dp.getValue(Field.FIELD_LONGITUDE).asFloat());
                boundsBuilder.include(currentPosition);

                if (oldPosition != null) {
                    //create polyline with last location
                    map.addPolyline(new PolylineOptions()
                            .geodesic(true)
                            .add(oldPosition)
                            .add(currentPosition)
                            .width(4)
                            .color(Color.BLACK));

                    //estimate altitude gain/loss
                    float elevation = currentAltitude - oldAltitude;
                    if (elevation >= 0) {
                        elevationGain = elevationGain + elevation;
                    } else {
                        elevationLoss = elevationLoss + (-elevation);
                    }
                }
                oldPosition = currentPosition;
                oldAltitude = currentAltitude;
            }

            ((TextView)view.findViewById(R.id.fragment_session_total_elevation_gain)).setText(Utils.getRightElevation(elevationGain, getActivity()));
            ((TextView)view.findViewById(R.id.fragment_session_total_elevation_loss)).setText(Utils.getRightElevation(elevationLoss, getActivity()));

            map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    map.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 5));
                }
            });
        } else {
            mapFragment.getView().setVisibility(View.GONE);
        }
    }

    private void fillIntervalTable(View view) {
        LinearLayout intervalView = (LinearLayout) view.findViewById(R.id.fragment_session_intervals);
        intervalView.removeAllViews();

        if (mNumSegments < 2) {
            return;
        }

        for (int i = 0; i < mNumSegments; i++) {
            //1 - interval title
            TextView title = new TextView(getActivity());
            title.setText(getText(R.string.interval) + " " + (i + 1));
            title.setTextSize(20);
            title.setTypeface(Typeface.DEFAULT_BOLD);

            intervalView.addView(title);

            //2- headers
            TableLayout intervalTable = new TableLayout(getActivity());
            TableRow headersRow = new TableRow(getActivity());

            TextView timeHeader = new TextView(getActivity());
            timeHeader.setText(getText(R.string.time));
            timeHeader.setPadding(8, 0, 8, 0);
            timeHeader.setGravity(Gravity.CENTER);
            headersRow.addView(timeHeader);

            TextView distanceHeader = new TextView(getActivity());
            distanceHeader.setText(getText(R.string.distance));
            distanceHeader.setPadding(8, 0, 8, 0);
            distanceHeader.setGravity(Gravity.CENTER);
            headersRow.addView(distanceHeader);

            TextView paceHeader = new TextView(getActivity());
            paceHeader.setText(getText(R.string.pace));
            paceHeader.setPadding(8, 0, 8, 0);
            paceHeader.setGravity(Gravity.CENTER);
            headersRow.addView(paceHeader);

            TextView speedHeader = new TextView(getActivity());
            speedHeader.setText(getText(R.string.speed));
            speedHeader.setPadding(8, 0, 8, 0);
            speedHeader.setGravity(Gravity.CENTER);
            headersRow.addView(speedHeader);

            TextView startTimeHeader = new TextView(getActivity());
            startTimeHeader.setText(getText(R.string.start));
            startTimeHeader.setPadding(8, 0, 8, 0);
            startTimeHeader.setGravity(Gravity.CENTER);
            headersRow.addView(startTimeHeader);

            TextView endTimeHeader = new TextView(getActivity());
            endTimeHeader.setText(getText(R.string.end));
            endTimeHeader.setPadding(8, 0, 8, 0);
            endTimeHeader.setGravity(Gravity.CENTER);
            headersRow.addView(endTimeHeader);

            TextView elevationGainHeader = new TextView(getActivity());
            elevationGainHeader.setText(getText(R.string.elevation_gain));
            elevationGainHeader.setPadding(8, 0 , 8, 0);
            elevationGainHeader.setGravity(Gravity.CENTER);
            headersRow.addView(elevationGainHeader);

            TextView elevationLossHeader = new TextView(getActivity());
            elevationLossHeader.setText(getText(R.string.elevation_loss));
            elevationLossHeader.setPadding(8, 0 , 8, 0);
            elevationLossHeader.setGravity(Gravity.CENTER);
            headersRow.addView(elevationLossHeader);

            intervalTable.addView(headersRow);

            //3 - values
            TableRow valuesRow = new TableRow(getActivity());

            TextView timeValue = new TextView(getActivity());
            timeValue.setText(Utils.getTimeDifference(mDistanceDataPoints.get(i).getEndTime(TimeUnit.MILLISECONDS),
                    mDistanceDataPoints.get(i).getStartTime(TimeUnit.MILLISECONDS)));
            timeValue.setPadding(8, 0, 8, 0);
            timeValue.setGravity(Gravity.CENTER);
            timeValue.setTypeface(Typeface.DEFAULT_BOLD);
            valuesRow.addView(timeValue);

            TextView distanceValue = new TextView(getActivity());
            distanceValue.setText(Utils.getRightDistance(mDistanceDataPoints.get(i).getValue(Field.FIELD_DISTANCE).asFloat(), getActivity()));
            distanceValue.setPadding(8, 0, 8, 0);
            distanceValue.setGravity(Gravity.CENTER);
            distanceValue.setTypeface(Typeface.DEFAULT_BOLD);
            valuesRow.addView(distanceValue);

            TextView paceValue = new TextView(getActivity());
            paceValue.setText(Utils.getRightPace(mSpeedDataPoints.get(i).getValue(Field.FIELD_SPEED).asFloat(), getActivity()));
            paceValue.setPadding(8, 0, 8, 0);
            paceValue.setGravity(Gravity.CENTER);
            paceValue.setTypeface(Typeface.DEFAULT_BOLD);
            valuesRow.addView(paceValue);

            TextView speedValue = new TextView(getActivity());
            speedValue.setText(Utils.getRightSpeed(mSpeedDataPoints.get(i).getValue(Field.FIELD_SPEED).asFloat(), getActivity()));
            speedValue.setPadding(8, 0, 8, 0);
            speedValue.setGravity(Gravity.CENTER);
            speedValue.setTypeface(Typeface.DEFAULT_BOLD);
            valuesRow.addView(speedValue);

            TextView startTimeValue = new TextView(getActivity());
            startTimeValue.setText(Utils.millisToTime(mDistanceDataPoints.get(i).getStartTime(TimeUnit.MILLISECONDS)));
            startTimeValue.setPadding(8, 0, 8, 0);
            startTimeValue.setGravity(Gravity.CENTER);
            startTimeValue.setTypeface(Typeface.DEFAULT_BOLD);
            valuesRow.addView(startTimeValue);

            TextView endTimeValue = new TextView(getActivity());
            endTimeValue.setText(Utils.millisToTime(mDistanceDataPoints.get(i).getEndTime(TimeUnit.MILLISECONDS)));
            endTimeValue.setPadding(8, 0 , 8, 0);
            endTimeValue.setGravity(Gravity.CENTER);
            endTimeValue.setTypeface(Typeface.DEFAULT_BOLD);
            valuesRow.addView(endTimeValue);

            TextView elevationGainValue = new TextView(getActivity());
            elevationGainValue.setText(getSegmentElevationGain(mDistanceDataPoints.get(i).getStartTime(TimeUnit.MILLISECONDS), mDistanceDataPoints.get(i).getEndTime(TimeUnit.MILLISECONDS)));
            elevationGainValue.setPadding(8, 0 , 8, 0);
            elevationGainValue.setGravity(Gravity.CENTER);
            elevationGainValue.setTypeface(Typeface.DEFAULT_BOLD);
            valuesRow.addView(elevationGainValue);

            TextView elevationLossValue = new TextView(getActivity());
            elevationLossValue.setText(getSegmentElevationLoss(mDistanceDataPoints.get(i).getStartTime(TimeUnit.MILLISECONDS), mDistanceDataPoints.get(i).getEndTime(TimeUnit.MILLISECONDS)));
            elevationLossValue.setPadding(8, 0 , 8, 0);
            elevationLossValue.setGravity(Gravity.CENTER);
            elevationLossValue.setTypeface(Typeface.DEFAULT_BOLD);
            valuesRow.addView(elevationLossValue);

            intervalTable.addView(valuesRow);

            intervalView.addView(intervalTable);
        }
    }

    private String getSegmentElevationLoss(long startTime, long endTime) {
        boolean firsTime = true;
        float elevation = 0;
        float oldAltitude = 0;

        for (DataPoint dp : mLocationDataPoints) {
            if (dp.getStartTime(TimeUnit.MILLISECONDS) >= startTime && dp.getEndTime(TimeUnit.MILLISECONDS) <= endTime) {
                float currentAltitude = -dp.getValue(Field.FIELD_ALTITUDE).asFloat();
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

        for (DataPoint dp : mLocationDataPoints) {
            if (dp.getStartTime(TimeUnit.MILLISECONDS) >= startTime && dp.getEndTime(TimeUnit.MILLISECONDS) <= endTime) {
                float currentAltitude = -dp.getValue(Field.FIELD_ALTITUDE).asFloat();
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

    private void showProgressBar(boolean b) {
        if (b) {
            mProgressBar.setVisibility(View.VISIBLE);
            mSessionView.setVisibility(View.GONE);
        }
        else {
            mProgressBar.setVisibility(View.GONE);
            mSessionView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_settings);
        item.setEnabled(false);
        item.setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }
}
