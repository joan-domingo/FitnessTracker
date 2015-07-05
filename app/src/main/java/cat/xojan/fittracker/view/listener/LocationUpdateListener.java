package cat.xojan.fittracker.view.listener;

import android.location.Location;

public interface LocationUpdateListener {
    public void onFirstLocationUpdate(Location location);
    public void onLocationUpdate(Location location);
}
