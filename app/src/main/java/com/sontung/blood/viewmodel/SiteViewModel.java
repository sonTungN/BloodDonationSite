package com.sontung.blood.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.sontung.blood.model.Site;
import com.sontung.blood.repo.SiteRepository;

import java.util.List;

public class SiteViewModel extends AndroidViewModel {
    private final SiteRepository siteRepository;
    
    public SiteViewModel(@NonNull Application application) {
        super(application);
        this.siteRepository = new SiteRepository(getApplication());
    }
    
    public MutableLiveData<List<Site>> getAllSiteData() {
        return siteRepository.getAllSiteData();
    }
    
    public MutableLiveData<Site> getSiteDataById(String siteId) {
        return siteRepository.getSiteDataById(siteId);
    }
    
    public MutableLiveData<List<Site>> getUserRegisteredSite(String userId) {
        return siteRepository.getUserRegisteredSite(userId);
    }
    
    public MutableLiveData<List<Site>> getUserVolunteerSite(String userId) {
        return siteRepository.getUserVolunteerSite(userId);
    }
    
    public MutableLiveData<List<Site>> getUserHostedSite(String userId) {
        return siteRepository.getUserHostedSite(userId);
    }
}
