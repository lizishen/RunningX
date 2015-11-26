package com.votors.runningx;
//EarthQuake
/**
 * Created by Jason on 2015/11/26 0026.
 */
public class EarthQuakeRec {
    private double lat, lng, magnitude;

    protected EarthQuakeRec(double lat, double lng, double magnitude) {
        super();
        this.lat = lat;
        this.lng = lng;
        this.magnitude = magnitude;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public double getMagnitude() {
        return magnitude;
    }
}
