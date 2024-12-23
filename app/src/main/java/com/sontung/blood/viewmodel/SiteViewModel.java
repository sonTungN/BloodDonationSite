package com.sontung.blood.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FieldValue;
import com.sontung.blood.callback.FirebaseCallback;
import com.sontung.blood.model.Site;
import com.sontung.blood.repo.SiteRepository;

import java.util.List;
import java.util.Objects;

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
    
    public MutableLiveData<Site> getUserHostedSite(String userId) {
        return siteRepository.getUserHostedSite(userId);
    }
    
    public void createNewSite(Site site, FirebaseCallback<Site> callback) {
        siteRepository.createNewSite(site, callback);
    }
    
    public void updateSiteId(String siteId, Site site) {
        siteRepository.updateSiteId(siteId, site);
    }
    
    public void updateSiteImages(String siteId, Site site) {
        siteRepository.updateSiteImages(siteId, site);
    }
    
    public void addUserIntoSiteRegisteredList(String userId, String siteId, FirebaseCallback<Boolean> callback) {
        siteRepository.addUserIntoSiteRegisteredList(userId, siteId, callback);
    }
    
    public void addUserIntoSiteVolunteerList(String userId, String siteId, FirebaseCallback<Boolean> callback) {
        siteRepository.addUserIntoSiteVolunteerList(userId, siteId, callback);
    }
}
