package com.example.metroapp_v1;

public class AllLocation {
    private double lon,lat;

    private String name;
        public AllLocation() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AllLocation(double lon, double lat, String name) {
        this.lon = lon;
        this.lat = lat;
        this.name = name;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLot(double lat) {
        this.lat = lat;
    }
}
