package com.sontung.blood.fragments;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sontung.blood.R;
import com.sontung.blood.callback.FirebaseCallback;
import com.sontung.blood.databinding.FragmentCreateReportBinding;
import com.sontung.blood.model.Report;
import com.sontung.blood.viewmodel.ReportViewModel;

import java.util.List;

public class CreateReportFragment extends DialogFragment {
    
    private static final String ARG_USER_ID = "userId";
    private static final String ARG_SITE_ID = "siteId";
    
    FragmentCreateReportBinding binding;
    private ReportViewModel reportViewModel;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentCreateReportBinding.inflate(getLayoutInflater());
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogTheme);
        
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
            siteId = getArguments().getString(ARG_SITE_ID);
        }
        
        binding.exitButton.setOnClickListener(view -> dismiss());
        binding.createReportBtn.setOnClickListener(v -> createReport());
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflater.inflate(R.layout.fragment_create_report, container, false);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }
    
    private void createReport() {
        Report pendingCreatedReport =
                Report.builder()
                        .userId(userId)
                        .siteId(siteId)
                        .bloodType(binding.userBloodType.getText().toString())
                        .bloodVolume(String.valueOf(binding.userBloodAmount.getText()))
                        .build();
        
        reportViewModel.createReport(pendingCreatedReport, new FirebaseCallback<Report>() {
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
}