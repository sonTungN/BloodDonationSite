package com.sontung.blood.model;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Data
public class CustomAdvancedMarker implements ClusterItem {
    private LatLng position;
    private String title;
    private String snippet;
    private int iconPic;
    private Site site;
    
    @Nullable
    @Override
    public Float getZIndex() {
        return 0f;
    }
}