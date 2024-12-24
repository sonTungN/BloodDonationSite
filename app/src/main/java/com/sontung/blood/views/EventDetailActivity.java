package com.sontung.blood.views;

import static android.content.ContentValues.TAG;

import static androidx.core.content.ContentProviderCompat.requireContext;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.sontung.blood.R;
import com.sontung.blood.adapter.DonorCardAdapter;
import com.sontung.blood.adapter.MultipleImageAdapter;
import com.sontung.blood.adapter.VolunteerCardAdapter;
import com.sontung.blood.callback.FirebaseCallback;
import com.sontung.blood.databinding.ActivityEventDetailBinding;
import com.sontung.blood.model.Site;
import com.sontung.blood.model.User;
import com.sontung.blood.utils.DateFormatter;
import com.sontung.blood.viewmodel.SiteViewModel;
import com.sontung.blood.viewmodel.UserViewModel;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EventDetailActivity extends AppCompatActivity {
    
    private ActivityEventDetailBinding binding;
    private String siteId;

    private SupportMapFragment mapFragment;
    private View mapPanel;
    
    private GoogleMap map;
    private LatLng coordinates;
    private Marker marker;
    
    private UserViewModel userViewModel;
    private SiteViewModel siteViewModel;
    
    // ViewPager with DotIndicator
    private ViewPager2 viewPager2;
    private DotsIndicator indicator;
    
    // RecyclerView
    private RecyclerView volunteerRecyclerView;
    private List<User> volunteerList;
    private VolunteerCardAdapter volunteerAdapter;
    
    private RecyclerView donorRecyclerView;
    private List<User> donorList;
    private DonorCardAdapter donorAdapter;
    
    // Navbar
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    
    @Override
    protected void onStart() {
        super.onStart();
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_detail);
        
        binding = DataBindingUtil.setContentView(this, R.layout.activity_event_detail);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        siteViewModel = new ViewModelProvider(this).get(SiteViewModel.class);
        
        siteId = getIntent().getStringExtra("SITE_ID");
        
        // Viewpager2 and DotIndicator
        viewPager2 = binding.viewPager2;
        indicator = binding.dotsIndicator;
        
        setUpDrawer();
        setUpInitialStage();
        fetchSiteDetailIntoViews(siteId);
        setUpOnButtonClickListener();
        setUpRecyclerView();
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void setUpOnButtonClickListener() {
        binding.donorApplyBtn.setOnClickListener(view -> donorApply());
        binding.volunteerApplyBtn.setOnClickListener(view -> volunteerApply());
    }
    
    private void setUpRecyclerView() {
        setUpVolunteerRecyclerView();
        setUpDonorRecyclerView();
    }
    
    @SuppressLint("NotifyDataSetChanged")
    private void setUpDonorRecyclerView() {
        donorRecyclerView = binding.donorRecyclerView;
        donorRecyclerView.setLayoutManager(
                new LinearLayoutManager(
                        getApplicationContext(),
                        LinearLayoutManager.VERTICAL,
                        false)
        );
        donorRecyclerView.hasFixedSize();
        donorList = new ArrayList<>();
        
        siteViewModel.getSiteDonorList(siteId).observe(this, users -> {
            donorList.clear();
            donorList.addAll(users);
            
            if (donorList.isEmpty()) {
                binding.notFoundDonor.setVisibility(View.VISIBLE);
            } else {
                binding.notFoundDonor.setVisibility(View.GONE);
            }
            
            donorAdapter = new DonorCardAdapter(getApplicationContext(), donorList);
            donorRecyclerView.setAdapter(donorAdapter);
            donorAdapter.notifyDataSetChanged();
        });
    }
    
    @SuppressLint("NotifyDataSetChanged")
    private void setUpVolunteerRecyclerView() {
        volunteerRecyclerView = binding.volunteerRecyclerView;
        volunteerRecyclerView.setLayoutManager(
                new LinearLayoutManager(
                        getApplicationContext(),
                        LinearLayoutManager.VERTICAL,
                        false)
        );
        volunteerRecyclerView.hasFixedSize();
        volunteerList = new ArrayList<>();
        
        siteViewModel.getSiteVolunteerList(siteId).observe(this, users -> {
            volunteerList.clear();
            volunteerList.addAll(users);
            
            if (volunteerList.isEmpty()) {
                binding.notFoundVolunteer.setVisibility(View.VISIBLE);
            } else {
                binding.notFoundVolunteer.setVisibility(View.GONE);
            }
            
            volunteerAdapter = new VolunteerCardAdapter(getApplicationContext(), volunteerList);
            volunteerRecyclerView.setAdapter(volunteerAdapter);
            volunteerAdapter.notifyDataSetChanged();
        });
    }
    
    //----------------------------------------SET UP APPLY BUTTON-----------------------------------
    private void donorApply() {
        String currentUserId = userViewModel.getCurrentUserId();
        siteViewModel.getSiteDataById(siteId).observe(this, site -> {
            userViewModel.getUserDataById(currentUserId).observe(this, user -> {
                siteViewModel.addUserIntoSiteRegisteredList(currentUserId, site.getSiteId(), new FirebaseCallback<>() {
                    @Override
                    public void onSuccess(List<Boolean> t) {
                    
                    }
                    
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        userViewModel.addCurrentUserRegisteredSite(site.getSiteId(), new FirebaseCallback<>() {
                            @Override
                            public void onSuccess(List<Boolean> t) {
                            
                            }
                            
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onSuccess(Boolean aBoolean) {
                                Toast.makeText(EventDetailActivity.this, "Successfully registered as donor", Toast.LENGTH_SHORT).show();
                                setApplyButtonState(binding.donorApplyBtn, false);
                                binding.donorApplyBtn.setText("Already Donor");
                                siteViewModel.getSiteDonorList(siteId);
                            }
                            
                            @Override
                            public void onFailure(List<Boolean> t) {
                            
                            }
                            
                            @Override
                            public void onFailure(Boolean aBoolean) {
                                Toast.makeText(EventDetailActivity.this, "Failed to update user record", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    
                    @Override
                    public void onFailure(List<Boolean> t) {
                    
                    }
                    
                    @Override
                    public void onFailure(Boolean aBoolean) {
                        Toast.makeText(EventDetailActivity.this, "Failed to update user record", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });
    }
    
    private void volunteerApply() {
        String currentUserId = userViewModel.getCurrentUserId();
        siteViewModel.getSiteDataById(siteId).observe(this, site -> {
            userViewModel.getUserDataById(currentUserId).observe(this, user -> {
                siteViewModel.addUserIntoSiteVolunteerList(currentUserId, site.getSiteId(), new FirebaseCallback<>() {
                    @Override
                    public void onSuccess(List<Boolean> t) {
                    
                    }
                    
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        userViewModel.addCurrentUserVolunteerSite(site.getSiteId(), new FirebaseCallback<>() {
                            @Override
                            public void onSuccess(List<Boolean> t) {
                            
                            }
                            
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onSuccess(Boolean aBoolean) {
                                Toast.makeText(EventDetailActivity.this, "Successfully registered as volunteer", Toast.LENGTH_SHORT).show();
                                setApplyButtonState(binding.volunteerApplyBtn, false);
                                binding.volunteerApplyBtn.setText("Already Volunteer");
                                siteViewModel.getSiteVolunteerList(siteId);
                            }
                            
                            @Override
                            public void onFailure(List<Boolean> t) {
                            
                            }
                            
                            @Override
                            public void onFailure(Boolean aBoolean) {
                                Toast.makeText(EventDetailActivity.this, "Failed to update user record", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    
                    @Override
                    public void onFailure(List<Boolean> t) {
                    
                    }
                    
                    @Override
                    public void onFailure(Boolean aBoolean) {
                        Toast.makeText(EventDetailActivity.this, "Failed to update user record", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });
    }
    
    //----------------------------------------SET UP VIEW-------------------------------------------
    @SuppressLint("SetTextI18n")
    private void setUpInitialStage() {
        String currentUserId = userViewModel.getCurrentUserId();
        
        setApplyButtonState(binding.donorApplyBtn, true);
        setApplyButtonState(binding.volunteerApplyBtn, true);
        binding.donorApplyBtn.setText("Donor Apply");
        binding.volunteerApplyBtn.setText("Volunteer Apply");
        
        siteViewModel.getSiteDataById(siteId).observe(this, site -> {
            userViewModel.getCurrentUser().observe(this, user -> {
                if (!site.getRequiredBloodType().equals(user.getBloodType())) {
                    setApplyButtonState(binding.donorApplyBtn, false);
                    binding.donorApplyBtn.setText("BLOOD TYPE MISMATCH");
                }
            });
            
            
            if (Objects.equals(site.getHost(), currentUserId)) {
                setApplyButtonState(binding.donorApplyBtn, false);
                binding.donorApplyBtn.setText("You are Host");
            }
            
            if (site.getListOfDonors().contains(currentUserId)) {
                setApplyButtonState(binding.donorApplyBtn, false);
                binding.donorApplyBtn.setText("Already Donor");
                
            } else {
                boolean isMaxDonor = site.getListOfDonors().size() >= site.getDonorMaxCapacity();
                if (isMaxDonor) {
                    setApplyButtonState(binding.donorApplyBtn, false);
                    binding.donorApplyBtn.setText("Donor at max");
                }
            }
            
            if (site.getListOfVolunteers().contains(currentUserId)) {
                setApplyButtonState(binding.volunteerApplyBtn, false);
                binding.volunteerApplyBtn.setText("Already Volunteer");
            } else {
                boolean isMaxVolunteer = site.getListOfVolunteers().size() >= site.getVolunteerMaxCapacity();
                if (isMaxVolunteer) {
                    setApplyButtonState(binding.volunteerApplyBtn, false);
                    binding.volunteerApplyBtn.setText("Volunteer at max");
                }
            }
        });
    }
    
    private void fetchSiteDetailIntoViews(String siteId) {
        siteViewModel.getSiteDataById(siteId)
                .observe(this, site -> {
                    binding.setSite(site);
                    binding.siteDate.setText(DateFormatter.toDateString(site.getEventDate()));
                    
                    userViewModel.getUserDataById(site.getHost())
                            .observe(this, user -> {
                                binding.siteHost.setText(user.getEmail());
                            });
                    
                    MultipleImageAdapter adapter = new MultipleImageAdapter(site.getSiteImageUrl());
                    viewPager2.setAdapter(adapter);
                    indicator.attachTo(viewPager2);
                    
                    try {
                        double latitude = Double.parseDouble(site.getLatitude());
                        double longitude = Double.parseDouble(site.getLongitude());
                        coordinates = new LatLng(latitude, longitude);
                        
                        binding.progressBarHolder.setVisibility(View.GONE);
                        setUpMapFragment();
                        
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Invalid latitude/longitude: " + e.getMessage());
                        Toast.makeText(this, "Invalid site location data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    //----------------------------------------SET UP MAP FRAGMENT-----------------------------------
    private void setUpMapFragment() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        
        if (mapFragment == null) {
            if (mapPanel == null || mapPanel.getVisibility() == View.GONE) {
                mapPanel = ((ViewStub) findViewById(R.id.stub_map)).inflate();
            }
            
            GoogleMapOptions options = new GoogleMapOptions();
            options.mapToolbarEnabled(false);
            
            mapFragment = SupportMapFragment.newInstance(options);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.confirmation_map, mapFragment, "MAP")
                    .commitNow();
            
            mapFragment.getMapAsync(googleMap -> {
                this.map = googleMap;
                map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                
                try {
                    boolean success =
                            map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_raw));
                    if (!success) {
                        Log.e(TAG, "STYLE: Style parsing Error");
                    }
                } catch (Resources.NotFoundException e) {
                    Log.e(TAG, "STYLE: Style not found", e);
                }
                
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15f));
                marker = map.addMarker(new MarkerOptions().position(coordinates));
            });
        } else {
            updateMapWithCoordinates(coordinates);
        }
    }
    
    private void updateMapWithCoordinates(LatLng cor) {
        marker.setPosition(cor);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(cor, 18f));
        if (mapPanel.getVisibility() == View.GONE) {
            mapPanel.setVisibility(View.VISIBLE);
        }
    }
    
    //----------------------------------------SET UP TOOL FUNCTIONS---------------------------------
    private void setApplyButtonState(View view, boolean isEnable) {
        view.setEnabled(isEnable);
    }
    
    @SuppressLint("SetTextI18n")
    private void generalDrawerSetUp() {
        drawerLayout = binding.drawer;
        navigationView = binding.navigationView;
        
        binding.toolbarId.toolbarTitleId.setText("Event Details");
        binding.toolbarId.backIcon.setVisibility(View.VISIBLE);
        
        View headerView = binding.navigationView.getHeaderView(0);
        TextView navName = headerView.findViewById(R.id.nav_name);
        TextView navEmail = headerView.findViewById(R.id.nav_email);
        drawerLayout.closeDrawer(GravityCompat.START);
        
        navigationView.bringToFront();
        binding.toolbarId.backIcon.setOnClickListener(view -> finish());
        
        userViewModel
                .getUserDataById(userViewModel.getCurrentUserId())
                .observe(this, user -> {
                    navName.setText(user.getUsername());
                    navEmail.setText(user.getEmail());
                });
        
        ActionBarDrawerToggle drawerToggle =
                new ActionBarDrawerToggle(
                        this,
                        drawerLayout,
                        R.string.open,
                        R.string.close
                );
        drawerLayout.addDrawerListener(drawerToggle);
        
        binding.toolbarId.menuIconId.setOnClickListener(view -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
                
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }
    
    private void setUpDrawer() {
        generalDrawerSetUp();
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.general_menu);
        navigationView.setCheckedItem(R.id.nav_event);
        
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.nav_home) {
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                
            } else if (menuItem.getItemId() == R.id.nav_event) {
                finish();
                return false;
                
            } else if (menuItem.getItemId() == R.id.nav_notification) {
                Toast.makeText(this, "NOTIFICATION", Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawer(GravityCompat.START);
                
            } else if (menuItem.getItemId() == R.id.nav_request) {
                Toast.makeText(this, "REQUEST", Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawer(GravityCompat.START);
                
            } else if (menuItem.getItemId() == R.id.nav_profile) {
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                
            } else if (menuItem.getItemId() == R.id.nav_logout) {
                Intent intent = new Intent(this, OnBoardingActivity.class);
                finish();
                userViewModel.signOut();
                startActivity(intent);
            }
            return false;
        });
    }
}