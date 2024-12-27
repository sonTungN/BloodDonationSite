package com.sontung.blood.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.sontung.blood.R;
import com.sontung.blood.adapter.EventProfileAdapter;
import com.sontung.blood.callback.FirebaseCallback;
import com.sontung.blood.databinding.ActivityHomeBinding;
import com.sontung.blood.model.Site;
import com.sontung.blood.model.User;
import com.sontung.blood.viewmodel.SiteViewModel;
import com.sontung.blood.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;

    private UserViewModel userViewModel;
    private SiteViewModel siteViewModel;
    
    private RecyclerView hostRecyclerView;
    private List<Site> hostSiteList = new ArrayList<>();
    
    private RecyclerView recentRecyclerView;
    private List<Site> recentSiteList = new ArrayList<>();
    
    private RecyclerView registeredRecyclerView;
    private List<Site> registeredSiteList = new ArrayList<>();
    
    private EventProfileAdapter adapter;

    // Navbar
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        siteViewModel = new ViewModelProvider(this).get(SiteViewModel.class);
        
        binding.noHostEventDisplay.setVisibility(View.GONE);
        binding.noEventDisplay.setVisibility(View.GONE);
        binding.discoverMoreBtn.setVisibility(View.GONE);
        
        binding.loadingHostLayout.setVisibility(View.VISIBLE);
        binding.loadingRegisteredLayout.setVisibility(View.VISIBLE);
        binding.loadingRecentLayout.setVisibility(View.VISIBLE);
        
        setUpDrawer();
        setUpRecyclerView();
        setUpButtonClickHandler();
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    
    private void setUpButtonClickHandler() {
        binding.createEventActivityCta.setOnClickListener(view -> {
            Intent i = new Intent(this, CreateEventActivity.class);
            startActivity(i);
            finish();
        });
        
        binding.discoverMoreBtn.setOnClickListener(view -> {
            Intent i = new Intent(this, EventActivity.class);
            startActivity(i);
            finish();
        });
        
        binding.eventActivityCta.setOnClickListener(view -> {
            Intent i = new Intent(this, EventActivity.class);
            startActivity(i);
            finish();
        });
    }
    
    private void setUpRecyclerView() {
        setUpRegisteredRecyclerView();
        setUpRecentRecyclerView();
        setUpHostRecyclerView();
    }
    
    @SuppressLint("NotifyDataSetChanged")
    private void setUpHostRecyclerView() {
        siteViewModel
                .getUserHostedSite(userViewModel.getCurrentUserId())
                .observe(this, site -> {
                    binding.loadingHostLayout.setVisibility(View.GONE);
                    if (site == null) {
                        binding.noHostEventDisplay.setVisibility(View.VISIBLE);
                        return;
                        
                    } else {
                        binding.noHostEventDisplay.setVisibility(View.GONE);
                    }
                    
                    hostSiteList.clear();
                    hostSiteList.add(site);
                    
                    hostRecyclerView= binding.hostRecyclerView;
                    hostRecyclerView.setLayoutManager(
                            new LinearLayoutManager(
                                    getApplicationContext(),
                                    LinearLayoutManager.VERTICAL,
                                    false)
                    );
                    hostRecyclerView.hasFixedSize();
                    
                    adapter = new EventProfileAdapter(this, hostSiteList);
                    hostRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                });
    }
    
    @SuppressLint("NotifyDataSetChanged")
    private void setUpRegisteredRecyclerView() {
        siteViewModel
                .getUserRegisteredSite(userViewModel.getCurrentUserId())
                .observe(this, sites -> {
                    binding.loadingRegisteredLayout.setVisibility(View.GONE);
                    
                    if (sites.isEmpty()) {
                        binding.noEventDisplay.setVisibility(View.VISIBLE);
                    } else {
                        binding.noEventDisplay.setVisibility(View.GONE);
                    }
                    
                    registeredSiteList.clear();
                    registeredSiteList.addAll(sites);
                    
                    registeredRecyclerView= binding.registeredRecyclerView;
                    registeredRecyclerView.setLayoutManager(
                            new LinearLayoutManager(
                                    getApplicationContext(),
                                    LinearLayoutManager.VERTICAL,
                                    false)
                    );
                    registeredRecyclerView.hasFixedSize();
                    
                    adapter = new EventProfileAdapter(this, registeredSiteList);
                    registeredRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                });
    }
    
    @SuppressLint("NotifyDataSetChanged")
    private void setUpRecentRecyclerView() {
        siteViewModel
                .getAllSiteData()
                .observe(this, sites -> {
                    binding.loadingRecentLayout.setVisibility(View.GONE);
                    binding.discoverMoreBtn.setVisibility(View.VISIBLE);
                    
                    recentSiteList.clear();
                    int count = 0;
                    for (Site site: sites) {
                        recentSiteList.add(site);
                        count++;
                        if (count == 2) break;
                    }
                    
                    recentRecyclerView= binding.recentRecyclerView;
                    recentRecyclerView.setLayoutManager(
                            new LinearLayoutManager(
                                    getApplicationContext(),
                                    LinearLayoutManager.VERTICAL,
                                    false)
                    );
                    recentRecyclerView.hasFixedSize();
                    
                    adapter = new EventProfileAdapter(this, recentSiteList);
                    recentRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                });
    }
    
    @SuppressLint("SetTextI18n")
    private void generalDrawerSetUp() {
        drawerLayout = binding.drawer;
        navigationView = binding.navigationView;
        
        binding.toolbarId.toolbarTitleId.setText("Dashboard");
        binding.toolbarId.backIcon.setVisibility(View.GONE);
        
        View headerView = binding.navigationView.getHeaderView(0);
        TextView navName = headerView.findViewById(R.id.nav_name);
        TextView navEmail = headerView.findViewById(R.id.nav_email);
        TextView navUserRole = headerView.findViewById(R.id.nav_user_role);
        ImageView navProfileImg = headerView.findViewById(R.id.profile_image);
        drawerLayout.closeDrawer(GravityCompat.START);
        
        navigationView.bringToFront();
        binding.toolbarId.backIcon.setOnClickListener(view -> finish());
        
        userViewModel.getUserDataById(userViewModel.getCurrentUserId(), new FirebaseCallback<>() {
            @Override
            public void onSuccess(List<User> t) {
            
            }
            
            @Override
            public void onSuccess(User user) {
                navName.setText(user.getUsername());
                navEmail.setText(user.getEmail());
                navUserRole.setText(user.getUserRole());
                
                Glide.with(getApplicationContext())
                        .load(user.getProfileUrl())
                        .into(navProfileImg);
            }
            
            @Override
            public void onFailure(List<User> t) {
            
            }
            
            @Override
            public void onFailure(User user) {
            
            }
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
        navigationView.setCheckedItem(R.id.nav_home);
        
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.nav_home) {
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
                
            } else if (menuItem.getItemId() == R.id.nav_event) {
                Intent intent = new Intent(this, EventActivity.class);
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(intent);
                
            } else if (menuItem.getItemId() == R.id.nav_my_event) {
                Intent intent = new Intent(this, CreateEventActivity.class);
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(intent);
                
            } else if (menuItem.getItemId() == R.id.nav_notification) {
                Intent intent = new Intent(this, NotificationActivity.class);
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(intent);
                
            } else if (menuItem.getItemId() == R.id.nav_about_us) {
                Intent intent = new Intent(this, GuidelineActivity.class);
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(intent);
                
            } else if (menuItem.getItemId() == R.id.nav_logout) {
                Intent intent = new Intent(this, OnBoardingActivity.class);
                finish();
                userViewModel.signOut();
                startActivity(intent);
            }
            return true;
        });
    }
    
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}