package com.sontung.blood.fragments;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.GeoPoint;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.sontung.blood.BuildConfig;
import com.sontung.blood.R;
import com.sontung.blood.adapter.MultipleImageAdapter;
import com.sontung.blood.databinding.FragmentEventMapBinding;
import com.sontung.blood.model.CustomAdvancedMarker;
import com.sontung.blood.model.MapPolyline;
import com.sontung.blood.model.Site;
import com.sontung.blood.model.User;
import com.sontung.blood.shared.Coordinates;
import com.sontung.blood.shared.Location;
import com.sontung.blood.utils.LocationPermission;
import com.sontung.blood.utils.MarkerSetter;
import com.sontung.blood.viewmodel.SiteViewModel;
import com.sontung.blood.viewmodel.UserViewModel;
import com.sontung.blood.views.EventDetailActivity;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import org.checkerframework.checker.units.qual.A;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class EventMapFragment
        extends Fragment
        implements OnMapReadyCallback {
    
    private FragmentEventMapBinding binding;
    
    private GoogleMap map;
    
    private UserViewModel userViewModel;
    private SiteViewModel siteViewModel;
    private User currentUser;
    
    private final List<Site> userRegisteredSite = new ArrayList<>();
    private final List<Site> userVolunteerSite = new ArrayList<>();
    private Site userHostedSite;
    
    // Google map API
    private GeoApiContext mapGeoAPIContext;
    
    // Location Service
    private FusedLocationProviderClient locationClient;
    private LocationRequest locRequest;
    private LocationCallback locCallback;
    
    private ClusterManager<CustomAdvancedMarker> customManager;
    private MarkerSetter markerRenderer;
    
    private final List<CustomAdvancedMarker> customAdvancedMarkers = new ArrayList<>();
    
    // Polylines
    private List<MapPolyline> listOfMapPolyline = new ArrayList<>();
    
    @Override
    public void onStart() {
        super.onStart();
        validateLocationPermission();
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentEventMapBinding.inflate(getLayoutInflater());
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        siteViewModel = new ViewModelProvider(this).get(SiteViewModel.class);
        currentUser =
                userViewModel.getUserDataById(
                        userViewModel.getCurrentUserId()
                ).getValue();
        
        fetchAllSiteIntoMap();
        
        locationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        
        locRequest = new LocationRequest
                .Builder(Priority.PRIORITY_HIGH_ACCURACY)
                .setIntervalMillis(Location.LOCATION_INTERVAL)
                .setMinUpdateIntervalMillis(Location.LOCATION_INTERVAL_MIN)
                .setMaxUpdateDelayMillis(Location.LOCATION_DELAY_TIME_MAX)
                .setWaitForAccurateLocation(true)
                .build();
        
        locCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (android.location.Location location : locationResult.getLocations()) {
                    Log.d("UPDATE LOCATION: LAT:", String.valueOf(location.getLatitude()));
                    Log.d("UPDATE LOCATION: LONG:", String.valueOf(location.getLongitude()));
                    
                    super.onLocationResult(locationResult);
                }
            }
            
        };
    }
    
    @Override
    public View onCreateView (
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        
        return binding.getRoot();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        startUpdatingLocation();
        fetchAllSiteIntoMap();
    }
    
    @Override
    public void onStop() {
        super.onStop();
        stopUpdatingLocation();
    }
    
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        
        try {
            boolean success =
                    map.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.style_raw));
            if (!success) {
                Log.e(TAG, "STYLE: Style parsing Error");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "STYLE: Style not found", e);
        }
        
        if (mapGeoAPIContext == null) {
            mapGeoAPIContext =
                    new GeoApiContext.Builder()
                            .apiKey(BuildConfig.MAPS_API_KEY).build();
        }
        
        map.setOnPolylineClickListener(this::polylineClickHandler);
    }
    
    private void polylineClickHandler(Polyline polyline) {
        for (MapPolyline currentPolyline: listOfMapPolyline) {
            if (polyline.getId().equals(currentPolyline.getPolyline().getId())) {
                currentPolyline
                        .getPolyline()
                        .setColor(
                                ContextCompat.getColor(requireActivity(), R.color.polyline)
                        );
                currentPolyline.getPolyline().setZIndex(1);
                binding.duration.setText(currentPolyline.getDirectionsLeg().duration.toString());
                
            } else {
                currentPolyline.getPolyline().setColor(
                        ContextCompat.getColor(requireActivity(), R.color.purple_500)
                );
                currentPolyline.getPolyline().setZIndex(0);
            }
        }
    }
    
    private void fetchAllSiteIntoMap() {
        siteViewModel
                .getUserRegisteredSite(userViewModel.getCurrentUserId())
                .observe(this, sites -> {
                    if (sites != null) {
                        userRegisteredSite.clear();
                        userRegisteredSite.addAll(sites);

                        markAllSiteFromList(map, userRegisteredSite);
                    } else {
                        Log.d("MAP", "Failed to get registered site!");
                    }
                });
        
//        siteViewModel
//                .getUserVolunteerSite(userViewModel.getCurrentUserId())
//                .observe(this, sites -> {
//                    if (sites != null) {
//                        userVolunteerSite.clear();
//                        userVolunteerSite.addAll(sites);
//
//                        markAllSiteFromList(map, userVolunteerSite);
//                    } else {
//                        Log.d("MAP", "Failed to get volunteer site!");
//                    }
//                });
        
        siteViewModel
                .getUserHostedSite(userViewModel.getCurrentUserId())
                .observe(this, sites -> {
                    if (sites != null) {
                        userHostedSite = sites;
                        markAllSiteFromList(map, List.of(userHostedSite));
                    } else {
                        Log.d("MAP", "Failed to get hosted site!");
                    }
                });
    }
    
    @SuppressLint("SetTextI18n")
    private void markAllSiteFromList (GoogleMap googleMap, List<Site> siteList) {
        if (customManager == null) {
            customManager = new ClusterManager<>(requireContext(), googleMap);
        }
        
        if (markerRenderer == null) {
            markerRenderer = new MarkerSetter(requireContext(), googleMap, customManager);
            customManager.setRenderer(markerRenderer);
        }
        
        userViewModel.getCurrentUser().observe(this, user -> {
            for (Site site: siteList) {
                String hostEmail =
                        Objects.requireNonNull(
                                userViewModel.getUserDataById(site.getHost()).getValue()
                        ).getEmail();
                double latValue = Double.parseDouble(site.getLatitude());
                double longValue = Double.parseDouble(site.getLongitude());
                
                int markerIcon = 0;
                if (site.getHost().equals(userViewModel.getCurrentUserId())) {
                    markerIcon = R.drawable.ic_admin_icon;
                    
                } else if (isUserRegistered(site, user)) {
                    markerIcon = R.drawable.ic_donor_icon;
                    
                } else if (isUserVolunteered(site, user)) {
                    markerIcon = R.drawable.ic_volunteer_icon;
                    
                } else {
                    markerIcon = R.drawable.icon_blood_type;
                }
                
                CustomAdvancedMarker marker =
                        CustomAdvancedMarker.builder()
                                .site(site)
                                .iconPic(markerIcon)
                                .title(site.getSiteName())
                                .snippet(hostEmail)
                                .position(new LatLng(latValue, longValue))
                                .build();
                
                customManager.addItem(marker);
                customAdvancedMarkers.add(marker);
            }
        });
        
        LatLng coordinates = Coordinates.RMIT;
        userViewModel.getCurrentUser().observe(this, user -> {
            CustomAdvancedMarker myMarker =
                    CustomAdvancedMarker.builder()
                            .site(null)
                            .iconPic(R.drawable.ic_location_icon)
                            .title(user.getUsername())
                            .snippet(user.getEmail())
                            .position(coordinates)
                            .build();
            
            customManager.addItem(myMarker);
            customAdvancedMarkers.add(myMarker);
            customManager.cluster();
            customManager.setOnClusterItemClickListener(marker -> {
                if (marker.getSite() == null) {
                    binding.siteName.setText("YOUR LOCATION");
                    binding.siteAddress.setText(Coordinates.RMIT_ADDRESS);
                    renderSiteDetailIntoView(false);
                    
                } else {
                    getSiteDistance(marker);
                    binding.siteName.setText(marker.getSite().getSiteName());
                    binding.siteAddress.setText("Location: " + marker.getSite().getSiteAddress());
                    
                    setUpImageSlider(marker.getSite().getSiteImageUrl());
                    setUpDetailBtnClicked(marker.getSite());
                    renderSiteDetailIntoView(true);
                }
                
                return false;
            });
            
            setCameraViewToUserCurrentLocation();
        });
    }
    
    private boolean isUserRegistered(Site site, User user) {
        return site.getListOfDonors() != null &&
                site.getListOfDonors().contains(user.getUserId());
    }
    
    private boolean isUserVolunteered(Site site, User user) {
        return site.getListOfVolunteers() != null &&
                site.getListOfVolunteers().contains(user.getUserId());
    }
    
    private void renderSiteDetailIntoView (boolean isDetailVisible) {
        if (isDetailVisible) {
            binding.toDetails.setVisibility(View.VISIBLE);
            binding.imageSlider.setVisibility(View.VISIBLE);
            binding.dotsIndicator.setVisibility(View.VISIBLE);
            binding.durationContainer.setVisibility(View.VISIBLE);
            
        } else {
            binding.toDetails.setVisibility(View.GONE);
            binding.imageSlider.setVisibility(View.GONE);
            binding.dotsIndicator.setVisibility(View.GONE);
            binding.durationContainer.setVisibility(View.GONE);
        }
    }
    
    private void setUpDetailBtnClicked (Site site) {
        binding.toDetails.setOnClickListener(view -> {
            Intent i = new Intent(requireContext(), EventDetailActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("SITE_ID", site.getSiteId());
            startActivity(i);
        });
    }
    
    private void setUpImageSlider (List<String> siteImageUrl) {
        MultipleImageAdapter adapter = new MultipleImageAdapter(siteImageUrl);
        binding.imageSlider.setAdapter(adapter);
        binding.dotsIndicator.attachTo(binding.imageSlider);
    }
    
    private void setCameraViewToUserCurrentLocation() {
        LatLngBounds latLngBounds = new LatLngBounds(
                new LatLng(Coordinates.BOTTOM_BOUND, Coordinates.LEFT_BOUND),
                new LatLng(Coordinates.TOP_BOUND, Coordinates.RIGHT_BOUND)
        );
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 0));
    }
    
    private void getSiteDistance(CustomAdvancedMarker marker) {
        com.google.maps.model.LatLng location = new com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(mapGeoAPIContext);
        directions.alternatives(true);
        directions.origin(
                new com.google.maps.model.LatLng(
                        Coordinates.RMIT_LAT,
                        Coordinates.RMIT_LONG
                )
        );
        
        directions
                .destination(location)
                .setCallback(new PendingResult.Callback<>() {
                    @Override
                    public void onResult(DirectionsResult result) {
                        addPolylinesForLocationToMap(result);
                    }
                    
                    @Override
                    public void onFailure(Throwable e) {
                        Log.d("MAP: LOCATION", "Location: Can't get direction, Exception: " + e.getMessage());
                    }
                });
    }
    
    private void addPolylinesForLocationToMap(DirectionsResult result) {
        Log.d("MAP: LOCATION", "Location: Got direction");
        new Handler(Looper.getMainLooper())
                .post(() -> {
                    if (!listOfMapPolyline.isEmpty()) {
                        for (MapPolyline mapPolyline: listOfMapPolyline) {
                            mapPolyline.getPolyline().remove();
                        }
                        
                        listOfMapPolyline.clear();
                        listOfMapPolyline = new ArrayList<>();
                    }
                    
                    double duration = 999999999;
                    for(DirectionsRoute route: result.routes) {
                        List<com.google.maps.model.LatLng> paths =
                                PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());
                        
                        List<LatLng> listOfNewPaths = new ArrayList<>();
                        for (com.google.maps.model.LatLng ll: paths) {
                            listOfNewPaths.add(new LatLng(ll.lat, ll.lng));
                        }
                        
                        Polyline poly = map.addPolyline(
                                new PolylineOptions().addAll(listOfNewPaths)
                        );
                        poly.setColor(ContextCompat.getColor(requireActivity(), R.color.softGrayishBlue));
                        poly.setClickable(true);
                        
                        MapPolyline mapPolyline =
                                MapPolyline.builder()
                                        .polyline(poly)
                                        .directionsLeg(route.legs[0])
                                        .build();
                        
                        listOfMapPolyline.add(mapPolyline);
                        
                        double checkDuration = route.legs[0].duration.inSeconds;
                        if (checkDuration < duration) {
                            duration = checkDuration;
                            polylineClickHandler(poly);
                            zoomRoute(poly.getPoints(), 50, 600);
                        }
                    }
                });
    }
    
    private void zoomRoute(List<LatLng> route, int padding, int duration) {
        if (map == null || route == null || route.isEmpty()) {
            return;
        }
        
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng point : route) {
            boundsBuilder.include(point);
        }
        
        map.animateCamera(
                CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), padding),
                duration,
                null
        );
    }
    
    private void validateLocationPermission() {
        if (LocationPermission.LOCATION_GRANTED(requireContext())) {
            startUpdatingLocation();
        } else {
            requestForLocationPermission();
        }
    }
    
    private void requestForLocationPermission() {
        requestLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }
    
    private final ActivityResultLauncher<String> requestLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(), isGranted -> {
                        if (isGranted) {
                            Toast.makeText(requireContext(), "Location permission granted.", Toast.LENGTH_SHORT).show();
                            startUpdatingLocation();
                        } else {
                            Toast.makeText(requireContext(), "Permission denied. Enable GPS for Google Maps functionality.", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
    
    private void startUpdatingLocation() {
        if (LocationPermission.LOCATION_GRANTED(requireContext())) { return; }
        
        if (
                ActivityCompat
                        .checkSelfPermission(
                                requireContext(),
                                Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                        &&
                        ActivityCompat
                                .checkSelfPermission(
                                        requireContext(),
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                ) != PackageManager.PERMISSION_GRANTED
        ) { return; }
        
        locationClient.requestLocationUpdates(locRequest, locCallback, null);
    }
    
    private void stopUpdatingLocation() {
        locationClient.removeLocationUpdates(locCallback);
    }
}