package com.sontung.blood.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.sontung.blood.R;
import com.sontung.blood.adapter.EventSiteAdapter;
import com.sontung.blood.databinding.ActivityEventBinding;
import com.sontung.blood.model.Site;
import com.sontung.blood.viewmodel.SiteViewModel;
import com.sontung.blood.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

public class EventActivity extends AppCompatActivity {
    
    private ActivityEventBinding binding;
    
    private UserViewModel userViewModel;
    private SiteViewModel siteViewModel;
    
    // Sites
    private RecyclerView siteRecyclerView;
    private EventSiteAdapter siteAdapter;
    private List<Site> siteList;
    
    // Search and Filter
    private SearchView searchView;
    
    // Navbar
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event);
        
        binding = DataBindingUtil.setContentView(this, R.layout.activity_event);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        siteViewModel = new ViewModelProvider(this).get(SiteViewModel.class);
        
        setUpDrawer();
        
        searchView = binding.searchView;
        setUpSiteRecyclerView();
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    
    // Setting up Site Recycler View
    private void setUpSiteRecyclerView() {
        siteRecyclerView = binding.siteRecyclerView;
        siteRecyclerView.setLayoutManager(
                new LinearLayoutManager(
                        this,
                        LinearLayoutManager.VERTICAL,
                        false)
        );
        siteRecyclerView.hasFixedSize();
        siteList = new ArrayList<>();
        
        siteViewModel.getAllSiteData().observe(this, this::setUpSiteToViews);
    }
    
    // Setting up the search engine
    @SuppressLint("NotifyDataSetChanged")
    private void setUpSiteToViews(List<Site> sites) {
        siteList.clear();
        siteList.addAll(sites);
     
        siteAdapter = new EventSiteAdapter(getApplicationContext(), siteList);
        siteRecyclerView.setAdapter(siteAdapter);
        siteAdapter.notifyDataSetChanged();
        
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            
            @Override
            public boolean onQueryTextChange(String query) {
                siteAdapter.getFilter().filter(query);
                return false;
            }
        });
    }
    
    @SuppressLint("SetTextI18n")
    private void generalDrawerSetUp() {
        drawerLayout = binding.drawer;
        navigationView = binding.navigationView;
        
        binding.toolbarId.toolbarTitleId.setText("Event Sites");
        binding.toolbarId.backIcon.setVisibility(View.GONE);
        
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