package cat.xojan.fittracker.result;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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

import java.util.Map;

import cat.xojan.fittracker.R;
import cat.xojan.fittracker.googlefit.FitnessController;
import cat.xojan.fittracker.session.SessionListFragment;
import cat.xojan.fittracker.workout.DistanceController;
import cat.xojan.fittracker.workout.MapController;
import cat.xojan.fittracker.workout.TimeController;

public class ResultFragment extends Fragment {

    private GoogleMap mMap;
    private EditText mDescription;
    private EditText mName;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);

        mMap = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.result_map)).getMap();
        Button save = (Button) view.findViewById(R.id.result_button_save);
        Button exit = (Button) view.findViewById(R.id.result_button_exit);
        mName = (EditText) view.findViewById(R.id.result_name);
        mDescription = (EditText) view.findViewById(R.id.result_description);
        TextView totalTime = (TextView) view.findViewById(R.id.result_total_time);
        TextView totalSpeed = (TextView) view.findViewById(R.id.result_total_speed);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FitnessController.getInstance().setSessionData(mName.getText().toString(), mDescription.getText().toString());
                FitnessController.getInstance().saveSession(getActivity().getSupportFragmentManager());
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

        totalTime.setText(String.valueOf(TimeController.getInstance().getSessionTotalTime()));
        totalSpeed.setText(String.valueOf(DistanceController.getInstance().getSessionDistance()/TimeController.getInstance().getSessionTotalTime()/1000));

        initMap();

        return view;
    }

    private void initMap() {
        //init google map
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
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(MapController.getInstance().getBounds(), 0));
            }
        });
    }
}
