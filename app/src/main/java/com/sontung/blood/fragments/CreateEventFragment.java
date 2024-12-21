package com.sontung.blood.fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.sontung.blood.R;
import com.sontung.blood.adapter.ImageAdapter;
import com.sontung.blood.databinding.FragmentCreateEventBinding;
import com.sontung.blood.model.Site;
import com.sontung.blood.model.User;
import com.sontung.blood.viewmodel.SiteViewModel;
import com.sontung.blood.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

public class CreateEventFragment extends Fragment
    implements
        OnMapReadyCallback,
        AdapterView.OnItemSelectedListener,
        ImageAdapter.OnItemCountAfterDelete,
        ImageAdapter.OnItemZoom {
    
    private FragmentCreateEventBinding fragmentCreateSiteBinding;
    private List<Uri> uriList = new ArrayList<>();
    
    private SupportMapFragment mapFragment;
    private View mapPanel;
    
    private Marker marker;
    private GoogleMap map;
    private LatLng coordinates;
    
    private ImageAdapter imageAdapter;
    
    private String severityCategory;
    
    private ImageView imageService;
    
    private UserViewModel userService;
    private User currentUser;
    
    private SiteViewModel siteService;
    private Site currentSite;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_event, container, false);
    }
    
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    
    }
    
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    
    }
    
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
    
    }
    
    @Override
    public void clickDelete(int leftNum) {
    
    }
    
    @Override
    public void itemZoomClick(int position) {
    
    }
}