package ua.com.pandasushi.pandacourierv2.services;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.pandasushi.pandacourierv2.R;

import ua.com.pandasushi.database.common.CourierOrder;
import ua.com.pandasushi.pandacourierv2.DBHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import ua.com.pandasushi.database.common.gps.models.AVLData;
import ua.com.pandasushi.database.common.gps.models.Points;
import ua.com.pandasushi.database.common.gps.models.Track;
import ua.com.pandasushi.pandacourierv2.activities.LoginActivity;

/**
 * Created by postp on 18.03.2018.
 */

public class TrackWritingService extends Service implements LocationListener {

    private Handler handler;

    private static Location mLastLocation;
    private static Location mPreviousLocation;

    private SharedPreferences sharedPreferences;
    private Double lat, lon;
    private LocationManager mLocationManager;
    private static String gprmc, gpgga;
    private static int pointsOnTrack, timerCount;
    private ArrayList<AVLData> lisAvldata = new ArrayList<>();
    public static Double currentLat, currentLon;
    private boolean isStart = false;
    private static double lenght, fullLenght;
    private static ArrayList<Points> pointsList = new ArrayList<>();
    private static ArrayList<Double> points = new ArrayList<>();
    private DBHelper dbHelper;
    private static String timeStart, timeStop;
    private Gson gson;
    public static String lenghtOfTrack = "0.0";
    public static Integer courierId;
    public static List<CourierOrder> orders = new ArrayList<>();
    public static Track track = new Track();

    private NotificationCompat.Builder builder;
    private Notification notification;

    private final static String TAG = "TrackService";

    private boolean appDestroy = false;

    Runnable tracking = new Runnable() {
        public void run() {

            try {
                timerCount++;
                AVLData avlData = parse(gprmc, gpgga);
                if (gpgga ==null & Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mLastLocation = getLastKnownLocation();
                    if (mLastLocation != null) {
                        avlData = parseAvl(mLastLocation);
                    }
                }

                int minspeed = 3;
                int maxspeed = 160;

                if (avlData.getLongitude() != 0.0 & avlData.getSpeed() >= minspeed & avlData.getSpeed() <= maxspeed) {
                    if (points.size() == 0) {
                        points.add(avlData.getLatitude());
                        points.add(avlData.getLongitude());
                        points.add(0.0);

                    }
                    isStart = true;
                    if (2 != 0 && timerCount % 2 == 0 || 2 != 0 && timerCount - 2 >= 0) {
                        timerCount = 0;
                        pointsList.add(new Points("" + avlData.getLatitude(), "" + avlData.getLongitude()));
                        lisAvldata.add(avlData);
                    }
                    if (15 != 0 & avlData.getHeading() != points.get(2) & Math.abs((int) avlData.getHeading() - points.get(2).intValue()) >= 15) {
                        points.set(2, (double) avlData.getHeading());
                        pointsList.add(new Points("" + avlData.getLatitude(), "" + avlData.getLongitude()));
                        lisAvldata.add(avlData);
                    }

                    Location loc1 = new Location("");
                    loc1.setLatitude(points.get(0));
                    loc1.setLongitude(points.get(1));
                    Location loc2 = new Location("");
                    loc2.setLatitude(avlData.getLatitude());
                    loc2.setLongitude(avlData.getLongitude());
                    float bearing = loc1.bearingTo(loc2);
                    int distanceInMeters = (int) loc1.distanceTo(loc2);

                    if (10 != 0 & avlData.getLatitude() != points.get(0) & Math.abs(distanceInMeters - 10) >= 10) {
                        points.set(0, avlData.getLatitude());
                        points.set(1, avlData.getLongitude());
                        pointsList.add(new Points("" + avlData.getLatitude(), "" + avlData.getLongitude()));
                        lisAvldata.add(avlData);
                    }

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
                    timerCount = 0;
                }


                currentLat = avlData.getLatitude();
                currentLon = avlData.getLongitude();

            } catch (Exception x) {
                x.printStackTrace();
            }
            fullLenght = lenght;
            lenghtOfTrack = String.valueOf(fullLenght);

            timeStop = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            track.setTimeStart(timeStart);
            track.setTimeStop(timeStop);
            List<Points> pointsListWithoutDuplicate = new ArrayList<>();
            Iterator<Points> itr = pointsList.iterator();
            boolean firstPoint = true;
            pointsOnTrack = 0;
            while (itr.hasNext()){
                Points points = itr.next();
                if (firstPoint){
                    pointsListWithoutDuplicate.add(points);
                    pointsOnTrack++;
                    firstPoint = false;
                }
                if (itr.hasNext()){
                    Points points2 = itr.next();
                    if (!points.getLat().equals(points2.getLat()) || !points.getLon().equals(points2.getLon())){
                        pointsListWithoutDuplicate.add(points2);
                        pointsOnTrack++;
                    }
                }
            }
            track.setPoints(pointsListWithoutDuplicate);
            track.setOrders(orders);
            track.setCourierId(courierId);
            track.setPointsOnTrack(pointsOnTrack);
            track.setTrackLenght(lenghtOfTrack);

            builder = new NotificationCompat.Builder(TrackWritingService.this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setOngoing(true)
                    .setContentTitle(getString(R.string.record_track_is_running))
                    .setContentText(lenghtOfTrack + " " + getString(R.string.m));

            notification = builder.build();

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(1, notification);

            handler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        handler = new Handler();
        dbHelper = new DBHelper(this);
        gson = new Gson();

        sharedPreferences = getSharedPreferences("myPref", MODE_PRIVATE);

        courierId = sharedPreferences.getInt("courierId", -1);

        sharedPreferences.edit().putBoolean("appDestroy", false).apply();

        sharedPreferences.edit().putBoolean("serviceStarted", true).apply();

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (mLocationManager != null) {
            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);
            }
        }

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
        }

