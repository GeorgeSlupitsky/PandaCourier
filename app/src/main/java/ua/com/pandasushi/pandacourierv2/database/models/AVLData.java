package ua.com.pandasushi.pandacourierv2.database.models;



public class AVLData {

    private long timestamp;
    private double longitude;
    private double latitude;
    private int altitude;
    private int angle;
    private int satellites;
    private int speed;

    private double heading;
    private double hdop;


    public AVLData() {
    }
    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public long getTimestamp() {
        long ts = timestamp;
        if (ts > 9999999999L) {
            ts = ts / 1000L; // dirty hack for teltonika
        }
        return ts;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double d) {
        this.longitude = d;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getAltitude() {
        return altitude;
    }

    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public int getSatellites() {
        return satellites;
    }

    public void setSatellites(int satellites) {
        this.satellites = satellites;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }


    /**
     * @return the hdop
     */
    public double getHdop() {
        return hdop;
    }

    /**
     * @param hdop the hdop to set
     */
    public void setHdop(double hdop) {
        this.hdop = hdop;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AVLData{");
        sb.append(", timestamp=").append(timestamp);
        sb.append(", longitude=").append(longitude);
        sb.append(", latitude=").append(latitude);
        sb.append(", altitude=").append(altitude);
        sb.append(", angle=").append(angle);
        sb.append(", satellites=").append(satellites);
        sb.append(", speed=").append(speed);
        sb.append(", hdop=").append(hdop);
        sb.append('}');
        return sb.toString();
    }
}
