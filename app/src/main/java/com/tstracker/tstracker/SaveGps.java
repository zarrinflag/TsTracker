package com.tstracker.tstracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.database.sqlite.SQLiteDatabase;
/**
 * Created by ali on 9/27/15.
 */
public class SaveGps {

    DatabaseHelper dbh;
    SQLiteDatabase db;
    private Context CuContext;
    private String CuBatteryCharge;

    public SaveGps(Context context, String Lat, String Lon, String alti, String speed, String curse, String datetime) {
        this.CuContext = context;
        this.CuBatteryCharge = "0";
        CuContext.registerReceiver(this.batteryInformationReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        try {

            dbh = new DatabaseHelper(CuContext);
            db = dbh.getWritableDatabase();

            String data =  Lat + "," + Lon + "," + alti + "," + speed + "," + curse + "," + datetime + "," + CuBatteryCharge;
            ContentValues values = new ContentValues();
            values.put(DatabaseContracts.AVLData.COLUMN_NAME_Data, data);
// Insert the new row, returning the primary key value of the new row
            long newRowId;
            newRowId = db.insert(DatabaseContracts.AVLData.TABLE_NAME, DatabaseContracts.AVLData.COLUMN_NAME_ID, values);
        } catch (Exception ioe) {
            throw new Error("Unable to create database");
        }

    }

    BroadcastReceiver batteryInformationReceiver= new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            int  level= intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);

            CuBatteryCharge = String.valueOf(level);;
        }
    };
}
