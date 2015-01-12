package cat.xojan.fittracker.result;

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
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import cat.xojan.fittracker.R;
import cat.xojan.fittracker.googlefit.FitnessController;
import cat.xojan.fittracker.session.SessionListFragment;
import cat.xojan.fittracker.util.SessionDataUtils;
import cat.xojan.fittracker.util.Utils;
import cat.xojan.fittracker.workout.DistanceController;
import cat.xojan.fittracker.workout.MapController;
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
                    mDescription.setText(FitnessController.getInstance().getFitnessActivity() + " @ " + cityName);
                } else {
                    mName.setText(Utils.millisToDay(TimeController.getInstance().getSessionEndTime()) + " " + getText(R.string.workout));
                    mDescription.setText(FitnessController.getInstance().getFitnessActivity());
                }
                showProgressBar(false);
            }

        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, MapController.getInstance().getLastPosition());

        return view;
    }

    private void setContent() {
        ((TextView) view.findViewById(R.id.fragment_result_total_time)).setText(Utils.millisToTime(TimeController.getInstance().getSessionWorkoutTime()));
        ((TextView) view.findViewById(R.id.fragment_result_start)).setText(Utils.millisToTime(TimeController.getInstance().getSessionStartTime()));
        ((TextView) view.findViewById(R.id.fragment_result_end)).setText(Utils.millisToTime(TimeController.getInstance().getSessionEndTime()));
        ((TextView) view.findViewById(R.id.fragment_result_total_distance)).setText(Utils.getRightDistance(DistanceController.getInstance().getSessionDistance(), getActivity()));
        float speed = DistanceController.getInstance().getSessionDistance() / (TimeController.getInstance().getSessionWorkoutTime() / 1000);
        ((TextView) view.findViewById(R.id.fragment_result_total_pace)).setText(Utils.getRightPace(speed, getActivity()));
        ((TextView) view.findViewById(R.id.fragment_result_total_speed)).setText(Utils.getRightSpeed(speed, getActivity()));

        SessionDataUtils.fillIntervalTable(view, getActivity(), FitnessController.getInstance().getNumSegments(), FitnessController.getInstance().getLocationDataPoints(),
                FitnessController.getInstance().getDistanceDataPoints(), FitnessController.getInstance().getSpeedDataPoints());
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
        mMap.setPadding(40, 80, 40, 0);
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
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(MapController.getInstance().getBounds(), 5));
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menu.clear();
    }
}