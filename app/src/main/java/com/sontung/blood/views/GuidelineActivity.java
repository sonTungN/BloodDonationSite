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
import com.google.android.material.tabs.TabLayoutMediator;
import com.sontung.blood.R;
import com.sontung.blood.adapter.GuidelineTabAdapter;
import com.sontung.blood.adapter.OnBoardingItemAdapter;
import com.sontung.blood.callback.FirebaseCallback;
import com.sontung.blood.databinding.ActivityGuidelineBinding;
import com.sontung.blood.model.OnBoardingItem;
import com.sontung.blood.model.User;
import com.sontung.blood.viewmodel.UserViewModel;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GuidelineActivity extends AppCompatActivity {
    
    private ActivityGuidelineBinding binding;
    
    private UserViewModel userViewModel;
    
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_guideline);
        
        binding = DataBindingUtil.setContentView(this, R.layout.activity_guideline);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        
        GuidelineTabAdapter guidelineTabAdapter = new GuidelineTabAdapter(this);
        binding.pageContent.setAdapter(guidelineTabAdapter);
        
        new TabLayoutMediator(binding.tabLayout, binding.pageContent, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("QUICK START");
                    break;
                case 1:
                    tab.setText("CONTACT US");
                    break;
            }
        }).attach();
        
        binding.pageContent.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Objects.requireNonNull(binding.tabLayout.getTabAt(position)).select();
            }
        });
        
        setUpDrawer();
        
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
        
        binding.toolbarId.toolbarTitleId.setText("Guideline");
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
        navigationView.setCheckedItem(R.id.nav_about_us);
        
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
                Intent intent = new Intent(this, CreateEventActivity.class);
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(intent);
                
            } else if (menuItem.getItemId() == R.id.nav_notification) {
                Intent intent = new Intent(this, NotificationActivity.class);
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(intent);
                
            } else if (menuItem.getItemId() == R.id.nav_about_us) {
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
                
            } else if (menuItem.getItemId() == R.id.nav_logout) {
                Intent intent = new Intent(this, OnBoardingActivity.class);
                finish();
                userViewModel.signOut();
                startActivity(intent);
            }
            return true;
        });
    }
}