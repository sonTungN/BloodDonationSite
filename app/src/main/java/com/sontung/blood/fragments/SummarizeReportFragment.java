package com.sontung.blood.fragments;

import android.annotation.SuppressLint;
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
import com.sontung.blood.databinding.FragmentSummarizeReportBinding;
import com.sontung.blood.model.Report;
import com.sontung.blood.viewmodel.ReportViewModel;
import com.sontung.blood.viewmodel.SiteViewModel;

import java.util.List;

public class SummarizeReportFragment extends DialogFragment {
    
    private static final String ARG_SITE_ID = "siteId";
    
    private FragmentSummarizeReportBinding binding;
    private SiteViewModel siteViewModel;
    private ReportViewModel reportViewModel;
    
    private String siteId;
    
    public static SummarizeReportFragment newInstance(String siteId) {
        SummarizeReportFragment fragment = new SummarizeReportFragment();
        Bundle args = new Bundle();
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
        binding = FragmentSummarizeReportBinding.inflate(getLayoutInflater());
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogTheme);
        
        if (getArguments() != null) {
            siteId = getArguments().getString(ARG_SITE_ID);
        }
        
        siteViewModel = new ViewModelProvider(this).get(SiteViewModel.class);
        reportViewModel = new ViewModelProvider(this).get(ReportViewModel.class);
        
        setUpDataIntoView();
        setUpButtonClickHandler();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflater.inflate(R.layout.fragment_create_report, container, false);
        setUpInitialState();
        return binding.getRoot();
    }
    
    private void setUpInitialState() {
        siteViewModel.getSiteDataById(siteId).observe(this, selectedSite -> {
            binding.setCurrentSite(selectedSite);
            
        });
    }
    
    @SuppressLint("SetTextI18n")
    private void setUpDataIntoView() {
        reportViewModel.getReportDataBySiteId(siteId).observe(this, reports -> {
            double bloodVolume = 0;
            for (Report report: reports) {
                bloodVolume += Double.parseDouble(report.getBloodVolume());
            }
            
            binding.overviewBloodVolume.setText(bloodVolume + " ML");
        });
    }
    
    private void setUpButtonClickHandler() {
        binding.exitButton.setOnClickListener(view -> dismiss());
        binding.closeOverviewCard.setOnClickListener(v -> dismiss());
    }
}