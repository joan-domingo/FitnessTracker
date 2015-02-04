package cat.xojan.fittracker.result;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import cat.xojan.fittracker.ActivityType;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.googlefit.FitnessController;
import cat.xojan.fittracker.session.MapLoader;
import cat.xojan.fittracker.sessionlist.SessionListFragment;
import cat.xojan.fittracker.util.SessionDetailedDataLoader;
import cat.xojan.fittracker.util.Utils;
import cat.xojan.fittracker.workout.DistanceController;
import cat.xojan.fittracker.workout.MapController;
import cat.xojan.fittracker.workout.TimeController;

public class ResultFragment extends Fragment {

    private EditText mDescription;
    private EditText mName;
    private static View view;
    private GoogleMap map;

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
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.save_activity)
                        .setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.fragment_container, new SessionListFragment())
                                        .commit();
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

        setContent();

        return view;
    }

    private void setContent() {

        new SessionDetailedDataLoader(getActivity().getBaseContext()) {
            public void onResult(LinearLayout intervalView, double totalDistance) {
                if (intervalView != null) {
                    LinearLayout detailedView = (LinearLayout) view.findViewById(R.id.session_intervals);
                    detailedView.removeAllViews();
                    detailedView.addView(intervalView);
                } else {
                    totalDistance = DistanceController.getInstance().getSessionDistance();
                }
                float speed = (float) (totalDistance / (TimeController.getInstance().getSessionWorkoutTime() / 1000));

                ((TextView) view.findViewById(R.id.fragment_result_total_time)).setText(Utils.getTimeDifference(TimeController.getInstance().getSessionWorkoutTime(), 0));
                ((TextView) view.findViewById(R.id.fragment_result_start)).setText(Utils.millisToTime(TimeController.getInstance().getSessionStartTime()));
                ((TextView) view.findViewById(R.id.fragment_result_end)).setText(Utils.millisToTime(TimeController.getInstance().getSessionEndTime()));
                ((TextView) view.findViewById(R.id.fragment_result_total_distance)).setText(Utils.getRightDistance((float) totalDistance, getActivity()));

                ((TextView) view.findViewById(R.id.fragment_result_total_pace)).setText(Utils.getRightPace(speed, getActivity()));
                ((TextView) view.findViewById(R.id.fragment_result_total_speed)).setText(Utils.getRightSpeed(speed, getActivity()));

                new LocationReader(getActivity()) {

                    public void onResult(String cityName) {
                        mName.setText(getText(R.string.workout) + " " + Utils.millisToDay(TimeController.getInstance().getSessionEndTime()));
                        if (!TextUtils.isEmpty(cityName)) {
                            mDescription.setText(getText(ActivityType.getRightLanguageString(FitnessController.getInstance().getFitnessActivity())) + " @ " + cityName);
                        } else {
                            mDescription.setText(getText(ActivityType.getRightLanguageString(FitnessController.getInstance().getFitnessActivity())));
                        }
                        showProgressBar(false);
                        setMap();
                    }

                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, MapController.getInstance().getLastPosition());

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                        FitnessController.getInstance().getLocationDataPoints(),
                        FitnessController.getInstance().getSegmentDataPoints());
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

    private void setMap() {
        //init google map
        MapFragment mapFragment = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.result_map));

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                map.clear();
                map.setPadding(40, 80, 40, 0);
                map.setMyLocationEnabled(false);
                map.getUiSettings().setZoomControlsEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
            }
        });

        new MapLoader(map) {
            public void onResult(final List<PolylineOptions> mPolyList, final List<MarkerOptions> mMarkerList, final LatLngBounds.Builder mBoundsBuilder) {
                map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        map.moveCamera(CameraUpdateFactory.newLatLngBounds(mBoundsBuilder.build(), 5));

                        for (PolylineOptions pl : mPolyList ) {
                            map.addPolyline(pl
                                    .geodesic(true)
                                    .width(6)
                                    .color(Color.BLACK));
                        }
                        for (MarkerOptions mo : mMarkerList) {
                            map.addMarker(mo);
                        }
                    }
                });
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                FitnessController.getInstance().getLocationDataPoints(),
                FitnessController.getInstance().getSegmentDataPoints());
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menu.clear();
    }
}