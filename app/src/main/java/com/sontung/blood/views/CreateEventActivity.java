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
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.sontung.blood.R;
import com.sontung.blood.adapter.CreateEventAdapter;
import com.sontung.blood.databinding.ActivityCreateEventBinding;
import com.sontung.blood.databinding.ActivityHomeBinding;
import com.sontung.blood.viewmodel.UserViewModel;

import java.util.Objects;

public class CreateEventActivity extends AppCompatActivity {
    
    private ActivityCreateEventBinding binding;
    
    private UserViewModel userViewModel;
    
    private CreateEventAdapter adapter;
    
    // Navbar
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_event);
        
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_event);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        
        adapter = new CreateEventAdapter(this);
        binding.pageContent.setAdapter(adapter);
        
        setUpDrawer();
        
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.pageContent.setCurrentItem(tab.getPosition());
            }
            
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        
        binding.pageContent.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Objects.requireNonNull(binding.tabLayout.getTabAt(position)).select();
            }
        });
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    
    @SuppressLint("SetTextI18n")
    private void generalDrawerSetUp() {
        drawerLayout = binding.drawer;
        navigationView = binding.navigationView;
        
        binding.toolbarId.toolbarTitleId.setText("Event Details");
        binding.toolbarId.backIcon.setVisibility(View.GONE);
        
        View headerView = binding.navigationView.getHeaderView(0);
        TextView navName = headerView.findViewById(R.id.nav_name);
        TextView navEmail = headerView.findViewById(R.id.nav_email);
        ImageView navProfileImg = headerView.findViewById(R.id.profile_image);
        drawerLayout.closeDrawer(GravityCompat.START);
        
        navigationView.bringToFront();
        binding.toolbarId.backIcon.setOnClickListener(view -> finish());
        
        userViewModel
                .getUserDataById(userViewModel.getCurrentUserId())
                .observe(this, user -> {
                    navName.setText(user.getUsername());
                    navEmail.setText(user.getEmail());
                    
                    Glide.with(getApplicationContext())
                            .load(user.getProfileUrl())
                            .into(navProfileImg);
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
        navigationView.setCheckedItem(R.id.nav_my_event);
        
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.nav_home) {
                Intent intent = new Intent(this, HomeActivity.class);
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(intent);
                
            } else if (menuItem.getItemId() == R.id.nav_event) {
                Intent intent = new Intent(this, EventActivity.class);
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(intent);
                
            } else if (menuItem.getItemId() == R.id.nav_my_event) {
                drawerLayout.closeDrawer(GravityCompat.START);
                finish();
                return true;
                
            } else if (menuItem.getItemId() == R.id.nav_notification) {
                Toast.makeText(this, "NOTIFICATION", Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawer(GravityCompat.START);
                
            } else if (menuItem.getItemId() == R.id.nav_profile) {
                Intent intent = new Intent(this, ProfileActivity.class);
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(intent);
                
            } else if (menuItem.getItemId() == R.id.nav_about_us) {
                Toast.makeText(this, "ABOUT US", Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawer(GravityCompat.START);
                
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