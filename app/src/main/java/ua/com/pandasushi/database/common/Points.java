package ua.com.pandasushi.database.common;

import java.io.Serializable;

public class Points implements Serializable {
    private Double longitude;
    private Double latitude;

    public Points(Double lat, Double lon){
        this.latitude=lat;
        this.longitude=lon;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
}
