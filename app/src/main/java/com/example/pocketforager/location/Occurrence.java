package com.example.pocketforager.location;

public class Occurrence {
    private double decimalLatitude;
    private double decimalLongitude;

    public Occurrence(double lat, double lon) {
        this.decimalLatitude = lat;
        this.decimalLongitude = lon;
    }

    public double getLatitude() {
        return decimalLatitude;
    }

    public double getLongitude() {
        return decimalLongitude;
    }
}
