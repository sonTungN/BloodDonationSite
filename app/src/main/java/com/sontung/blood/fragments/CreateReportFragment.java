package com.sontung.blood.fragments;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sontung.blood.R;
import com.sontung.blood.databinding.FragmentCreateReportBinding;

public class CreateReportFragment extends DialogFragment {
    
    private static final String ARG_USER_ID = "userId";
    private static final String ARG_SITE_ID = "siteId";
    
    FragmentCreateReportBinding binding;
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
        
        binding.exitButton.setOnClickListener(view -> {
            dismiss();
        });
        
        binding.createReportBtn.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "CREATE REPORT", Toast.LENGTH_SHORT).show();
            Toast.makeText(requireContext(), "userID: " + userId, Toast.LENGTH_SHORT).show();
            Toast.makeText(requireContext(), "siteID: " + siteId, Toast.LENGTH_SHORT).show();
//            dismiss();
        });
        
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflater.inflate(R.layout.fragment_create_report, container, false);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }
}