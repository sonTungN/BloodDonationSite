package com.sontung.blood.shared;

import com.google.android.gms.maps.model.LatLng;

public class Coordinates {
    public static LatLng RMIT = new LatLng(10.74999, 106.666664);
    
    public static String RMIT_ADDRESS = "Main Gate - 702 Nguyễn Văn Linh Street, Tân Hưng Ward, District 7, Ho Chi Minh City, 700000, Vietnam";
    
    public static double RMIT_LAT = 10.74999;
    public static double RMIT_LONG = 106.666664;
    
    public static double BOTTOM_BOUND = RMIT_LAT - .1;
    public static double LEFT_BOUND = RMIT_LONG - .1;
    public static double TOP_BOUND = RMIT_LAT + .1;
    public static double RIGHT_BOUND = RMIT_LONG + .1;
}
