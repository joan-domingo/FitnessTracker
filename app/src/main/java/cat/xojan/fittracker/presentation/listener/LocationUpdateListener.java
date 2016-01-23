package cat.xojan.fittracker.presentation.listener;

import android.location.Location;

public interface LocationUpdateListener {
    public void onFirstLocationUpdate(Location location);
    public void onLocationUpdate(Location location);
}
