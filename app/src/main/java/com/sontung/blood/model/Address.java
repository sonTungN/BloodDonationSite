package com.sontung.blood.model;

import com.google.android.gms.maps.model.LatLng;

import lombok.Getter;

@Getter
public class Address {
    private final String name;
    private final String address;
    private final LatLng coordinates;
    
    public Address(String name, String address, double lat, double lng) {
        this.name = name;
        this.address = address;
        this.coordinates = new LatLng(lat, lng);
    }
    
}
