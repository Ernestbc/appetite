package com.example.jrpotter.appetite.tool;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/**
 * Singleton used to setup location based services.
 *
 * Created by jrpotter on 12/25/14.
 */
public class UserLocation {

    // Static Members
    // ==================================================

    private static Activity mContext;
    private static UserLocation instance;

    // Location Data
    private static Location mLastLocation;


    // Public Methods
    // ==================================================

    /**
     *
     * @return ""
     */
    public static UserLocation getInstance() {
        return getInstance(null);
    }

    /**
     * Allow the user to change the context of the location call.
     *
     * @param context ""
     * @return ""
     */
    public static UserLocation getInstance(Activity context) {
        if(instance == null || context != null) {
            mContext = context;
            instance = new UserLocation();
        }

        return instance;
    }

    public double getLatitude() {
        return mLastLocation.getLatitude();
    }

    public double getLongitude() {
        return mLastLocation.getLongitude();
    }

    public LatLng getLocation() { return new LatLng(getLatitude(), getLongitude()); }


    // Location Methods
    // ==================================================

    private class UserLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            synchronized (instance) {
                mLastLocation.setProvider(location.getProvider());
                mLastLocation.setLatitude(location.getLatitude());
                mLastLocation.setLongitude(location.getLongitude());
                instance.notify();
            }
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


    // Private Constructor
    // ==================================================

    /**
     * Find the user's current location.
     */
    private UserLocation() {
        mLastLocation = new Location("flp"); // Default Location
        LocationManager mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new UserLocationListener(), null);
    }

}
