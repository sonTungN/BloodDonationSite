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
    
    public static List<Address> CREATE_ADDRESS_AVAILABLE = new ArrayList<>(Arrays.asList(
            new Address(
                    "Bệnh viện Đại học Y Dược TP.HCM",
                    "215 Đ. Hồng Bàng, Phường 11, Quận 5, Hồ Chí Minh",
                    10.755452274542863, 106.66454530551269
            ),
            new Address(
                    "Bệnh viện Chợ Rẫy",
                    "201B Đ. Nguyễn Chí Thanh, Phường 12, Quận 5, Hồ Chí Minh",
                    10.758120540545562, 106.65953067720469
            ),
            new Address(
                    "Bệnh viện Truyền máu - Huyết học Tp. Hồ Chí Minh",
                    "118 Đ. Hồng Bàng, Phường 12, Quận 5, Hồ Chí Minh",
                    10.756617732143596, 106.6658494154832
            ),
            new Address(
                    "Bệnh viện Nhi Đồng 2",
                    "14 Lý Tự Trọng, Bến Nghé, Quận 1, Hồ Chí Minh",
                    10.780777186921433, 106.70315183077477
            )
    ));
}
