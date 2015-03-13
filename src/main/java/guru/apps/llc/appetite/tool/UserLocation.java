package guru.apps.llc.appetite.tool;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import guru.apps.llc.appetite.Appetite;
import com.google.android.gms.maps.model.LatLng;

/**
 * Singleton used to setup location based services.
 *
 * Created by jrpotter on 12/25/14.
 */
public class UserLocation {

    // Static Members
    // ==================================================

    private static UserLocation instance;


    // Instance Members
    // ==================================================

    private Location mLastLocation;


    // Singleton Members
    // ==================================================

    public static UserLocation getInstance() {
        if(instance == null) {
            instance = new UserLocation();
        }

        return instance;
    }


    // Public Methods
    // ==================================================

    public void init() {
        mLastLocation = new Location("flp"); // Default Location
        LocationManager mLocationManager = (LocationManager) Appetite.getContext().getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new UserLocationListener(), null);
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

    private UserLocation() {
        // Intentionally empty
    }

}
