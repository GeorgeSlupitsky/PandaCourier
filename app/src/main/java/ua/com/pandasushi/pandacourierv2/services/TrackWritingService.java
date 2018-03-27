package ua.com.pandasushi.pandacourierv2.services;

import android.Manifest;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.gson.Gson;

import ua.com.pandasushi.pandacourierv2.DBHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import ua.com.pandasushi.database.common.gps.models.AVLData;
import ua.com.pandasushi.database.common.gps.models.Points;
import ua.com.pandasushi.database.common.gps.models.Track;

/**
 * Created by postp on 18.03.2018.
 */

public class TrackWritingService extends Service implements LocationListener {

    private Handler handler;

    private static Location mLastLocation;
    private static Location mPreviousLocation;

    private Double lat, lon;
    private LocationManager mLocationManager;
    private static String gprmc, gpgga;
    private static int pointsOnTrack, timerCount;
    private ArrayList<AVLData> lisAvldata = new ArrayList<>();
    public static Double currentLat, currentLon;
    private boolean isStart = false;
    private double lenght, fullLenght;
    private ArrayList<Points> pointsList = new ArrayList<>();
    private ArrayList<Double> points = new ArrayList<>();
    private DBHelper dbHelper;
    private String timeStart, timeStop;
    private Gson gson;
    public static String lenghtOfTrack;

    Runnable tracking = new Runnable() {
        public void run() {

            try {
                AVLData avlData = parse(gprmc, gpgga);
                if (gpgga == null & Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mLastLocation = getLastKnownLocation();
                    if (mLastLocation != null) {
                        avlData = parseAvl(mLastLocation);
                    }
                }

                int minspeed = 3;
                int maxspeed = 180;

                if (avlData.getLongitude() != 0.0 & avlData.getSpeed() >= minspeed & avlData.getSpeed() <= maxspeed) {
                    if (points.size() == 0) {
                        points.add(avlData.getLatitude());
                        points.add(avlData.getLongitude());
                        points.add(0.0);

                    }
                    isStart = true;

                    timerCount = 0;
                    pointsList.add(new Points("" + avlData.getLatitude(), "" + avlData.getLongitude()));
                    pointsOnTrack++;
                    lisAvldata.add(avlData);

                    points.set(2, (double) avlData.getHeading());
                    pointsList.add(new Points("" + avlData.getLatitude(), "" + avlData.getLongitude()));
                    pointsOnTrack++;
                    lisAvldata.add(avlData);


                    Location loc1 = new Location("");
                    loc1.setLatitude(points.get(0));
                    loc1.setLongitude(points.get(1));
                    Location loc2 = new Location("");
                    loc2.setLatitude(avlData.getLatitude());
                    loc2.setLongitude(avlData.getLongitude());

                    points.set(0, avlData.getLatitude());
                    points.set(1, avlData.getLongitude());
                    pointsList.add(new Points("" + avlData.getLatitude(), "" + avlData.getLongitude()));
                    pointsOnTrack++;
                    lisAvldata.add(avlData);

                    if (pointsList.size() > 2) {
                        lenght = 0;
                        for (int i = 0; i < pointsList.size() - 1; i++) {
                            Location location1 = new Location("");
                            location1.setLatitude(Double.parseDouble(pointsList.get(i).getLat()));
                            location1.setLongitude(Double.parseDouble(pointsList.get(i).getLon()));
                            Location location2 = new Location("");
                            location2.setLatitude(Double.parseDouble(pointsList.get(i + 1).getLat()));
                            location2.setLongitude(Double.parseDouble(pointsList.get(i + 1).getLon()));
                            int meters = (int) location1.distanceTo(location2);
                            lenght += meters;
                        }
                    }
                } else {
                    isStart = false;
                }


                currentLat = avlData.getLatitude();
                currentLon = avlData.getLongitude();

            } catch (Exception x) {
                Log.i("run Exeption", x.getMessage());
            }
            fullLenght = lenght;
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        handler = new Handler();
        dbHelper = new DBHelper(this);
        gson = new Gson();

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (mLocationManager != null) {
            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);
            }
        }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        mLocationManager.addNmeaListener(new GpsStatus.NmeaListener() {
            public void onNmeaReceived(long timestamp, String nmea) {
                if (nmea.startsWith("$GPGGA")) {
                    sendGpgga(nmea);
                }
                if (nmea.startsWith("$GPRMC")) {
                    sendGprmc(nmea);
                }

            }
        });

