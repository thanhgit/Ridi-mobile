package saveteam.com.ridi.model;

import com.google.firebase.database.IgnoreExtraProperties;

import saveteam.com.ridi.firebase.model.TripFB;

@IgnoreExtraProperties
public class Query {
    private String key;
    private TripFB trip;

    public Query() {
    }

    public Query(String key, TripFB trip) {
        this.key = key;
        this.trip = trip;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public TripFB getTrip() {
        return trip;
    }

    public void setTrip(TripFB trip) {
        this.trip = trip;
    }
}