        if (!appDestroy){
            builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setOngoing(true)
                    .setContentTitle(getString(R.string.record_track_is_running))
                    .setContentText(lenghtOfTrack + " " + getString(R.string.m));

            notification = builder.build();


            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(1, notification);
        }

        handler.post(tracking);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        appDestroy = sharedPreferences.getBoolean("appDestroy", false);

        if (appDestroy){
            Intent broadcastIntent = new Intent("RestartTrackWritingService");
            sendBroadcast(broadcastIntent);
            handler.removeCallbacks(tracking);
        } else {
            if (pointsList.size() >= 2) {
                timeStop = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                ContentValues cv = new ContentValues();
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                String name = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
                cv.put("name", name);
                Track trackSaved = new Track();
                trackSaved.setTimeStart(timeStart);
                trackSaved.setTimeStop(timeStop);
                List<Points> pointsListWithoutDuplicate = new ArrayList<>();
                Iterator<Points> itr = pointsList.iterator();
                boolean firstPoint = true;
                while (itr.hasNext()){
                    Points points = itr.next();
                    if (firstPoint){
                        pointsListWithoutDuplicate.add(points);
                        firstPoint = false;
                    }
                    if (itr.hasNext()){
                        Points points2 = itr.next();
                        if (!points.getLat().equals(points2.getLat()) || !points.getLon().equals(points2.getLon())){
                            pointsListWithoutDuplicate.add(points2);
                        }
                    }
                }
                trackSaved.setPoints(pointsListWithoutDuplicate);
                trackSaved.setOrders(orders);
                trackSaved.setCourierId(courierId);
                trackSaved.setPointsOnTrack(pointsOnTrack);
                trackSaved.setTrackLenght(String.valueOf(fullLenght));

                lenghtOfTrack = String.valueOf(fullLenght);

                sharedPreferences.edit().putString("lastTrack", gson.toJson(track)).apply();

                cv.put("track", gson.toJson(track).getBytes());
                db.insert("trackdata", null, cv);

            }

            timeStart = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            lenght = 0.0;
            fullLenght = 0.0;
            lenghtOfTrack = "0.0";
            track = new Track();
            pointsList = new ArrayList<>();
            orders = new ArrayList<>();
            points = new ArrayList<>();

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(1);

            sharedPreferences.edit().putBoolean("serviceStarted", false).apply();

            handler.removeCallbacks(tracking);
        }
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
