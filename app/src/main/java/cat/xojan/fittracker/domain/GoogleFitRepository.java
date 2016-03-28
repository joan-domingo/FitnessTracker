package cat.xojan.fittracker.domain;

import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Date;

public interface GoogleFitRepository {
    SessionReadResult readHistory(Date lastUpdate, GoogleApiClient googleApiClient);
}
