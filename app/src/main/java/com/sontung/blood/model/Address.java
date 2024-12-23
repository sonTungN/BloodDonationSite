package com.sontung.blood.model;

import com.google.android.gms.maps.model.LatLng;

import lombok.Getter;

@Getter
public class Address {
    private final String name;
    private final LatLng coordinates;
    
    public Address(String name, double lat, double lng) {
        this.name = name;
        this.coordinates = new LatLng(lat, lng);
    }
    
}
