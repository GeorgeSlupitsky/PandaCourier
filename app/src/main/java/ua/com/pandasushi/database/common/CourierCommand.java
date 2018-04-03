package ua.com.pandasushi.database.common;

import java.io.Serializable;
import java.util.Date;

import ua.com.pandasushi.database.common.gps.models.Track;

public class CourierCommand implements Serializable {
    private Integer courierId;
    private CourierOrder order;
    private Track track;
    private Commands command;
    private String password;
    private Double odometer;
    private Double extraTrip;
    private Integer shiftId;
    private Date timeOfClose;
    private byte [] photo;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Double getOdometer() {
        return odometer;
    }

    public void setOdometer(Double odometer) {
        this.odometer = odometer;
    }

    public Integer getCourierId() {
        return courierId;
    }

    public void setCourierId(Integer courierId) {
        this.courierId = courierId;
    }

    public Commands getCommand() {
        return command;
    }

    public void setCommand(Commands command) {
        this.command = command;
    }

    public CourierOrder getOrder() {
        return order;
    }

    public void setOrder(CourierOrder order) {
        this.order = order;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public Double getExtraTrip() {
        return extraTrip;
    }

    public void setExtraTrip(Double extraTrip) {
        this.extraTrip = extraTrip;
    }

    public Integer getShiftId() {
        return shiftId;
    }

    public void setShiftId(Integer shiftId) {
        this.shiftId = shiftId;
    }

    public Date getTimeOfClose() {
        return timeOfClose;
    }

    public void setTimeOfClose(Date timeOfClose) {
        this.timeOfClose = timeOfClose;
    }
}
