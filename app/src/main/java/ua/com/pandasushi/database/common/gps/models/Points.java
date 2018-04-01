package ua.com.pandasushi.database.common.gps.models;

import java.io.Serializable;

/**
 * Created by oleg on 13.06.16.
 */
public class Points implements Serializable{

    private long pointId;

    private String lat;

    private String lon;

    private Track track;

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

    public long getPointId() {
        return pointId;
    }

    public void setPointId(long pointId) {
        this.pointId = pointId;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    @Override
    public String toString() {
        return "["+lat+","+lon+"]";
    }
}