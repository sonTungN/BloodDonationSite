package com.sontung.blood.shared;

import com.google.android.gms.maps.model.LatLng;
import com.sontung.blood.model.Address;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Coordinates {
    public static LatLng RMIT = new LatLng(10.729360903646382, 106.69592019042942);
    
    public static String RMIT_ADDRESS = "Main Gate - 702 Nguyễn Văn Linh Street, Tân Hưng Ward, District 7, Ho Chi Minh City, 700000, Vietnam";
    
    public static double RMIT_LAT = RMIT.latitude;
    public static double RMIT_LONG = RMIT.longitude;
    
    public static double BOTTOM_BOUND = RMIT_LAT - .1;
    public static double LEFT_BOUND = RMIT_LONG - .1;
    public static double TOP_BOUND = RMIT_LAT + .1;
    public static double RIGHT_BOUND = RMIT_LONG + .1;
    
    public static List<Address> CREATE_ADDRESS_AVAILABLE = new ArrayList<Address>(Arrays.asList(
            new Address("227 Nguyễn Văn Cừ, District 5, Ho Chi Minh City", 10.762622, 106.682229),
            new Address("268 Lý Thường Kiệt, District 10, Ho Chi Minh City", 10.770173, 106.666296),
            new Address("702 Nguyễn Văn Linh, District 7, Ho Chi Minh City", 10.729647, 106.694225),
            new Address("391A Nam Kỳ Khởi Nghĩa, District 3, Ho Chi Minh City", 10.769359, 106.685333)
    ));
}
