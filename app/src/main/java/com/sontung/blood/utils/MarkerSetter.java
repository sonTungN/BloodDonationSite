package com.sontung.blood.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.sontung.blood.R;
import com.sontung.blood.model.CustomAdvancedMarker;

public class MarkerSetter extends DefaultClusterRenderer<CustomAdvancedMarker> {
    private final IconGenerator iconGenerator;
    private final ImageView imageView;
    
    public MarkerSetter (
            Context context,
            GoogleMap map,
            ClusterManager<CustomAdvancedMarker> clusterManager
    ) {
        super(context, map, clusterManager);
        
        iconGenerator = new IconGenerator(context.getApplicationContext());
        imageView = new ImageView(context.getApplicationContext());
        
        imageView.setLayoutParams(
                new ViewGroup.LayoutParams(20, 20)
        );
        iconGenerator.setContentView(imageView);
    }
    
    @Override
    protected void onBeforeClusterItemRendered(
            @NonNull CustomAdvancedMarker item,
            @NonNull MarkerOptions markerOptions
    ) {
        imageView.setImageResource(item.getIconPic());
        Bitmap bitmapIcon = iconGenerator.makeIcon();
        markerOptions
                
                .icon(BitmapDescriptorFactory.fromBitmap(bitmapIcon))
                .title(item.getTitle())
                .snippet(item.getSnippet());
    }
    
    @Override
    protected boolean shouldRenderAsCluster(@NonNull Cluster<CustomAdvancedMarker> cluster) {
        return false;
    }
}
