package com.adneom.testplanpie.gps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Allows to get device's current location
 * Created by gtshilombowanticale on 14-08-16.
 */
public class FDGPSTracker implements LocationListener {

    /**
     * Represents the actual context
     */
    private final Context context;

    /**
     * Represents a location manager to handle the location
     */
    private LocationManager locationManager;

    /**
     * Represents the location from location manager
     */
    Location location;

    /**
     * This the status of GPS, true if gps is enabled else false
     */
    private Boolean isGPSEnabled;

    /**
     * This the status of Network, true if gps is enabled else false
     */
    private Boolean isNetworkEnabled;

    /**
     * The minimum distance to change Updates in meters
     */
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters

    /**
     * The minimum time between updates in milliseconds
     */
    private static final long MIN_TIME_BW_UPDATES = 1; // 1 minute

    public FDGPSTracker(Context context) {
        this.context = context;
    }

    public Location getCurrentLocation() {
        try {
            //instantiated location manager
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            //getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            //getting Network
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            Log.i("Adneom", " status GPS : " + isGPSEnabled + " and status Network : " + isNetworkEnabled + " *** ");

            if (!isGPSEnabled && !isNetworkEnabled) {
                Log.i("Adneom", " GPS and Network are not enabled !!! ");
            } else {
                if (isGPSEnabled) {
                    gettingLocation(1);
                }
                if (isNetworkEnabled) {
                    gettingLocation(2);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.i("Adneom", "Error : " + e.getMessage().toString());
            Log.e("E","Error : "+e.getMessage().toString());
        }
        return location;
    }

    /**
     * Represents the location
     *
     * @param val represents the provider, 1 = GPS and 2 = Network
     */
    private void gettingLocation(int val) {
        location = null;
        if(locationManager != null){
            switch (val){
                case 1:
                    location = locationManager
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if(location != null){
                        Log.i("Adneom"," location is from gps ("+location.getLatitude()+","+location.getLatitude()+") -- ");
                    }
                    break;
                case 2:
                    location = locationManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if(location != null){
                        Log.i("Adneom"," location is from network ("+location.getLatitude()+","+location.getLatitude()+") -- ");
                    }
                    break;
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
