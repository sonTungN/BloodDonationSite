package com.sontung.blood.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.sontung.blood.callback.FirebaseCallback;
import com.sontung.blood.model.Report;
import com.sontung.blood.model.Site;
import com.sontung.blood.repo.ReportRepository;
import com.sontung.blood.repo.SiteRepository;

import java.util.List;

public class ReportViewModel extends AndroidViewModel {
    private final ReportRepository reportRepository;
    
    public ReportViewModel(@NonNull Application application) {
        super(application);
        this.reportRepository = new ReportRepository(getApplication());
    }
    
    public void createReport(Report report, FirebaseCallback<Report> callback) {
        reportRepository.createReport(report, callback);
    }
}
