package ua.com.pandasushi.database.common.gps.models;

import java.io.Serializable;
import java.util.List;

import ua.com.pandasushi.database.common.CourierOrder;

/**
 * Created by oleg on 09.06.16.
 */
public class Track implements Serializable {

    private Long trackId;

    private String maxSpeed;

    private String averageSpeed;

    private String averageAltitude;

    private String maxAltitude;

    private String trackLenght;

    private String timeStart;

    private String timeStop;

    private int pointsOnTrack;

    private Integer courierId;

    private List<Points> points;

    private List<AVLData> avlDataList;

    private List<CourierOrder> orders;

    public Track() {
    }

    public Long getTrackId() {
        return trackId;
    }

    public void setTrackId(Long trackId) {
        this.trackId = trackId;
    }

    public int getPointsOnTrack() {
        return pointsOnTrack;
    }

    public void setPointsOnTrack(int pointsOnTrack) {
        this.pointsOnTrack = pointsOnTrack;
    }

    public List<Points> getPoints() {
        return points;
    }

    public void setPoints(List<Points> points) {
        this.points = points;
    }

    public String getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(String maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public String getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(String averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public String getAverageAltitude() {
        return averageAltitude;
    }

    public void setAverageAltitude(String averageAltitude) {
        this.averageAltitude = averageAltitude;
    }

    public String getMaxAltitude() {
        return maxAltitude;
    }

    public void setMaxAltitude(String maxAltitude) {
        this.maxAltitude = maxAltitude;
    }

    public String getTrackLenght() {
        return trackLenght;
    }

    public void setTrackLenght(String trackLenght) {
        this.trackLenght = trackLenght;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeStop() {
        return timeStop;
    }

    public void setTimeStop(String timeStop) {
        this.timeStop = timeStop;
    }

    public List<AVLData> getAvlDataList() {
        return avlDataList;
    }

    public void setAvlDataList(List<AVLData> avlDataList) {
        this.avlDataList = avlDataList;
    }

    public List<CourierOrder> getOrders() {
        return orders;
    }

    public void setOrders(List<CourierOrder> orders) {
        this.orders = orders;
    }

    public Integer getCourierId() {
        return courierId;
    }

    public void setCourierId(Integer courierId) {
        this.courierId = courierId;
    }

}
