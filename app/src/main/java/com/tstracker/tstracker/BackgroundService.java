package com.tstracker.tstracker;


import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;
import android.location.LocationManager;
import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.provider.Settings;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import android.os.IBinder;
import android.util.Log;
import android.os.Bundle;

/**
 * Created by ali on 9/25/15.
 */
public class BackgroundService extends Service {

    public String TAG="GPSLog";
    private static LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10;
public static Context context;
    public BackgroundService() {

    }



    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }
    @Override
    public void onCreate() {
       // LOCATION_INTERVAL=Integer.valueOf(Tools.Interval);

        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            try {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, new LocationListener(LocationManager.NETWORK_PROVIDER));
            } catch (java.lang.SecurityException ex) {
                Toast.makeText(context, "fail to request location update, ignore", Toast.LENGTH_LONG).show();
            } catch (IllegalArgumentException ex) {
                Toast.makeText(context, "network provider does not exist, " + ex.getMessage(), Toast.LENGTH_LONG).show();

            }
            try {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, new LocationListener(LocationManager.GPS_PROVIDER));
            } catch (java.lang.SecurityException ex) {
                Toast.makeText(context, "fail to request location update, ignore", Toast.LENGTH_LONG).show();
            } catch (IllegalArgumentException ex) {
                Toast.makeText(context, "GPS provider does not exist, " + ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }

    public class LocationListener implements android.location.LocationListener {
        Location mLastLocation;
        public LocationListener(String provider)
        {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }
        private String IsTimeToSend() {
            DatabaseHelper dh = new DatabaseHelper(getApplicationContext());
            SQLiteDatabase db;
            try {
                db = dh.getReadableDatabase();
                String[] columns = {DatabaseContracts.Settings.COLUMN_NAME_endTime
                        , DatabaseContracts.Settings.COLUMN_NAME_fromTime
                        , DatabaseContracts.Settings.COLUMN_NAME_days};
                Cursor c = db.query(DatabaseContracts.Settings.TABLE_NAME, columns, "", null, "", "", "");
                c.moveToFirst();
                try {
                    String days = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Settings.COLUMN_NAME_days));
                    String EndTime = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Settings.COLUMN_NAME_endTime));
                    String startTime = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Settings.COLUMN_NAME_fromTime));

                    Calendar cal = Calendar.getInstance();
                    Date date = cal.getTime();
                    String datetime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(date);
                    //(String) DateFormat.format("yyyy-MM-dd HH:mm:ss",c );
                    Integer dayWeek = cal.get(Calendar.DAY_OF_WEEK);
                    int hourOfDay = Integer.valueOf(new SimpleDateFormat("k", Locale.US).format(date));

                    if (dayWeek == 7)//shanbe
                        dayWeek = 0;
                    if (days.contains(String.valueOf(dayWeek)))//check day
                        if (hourOfDay < Integer.valueOf(EndTime) && hourOfDay >= Integer.valueOf(startTime))//check hour
                        {
                            return datetime;
                        }
                } catch (Exception er) {
                    // txtResult.setText(er.getMessage());
                }
            } catch (Exception ex) {

            }
            return null;
        }
        @Override
        public void onLocationChanged(Location location) {

            String Lat, Lon, alti, speed, coarse, datetime;
            try {
                Lat = String.valueOf(location.getLatitude());
                Lon = String.valueOf(location.getLongitude());
                alti = String.valueOf(location.getAltitude());
                speed = String.valueOf(location.getSpeed());
                coarse = "0";
                datetime = IsTimeToSend();

                if ( datetime != null  ) {
                    SaveGps s = new SaveGps(getApplicationContext(), Lat, Lon, alti, speed, coarse, datetime);
//                    Toast.makeText(getApplicationContext(),datetime, Toast.LENGTH_LONG).show();
}
            } catch (Exception er) {

            }
            Lat = Lon = alti = speed = coarse = datetime = null;
        }
        @Override
        public void onProviderDisabled(String provider)
        {
//            Toast.makeText( getApplicationContext(), "Gps غیر فعال شد", Toast.LENGTH_SHORT ).show();

        }

@Override
        public void onProviderEnabled(String provider)
        {
//            Toast.makeText( getApplicationContext(), "Gps فعال شد", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }
}
