package com.sontung.blood.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sontung.blood.R;
import com.sontung.blood.callback.FirebaseCallback;
import com.sontung.blood.databinding.FragmentCreateReportBinding;
import com.sontung.blood.model.Report;
import com.sontung.blood.model.User;
import com.sontung.blood.utils.FieldValidation;
import com.sontung.blood.viewmodel.ReportViewModel;
import com.sontung.blood.viewmodel.UserViewModel;

import java.util.List;
import java.util.Objects;

public class CreateReportFragment extends DialogFragment {
    
    private static final String ARG_USER_ID = "userId";
    private static final String ARG_SITE_ID = "siteId";
    
    private FragmentCreateReportBinding binding;
    private ReportViewModel reportViewModel;
    private UserViewModel userViewModel;
    
    private String userId;
    private String siteId;
    
    public static CreateReportFragment newInstance(String userId, String siteId) {
        CreateReportFragment fragment = new CreateReportFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        args.putString(ARG_SITE_ID, siteId);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            Window window = getDialog().getWindow();
            if (window != null) {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int screenWidth = displayMetrics.widthPixels;
                
                window.setLayout((int) (screenWidth * 0.9), ViewGroup.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        }
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentCreateReportBinding.inflate(getLayoutInflater());
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogTheme);
        
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
            siteId = getArguments().getString(ARG_SITE_ID);
        }
        
        reportViewModel = new ViewModelProvider(this).get(ReportViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        
        setUpButtonClickHandler();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflater.inflate(R.layout.fragment_create_report, container, false);
        setUpInitialState();
        
        return binding.getRoot();
    }
    
    private void createReport() {
        if (!isReportInputValid()) {
            Toast.makeText(requireContext(), "ERROR: Some input are invalid!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Report pendingCreatedReport =
                Report.builder()
                        .userId(userId)
                        .siteId(siteId)
                        .bloodType(binding.userBloodType.getText().toString())
                        .bloodVolume(String.valueOf(binding.userBloodAmount.getText()))
                        .build();
        
        reportViewModel.createReport(pendingCreatedReport, new FirebaseCallback<>() {
            @Override
            public void onSuccess(List<Report> t) {
            
            }
            
            @Override
            public void onSuccess(Report report) {
                reportViewModel.updateReportId(report.getReportId(), report);
                Toast.makeText(requireContext(), "Create Report Successfully!", Toast.LENGTH_SHORT).show();
                dismiss();
            }
            
            @Override
            public void onFailure(List<Report> t) {
            
            }
            
            @Override
            public void onFailure(Report report) {
            
            }
        });
    }
    
    private boolean isReportInputValid() {
        binding.reportAmountErr.setVisibility(View.GONE);
        int invalidCount = 0;
        String reportAmount = binding.userBloodAmount.getText().toString().trim();
        
        if (reportAmount.isEmpty()) {
            binding.reportAmountErr.setVisibility(View.VISIBLE);
            invalidCount++;
        }
        
        return invalidCount == 0;
    }
    
    private void setUpButtonClickHandler() {
        binding.exitButton.setOnClickListener(view -> dismiss());
        binding.createReportBtn.setOnClickListener(v -> createReport());
    }
    
    private void setUpInitialState() {
        binding.reportAmountErr.setVisibility(View.GONE);
        
        userViewModel.getUserDataById(userId).observe(this, selectedUser -> {
            binding.setUser(selectedUser);
            
            Glide.with(requireContext())
                    .load(selectedUser.getProfileUrl())
                    .into(binding.avatar);
        });
    }
}