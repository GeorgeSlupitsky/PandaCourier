package ua.com.pandasushi.pandacourierv2.database.models;

/**
 * Created by oleg on 10.06.16.
 */
public class PressedSensor {
    private String name;
    private String date;
    private int number;
    private Points points;

    public PressedSensor(String name, String date, Points p, int num){
        this.name=name;
        this.date=date;
        this.points=p;
        this.number =num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Points getPoints() {
        return points;
    }

    public void setPoints(Points points) {
        this.points = points;
    }
}
