package com.sontung.blood.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

public class LocationPermission {
    public static boolean LOCATION_GRANTED (Context context) {
        boolean ACCESS_FINE_GRANTED =
                ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED;
        
        boolean ACCESS_COARSE_GRANTED =
                ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED;
        
        return ACCESS_FINE_GRANTED && ACCESS_COARSE_GRANTED;
    }
    
    public static boolean MISSING_LOCATION_PERMISSION (Context context) {
        boolean ACCESS_FINE_MISSING =
                ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED;
        
        boolean ACCESS_COARSE_MISSING =
                ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED;
        
        return ACCESS_FINE_MISSING && ACCESS_COARSE_MISSING;
    }
}
