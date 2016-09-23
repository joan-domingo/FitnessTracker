package cat.xojan.fittracker.presentation.sessiondetails;

import android.graphics.Color;
import android.location.Location;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import cat.xojan.fittracker.data.entity.DistanceUnit;
import cat.xojan.fittracker.data.entity.Workout;
import cat.xojan.fittracker.domain.interactor.UnitDataInteractor;
import cat.xojan.fittracker.domain.interactor.WorkoutInteractor;
import cat.xojan.fittracker.presentation.BasePresenter;
import cat.xojan.fittracker.util.Utils;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Presenter for {@link SessionDetailsActivity}.
 */
public class SessionDetailsPresenter implements BasePresenter {

    private final UnitDataInteractor mUnitDataInteractor;
    private final WorkoutInteractor mWorkoutInteractor;
    private ViewListener mListener;
    private LatLngBounds.Builder mBoundsBuilder;

    interface ViewListener {

        void updateData(Workout workout);

        void onWorkoutDeleted();
    }

    public SessionDetailsPresenter(UnitDataInteractor unitDataInteractor,
                                   WorkoutInteractor workoutInteractor) {
        mUnitDataInteractor = unitDataInteractor;
        mWorkoutInteractor = workoutInteractor;
        mBoundsBuilder = new LatLngBounds.Builder();
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {
        mListener = null;
    }

    public void listenToUpdates(ViewListener listener) {
        mListener = listener;
    }

    public void loadSessionData(long workoutId) {
        mWorkoutInteractor.loadWorkout(workoutId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Workout>() {
                    @Override
                    public void call(Workout workout) {
                        mListener.updateData(workout);
                    }
                });
    }

    public void deleteSession(Workout workout) {
        mWorkoutInteractor.deleteWorkout(workout)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        mListener.onWorkoutDeleted();
                    }
                });
    }

    public void paintMap(GoogleMap map, List<Location> locations) {
        if (locations.size() != 0) {
            if (locations.size() == 1) {
                addStartMarker(locations.get(0), map);
                addPositionToBoundsBuilder(locations.get(0));
            } else {
                addStartMarker(locations.get(0), map);
                addPositionToBoundsBuilder(locations.get(0));
                Location oldLocation = locations.get(0);
                for (int i = 1; i < locations.size(); i++) {
                    addPolyline(oldLocation, locations.get(i), map);
                    oldLocation = locations.get(i);
                    addPositionToBoundsBuilder(locations.get(i));
                }
                addFinishMarker(locations.get(locations.size() - 1), map);
            }
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(mBoundsBuilder.build(), 0));
        }
    }

    public void setDistanceTextView(final TextView view, final Long distance) {
        mUnitDataInteractor.getDistanceUnit()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<DistanceUnit>() {
                    @Override
                    public void call(DistanceUnit distanceUnit) {
                        view.setText(Utils.formatDistance(distance, distanceUnit));
                    }
                });
    }

    private void addPositionToBoundsBuilder(Location location) {
        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
        mBoundsBuilder.include(position);
    }

    private void addFinishMarker(Location location, GoogleMap map) {
        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
        map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(position));
    }

    private void addPolyline(Location oldLocation, Location newLocation, GoogleMap map) {
        LatLng oldPosition = new LatLng(oldLocation.getLatitude(), oldLocation.getLongitude());
        LatLng newPosition = new LatLng(newLocation.getLatitude(), newLocation.getLongitude());
        map.addPolyline(new PolylineOptions()
                .geodesic(true)
                .add(oldPosition)
                .add(newPosition)
                .width(6)
                .color(Color.BLACK));
    }

    private void addStartMarker(Location location, GoogleMap map) {
        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
        map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .position(position));
    }
}
