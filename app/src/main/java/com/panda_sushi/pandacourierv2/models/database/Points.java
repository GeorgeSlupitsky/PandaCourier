package com.panda_sushi.pandacourierv2.models.database;

/**
 * Created by oleg on 13.06.16.
 */
public class Points{
    private String lat;
    private String lon;

    public Points(String lat, String lon){
        this.lat=lat;
        this.lon=lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    @Override
    public String toString() {
        return "["+lat+","+lon+"]";
    }
}