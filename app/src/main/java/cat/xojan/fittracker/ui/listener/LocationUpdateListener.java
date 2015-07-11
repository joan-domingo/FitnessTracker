package cat.xojan.fittracker.ui.listener;

import android.location.Location;

public interface LocationUpdateListener {
    public void onFirstLocationUpdate(Location location);
    public void onLocationUpdate(Location location);
}