        handler.post(tracking);
        timeStart = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pointsList.size() >= 2) {
            timeStop = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            ContentValues cv = new ContentValues();
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String name = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
            cv.put("name", name);
            Track track = new Track();
            track.setName(name);
            track.setTimeStart(timeStart);
            track.setTimeStop(timeStop);
            track.setPoints(pointsList);
            ArrayList<Integer> speedList = new ArrayList<>();
            ArrayList<Integer> altitudeList = new ArrayList<>();
            int speed = 0, altitude = 0;
            for (AVLData avlData : lisAvldata) {
                speedList.add(avlData.getSpeed());
                altitudeList.add(avlData.getAltitude());
                speed += avlData.getSpeed();
                altitude += avlData.getAltitude();
            }
            Collections.sort(speedList);
            Collections.sort(altitudeList);
            if (altitudeList.size() != 0) {
                track.setMaxAltitude(String.valueOf(altitudeList.get(altitudeList.size() - 1)) + " m");
                track.setAverageSpeed(String.valueOf(speed / speedList.size()) + " km/h");
            }
            if (speedList.size() != 0) {
                track.setMaxSpeed(String.valueOf(speedList.get(speedList.size() - 1)) + " km/h");
                track.setAverageAltitude(String.valueOf(altitude / altitudeList.size()) + " m");
            }

            track.setAvlDataList(lisAvldata);
            track.setPointsOnTrack(pointsOnTrack);
            track.setTrackLenght(String.valueOf(fullLenght));
            lenghtOfTrack = String.valueOf(fullLenght);
            cv.put("track", gson.toJson(track).getBytes());
            db.insert("trackdata", null, cv);
        }

        handler.removeCallbacks(tracking);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public AVLData parseAvl(Location location) {
        AVLData avlData = new AVLData();
        avlData.setSpeed((int) location.getSpeed());
        avlData.setLatitude(location.getLatitude());
        avlData.setLongitude(location.getLongitude());
        avlData.setHeading(location.getBearing());
        avlData.setAltitude((int) location.getAltitude());
        return avlData;
    }

    public AVLData parse(String gprmc, String gpgga) {

        AVLData avl = new AVLData();

        try {

            //GPGGA,142436.000,4948.793629,N,02403.732753,E,1,6,1.96,351.018,M,36.946,M,,*53
            String[] gpggaArr = gpgga.split(",");

            String[] gprs = gprmc.split(",");
            boolean validGPS = gprs[2].equalsIgnoreCase("A");

            double latitude = validGPS ? _parseLatitude(gprs[3], gprs[4]) : 0.0;
            double longitude = validGPS ? _parseLongitude(gprs[5], gprs[6]) : 0.0;

            double speedKPH = validGPS ? Double.parseDouble(gprs[7]) * 1.852 : 0.0;
            if (!gprs[8].equals("")) {
                double heading = validGPS ? (gprs[8] != null ? Double.parseDouble(gprs[8]) : 0.0) : 0.0;
                avl.setHeading((int) heading);

            }

            if (!gpggaArr[7].equals("")) {
                int satellites = validGPS ? Integer.parseInt(gpggaArr[7]) : 0;
                avl.setSatellites(satellites);
            }

            if (!gpggaArr[8].equals("")) {
                double HDOP = validGPS ? Double.parseDouble(gpggaArr[8]) : 0.0;
                avl.setHdop(HDOP);

            }
            if (!gpggaArr[9].equals("")) {
                double altitude = validGPS ? Double.parseDouble(gpggaArr[9]) : 0.0;
                avl.setAltitude((int) altitude);

            }
            avl.setLatitude(latitude);
            avl.setLongitude(longitude);
            avl.setSpeed((int) speedKPH);
            return avl;

        } catch (Exception e) {
            Log.i("Exception", e.getMessage());
            avl.setHdop(500);
        }
        return avl;
    }


    private double _parseLatitude(String s, String d) {
        Double _lat = Double.parseDouble(s);
        if (_lat < 99999.0) {
            double lat = (double) (_lat.longValue() / 100L); // _lat is always positive here
            lat += (_lat - (lat * 100.0)) / 60.0;
            return d.equals("S") ? -lat : lat;
        } else {
            return 90.0; // invalid latitude
        }
    }


    private double _parseLongitude(String s, String d) {
        Double _lon = Double.parseDouble(s);
        if (_lon < 99999.0) {
            double lon = (double) (_lon.longValue() / 100L); // _lon is always positive here
            lon += (_lon - (lon * 100.0)) / 60.0;
            return d.equals("wa") ? -lon : lon;
        } else {
            return 180.0; // invalid longitude
        }
    }

    private Location getLastKnownLocation() {
        List<String> providers = mLocationManager.getProviders(true);
        Log.i(providers.toString(), "                 ");
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return null;
            }
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    private String sendGpgga(String msg) {
        gpgga = msg;
        return gpgga;
    }

    private String sendGprmc(String msg) {
        gprmc = msg;
        return gprmc;
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lon = location.getLongitude();
        if (mLastLocation == null){
            mPreviousLocation = location;
        } else {
            mPreviousLocation = mLastLocation;
        }
        mLastLocation = location;
        setLtLn();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    void setLtLn(){
        lat = lat;
        lon = lon;
    }



}
