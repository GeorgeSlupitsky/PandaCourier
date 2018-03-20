package com.panda_sushi.pandacourierv2.models.database;

import android.graphics.Point;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oleg on 09.06.16.
 */
public class Track implements Serializable {
    private String name;
    private String time;
    private String maxSpeed;
    private String averageSpeed;
    private String averageAltitude;
    private String maxAltitude;
    private String trackLenght;
    private String timeStart;
    private String timeStop;
    private int pointsOnTrack;
    private List<AVLData> avlDataList;
    private ArrayList<PressedSensor> sensors;
    private List<Points> points;
    private ArrayList<Point> chartPoits;

    public int getPointsOnTrack() {
        return pointsOnTrack;
    }

    public void setPointsOnTrack(int pointsOnTrack) {
        this.pointsOnTrack = pointsOnTrack;
    }


    public ArrayList<Point> getChartPoits() {
        return chartPoits;
    }

    public void setChartPoits(ArrayList<Point> chartPoits) {
        this.chartPoits = chartPoits;
    }


    public ArrayList<Point> getAltitudeChart() {
        return altitudeChart;
    }

    public void setAltitudeChart(ArrayList<Point> altitudeChart) {
        this.altitudeChart = altitudeChart;
    }

    private ArrayList<Point> altitudeChart;

    public List<Points> getPoints() {
        return points;
    }

    public void setPoints(List<Points> points) {
        this.points = points;
    }

    public ArrayList<PressedSensor> getSensors() {
        return sensors;
    }

    public void setSensors(ArrayList<PressedSensor> sensors) {
        this.sensors = sensors;
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


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<AVLData> getAvlDataList() {
        return avlDataList;
    }

    public void setAvlDataList(List<AVLData> avlDataList) {
        this.avlDataList = avlDataList;
    }


}
