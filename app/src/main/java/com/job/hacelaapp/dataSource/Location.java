package com.job.hacelaapp.dataSource;

import com.google.firebase.firestore.GeoPoint;

/**
 * Created by Job on Wednesday : 5/2/2018.
 */
public class Location {
    private GeoPoint geopoint;
    private String place;

    public Location(GeoPoint geopoint, String place) {
        this.geopoint = geopoint;
        this.place = place;
    }

    public GeoPoint getGeopoint() {
        return geopoint;
    }

    public void setGeopoint(GeoPoint geopoint) {
        this.geopoint = geopoint;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    @Override
    public String toString() {
        return "Location{" +
                "geopoint=" + geopoint +
                ", place='" + place + '\'' +
                '}';
    }
}
