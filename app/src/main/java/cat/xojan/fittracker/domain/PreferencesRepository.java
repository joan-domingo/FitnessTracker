package cat.xojan.fittracker.domain;

import cat.xojan.fittracker.data.entity.DistanceUnit;

public interface PreferencesRepository {
    DistanceUnit getMeasureUnit();
    void setMeasureUnit(DistanceUnit distanceUnit);
}
