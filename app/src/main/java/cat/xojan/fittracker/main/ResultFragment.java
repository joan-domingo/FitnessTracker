package cat.xojan.fittracker.main;

import android.app.AlertDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cat.xojan.fittracker.BaseFragment;
import cat.xojan.fittracker.Constant;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.main.controllers.DistanceController;
import cat.xojan.fittracker.main.controllers.FitnessController;
import cat.xojan.fittracker.main.controllers.MapController;
import cat.xojan.fittracker.main.controllers.TimeController;
import cat.xojan.fittracker.util.SessionDetailedData;
import cat.xojan.fittracker.util.SessionMapData;
import cat.xojan.fittracker.util.Utils;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ResultFragment extends BaseFragment {

    @Inject FitnessController fitController;
    @Inject MapController mapController;
    @Inject DistanceController distanceController;
    @Inject TimeController timeController;
    @Inject Context context;

    @Bind(R.id.result_description) EditText mDescription;
    @Bind(R.id.result_name) EditText mName;

    private static View view;
    private GoogleMap map;
    private double totalDistance;

    @OnClick(R.id.result_button_save)
    public void onClickSave(Button save) {
        fitController.saveSession(getActivity(), mName.getText().toString(),
                mDescription.getText().toString(), totalDistance);
    }

    @OnClick(R.id.result_button_exit)
    public void onClickExit(Button exit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.save_activity)
                .setPositiveButton(R.string.exit, (dialog, id) -> getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new SessionListFragment())
                        .commit())
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    // User cancelled the dialog
                });
        // Create the AlertDialog object and return it
        builder.create().show();
    }

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
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        showProgressDialog(true);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContent();
    }

    private void setContent() {

        Observable.just(context)
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Context>() {

                    private LinearLayout intervalView;
                    private double distance;

                    @Override
                    public void onCompleted() {
                        Observable.just("")
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(result -> {
                                    showDetailedData(intervalView, totalDistance);
                                });
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(Constant.TAG, "Error getting Datapoints: set detailed data");
                    }

                    @Override
                    public void onNext(Context context) {
                        SessionDetailedData detailedData = new SessionDetailedData(context);
                        detailedData.readDetailedData(fitController.getLocationDataPoints(),
                                fitController.getSegmentDataPoints());
                        intervalView = detailedData.getIntervalView();
                        totalDistance = detailedData.getTotalDistance();
                    }
                });
    }

    private void showDetailedData(LinearLayout intervalView, double distance) {
        if (intervalView != null) {
            LinearLayout detailedView = (LinearLayout) view.findViewById(R.id.session_intervals);
            detailedView.removeAllViews();
            detailedView.addView(intervalView);
            totalDistance = distance;
        } else {
            totalDistance = distanceController.getSessionDistance();
        }
        float speed = (float) (totalDistance / (timeController.getSessionWorkoutTime() / 1000));

        ((TextView) view.findViewById(R.id.fragment_result_total_time)).setText(Utils.getTimeDifference(timeController.getSessionWorkoutTime(), 0));
        ((TextView) view.findViewById(R.id.fragment_result_start)).setText(Utils.millisToTime(timeController.getSessionStartTime()));
        ((TextView) view.findViewById(R.id.fragment_result_end)).setText(Utils.millisToTime(timeController.getSessionEndTime()));
        ((TextView) view.findViewById(R.id.fragment_result_total_distance)).setText(Utils.getRightDistance((float) totalDistance, getActivity()));

        ((TextView) view.findViewById(R.id.fragment_result_total_pace)).setText(Utils.getRightPace(speed, getActivity()));
        ((TextView) view.findViewById(R.id.fragment_result_total_speed)).setText(Utils.getRightSpeed(speed, getActivity()));

        Observable.just(mapController.getLastPosition())
                .subscribeOn(Schedulers.newThread())
                .subscribe(pos -> {
                            String cityName = null;
                            Geocoder gcd = new Geocoder(getActivity().getBaseContext(), Locale.ENGLISH);
                            List<Address> addresses;
                            try {
                                addresses = gcd.getFromLocation(pos.latitude, pos.longitude, 1);
                                if (addresses != null && addresses.size() > 0)
                                    cityName = addresses.get(0).getLocality();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Observable.just(cityName)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(cn -> {
                                        mName.setText(getText(R.string.workout) + " " +
                                                Utils.millisToDay(timeController.getSessionEndTime()));
                                        if (!TextUtils.isEmpty(cn)) {
                                            mDescription.setText(getText(ActivityType
                                                    .getRightLanguageString(fitController
                                                            .getFitnessActivity())) + " @ " + cn);
                                        } else {
                                            mDescription.setText(getText(ActivityType
                                                    .getRightLanguageString(fitController
                                                            .getFitnessActivity())));
                                        }
                                        showProgressDialog(false);
                                        setMap();
                                    });
                        }
                );
    }

    private void showProgressDialog(boolean b) {
        if (b) {
            //((MainActivity) getActivity()).showDialog();
        } else {
            //((MainActivity) getActivity()).dismissDialog();
        }
    }

    private void setMap() {
        //init google map
        MapFragment mapFragment = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.result_map));

        mapFragment.getMapAsync(googleMap -> {
            map = googleMap;
            map.clear();
            map.setPadding(40, 80, 40, 0);
            map.setMyLocationEnabled(false);
            map.getUiSettings().setZoomControlsEnabled(false);
            map.getUiSettings().setMyLocationButtonEnabled(false);

            Observable.just(map)
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Subscriber<GoogleMap>() {
                        private SessionMapData mapData;

                        @Override
                        public void onCompleted() {
                            Observable.just(map)
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(result -> {
                                        mapData.setDataIntoMap(result, mapData);
                                        showProgressDialog(false);
                                    });
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(Constant.TAG, "Error getting Datapoints: set map polylines");
                        }

                        @Override
                        public void onNext(GoogleMap googleMap) {
                            mapData = new SessionMapData();
                            mapData.readMapData(fitController.getSegmentDataPoints(), fitController.getLocationDataPoints());
                        }
                    });
        });
    }

        @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menu.clear();
    }
}