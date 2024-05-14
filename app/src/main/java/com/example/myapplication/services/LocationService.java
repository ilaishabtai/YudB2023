package com.example.myapplication.services;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class LocationService implements LocationListener{
    private static final String TAG = "LocationUtil";
    private Context context;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private OnLocationChangeListener onLocationChangeListener;

    public interface OnLocationChangeListener {
        void onLocationChanged(Location location);
    }

    public LocationService(Context context) {
        this.context = context;
    }

    public void requestLocationUpdates(OnLocationChangeListener listener) {
        this.onLocationChangeListener = listener;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null && ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, (float) 0, (LocationListener) this);
        } else {
            Log.e(TAG, "Permission denied or LocationManager is null");
        }
    }

    public void removeLocationUpdates() {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.d(TAG, "Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude());
        if (onLocationChangeListener != null) {
            onLocationChangeListener.onLocationChanged(location);
        }
    }

}
