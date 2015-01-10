package cat.xojan.fittracker.session;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.datatype.DatatypeConstants;

import cat.xojan.fittracker.Constant;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.googlefit.FitnessController;
import cat.xojan.fittracker.util.SessionDataUtils;
import cat.xojan.fittracker.util.Utils;

public class SessionFragment extends Fragment {

    private static Handler handler;
    private int mNumSegments;
    private List<DataPoint> mDistanceDataPoints;
    private List<DataPoint> mSpeedDataPoints;
    private List<DataPoint> mLocationDataPoints;
    private MenuItem mDeleteButton;

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
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.fragment_session_toolbar);
        ((ActionBarActivity) getActivity()).setSupportActionBar(toolbar);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressBar = (ProgressBar) view.findViewById(R.id.fragment_loading_spinner);
        mSessionView = (LinearLayout) view.findViewById(R.id.fragment_session_container);
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
                        if (mSession.getAppPackageName().equals(Constant.PACKAGE_SPECIFIC_PART))
                            mDeleteButton.setVisible(true);

                        mDataSets = FitnessController.getInstance().getSingleSessionDataSets();
                        fillViewContent(view);
                        break;
                    case Constant.MESSAGE_SESSION_DELETED:
                        showProgressBar(false);
                        getActivity().getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE)
                                .edit().putBoolean(Constant.PARAMETER_RELOAD_LIST, true).apply();
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, new SessionListFragment())
                                .commit();
                }
            }
        };

        return view;
    }

    private void fillViewContent(View view) {

        ((TextView) view.findViewById(R.id.fragment_session_name)).setText(mSession.getName());
        ((TextView)view.findViewById(R.id.fragment_session_description)).setText(mSession.getDescription());
        ((TextView)view.findViewById(R.id.fragment_session_date)).setText(Utils.getRightDate(mSession.getStartTime(TimeUnit.MILLISECONDS), getActivity()));
        ((TextView)view.findViewById(R.id.fragment_session_start)).setText(Utils.millisToTime(mSession.getStartTime(TimeUnit.MILLISECONDS)));
        ((TextView)view.findViewById(R.id.fragment_session_end)).setText(Utils.millisToTime(mSession.getEndTime(TimeUnit.MILLISECONDS)));
        ((TextView)view.findViewById(R.id.fragment_session_total_time)).setText(Utils.getTimeDifference(mSession.getEndTime(TimeUnit.MILLISECONDS), mSession.getStartTime(TimeUnit.MILLISECONDS)));

        ((TextView) view.findViewById(R.id.fragment_session_total_speed)).setText(Utils.getRightSpeed(0, getActivity()));
        ((TextView) view.findViewById(R.id.fragment_session_total_pace)).setText(Utils.getRightPace(0, getActivity()));

        ((TextView)view.findViewById(R.id.fragment_session_total_elevation_gain)).setText(Utils.getRightElevation(0, getActivity()));
        ((TextView)view.findViewById(R.id.fragment_session_total_elevation_loss)).setText(Utils.getRightElevation(0, getActivity()));

        mNumSegments = 0;
        mDistanceDataPoints = null;
        mSpeedDataPoints = null;
        mLocationDataPoints = null;

        for (DataSet ds : mDataSets) {
            if (ds.getDataType().equals(DataType.AGGREGATE_ACTIVITY_SUMMARY)) {
                if (ds.getDataPoints() != null && ds.getDataPoints().size() > 0) {
                    mNumSegments = ds.getDataPoints().get(0).getValue(Field.FIELD_NUM_SEGMENTS).asInt();
                }
            }/* else if (ds.getDataType().equals(DataType.AGGREGATE_SPEED_SUMMARY)) {
                if (ds.getDataPoints() != null && ds.getDataPoints().size() > 0) {

                    String speed = Utils.getRightSpeed(ds.getDataPoints().get(0).getValue(Field.FIELD_AVERAGE).asFloat(), getActivity().getBaseContext());
                    ((TextView) view.findViewById(R.id.fragment_session_total_speed)).setText(speed);

                    String pace = Utils.getRightPace(ds.getDataPoints().get(0).getValue(Field.FIELD_AVERAGE).asFloat(), getActivity().getBaseContext());
                    ((TextView) view.findViewById(R.id.fragment_session_total_pace)).setText(pace);
                }
            } */else if (ds.getDataType().equals(DataType.TYPE_DISTANCE_DELTA)) {
                mDistanceDataPoints = ds.getDataPoints();
//                FitnessController.getInstance().dumpDataSet(ds);
            } else if (ds.getDataType().equals(DataType.TYPE_SPEED)) {
                mSpeedDataPoints = ds.getDataPoints();
            } else if (ds.getDataType().equals(DataType.TYPE_LOCATION_SAMPLE)) {
                mLocationDataPoints = ds.getDataPoints();
                //FitnessController.getInstance().dumpDataSet(ds);
            }
        }

        float totalDistance = 0;
        for (DataPoint dp : mDistanceDataPoints) {
            totalDistance = totalDistance + dp.getValue(Field.FIELD_DISTANCE).asFloat();
        }
        ((TextView)view.findViewById(R.id.fragment_session_total_distance)).setText(Utils.getRightDistance(totalDistance, getActivity()));
        long totalTime = (mSession.getEndTime(TimeUnit.MILLISECONDS) - mSession.getStartTime(TimeUnit.MILLISECONDS)) / 1000;
        float speed = totalDistance / totalTime;
        ((TextView) view.findViewById(R.id.fragment_session_total_speed)).setText(Utils.getRightSpeed(speed, getActivity()));
        ((TextView) view.findViewById(R.id.fragment_session_total_pace)).setText(Utils.getRightPace(speed, getActivity()));

        if (mLocationDataPoints != null && mLocationDataPoints.size() > 0) {
            fillMap(true);
        } else {
            fillMap(false);
        }

        SessionDataUtils.fillIntervalTable(view, getActivity(), mNumSegments, mLocationDataPoints, mDistanceDataPoints, mSpeedDataPoints);

        showProgressBar(false);
    }

    private void fillMap(boolean fillMap) {
         MapFragment mapFragment = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.fragment_session_map));
        if (fillMap) {
            mapFragment.getView().setVisibility(View.VISIBLE);
            final GoogleMap map = mapFragment.getMap();
            map.clear();
            map.setPadding(40, 80, 40, 0);
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

            if (mLocationDataPoints.size() > 0) {
                //add start marker
                map.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        .position(new LatLng( mLocationDataPoints.get(0).getValue(Field.FIELD_LATITUDE).asFloat(),
                                mLocationDataPoints.get(0).getValue(Field.FIELD_LONGITUDE).asFloat())));

                //add end marker
                map.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        .position(new LatLng( mLocationDataPoints.get(mLocationDataPoints.size()-1).getValue(Field.FIELD_LATITUDE).asFloat(),
                                mLocationDataPoints.get(mLocationDataPoints.size()-1).getValue(Field.FIELD_LONGITUDE).asFloat())));
            }

            ((TextView) view.findViewById(R.id.fragment_session_total_elevation_gain)).setText(Utils.getRightElevation(elevationGain, getActivity()));
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
        super.onPrepareOptionsMenu(menu);

        MenuItem settings = menu.findItem(R.id.action_settings);
        settings.setVisible(false);
        mDeleteButton = menu.findItem(R.id.action_delete);
        mDeleteButton.setVisible(false);
        MenuItem attributions = menu.findItem(R.id.action_attributions);
        attributions.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_delete:
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
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
