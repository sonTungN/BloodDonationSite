package com.sontung.blood.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Site {
    private String host;
    private String siteId;
    private String siteName;
    private String siteDesc;
    private String siteAddress;
    @Builder.Default private List<String> siteImageUrl = new ArrayList<>();
    
    private String requiredBloodType;
    private int donorMaxCapacity;
    private int volunteerMaxCapacity;
    
    private String latitude;
    private String longitude;
    
    private String siteStatus;
    
    @Builder.Default private List<String> listOfDonors = new ArrayList<>();
    @Builder.Default private List<String> listOfVolunteers = new ArrayList<>();
    @Builder.Default private List<String> listOfReports = new ArrayList<>();
    @Builder.Default private List<String> listOfRequests = new ArrayList<>();
    
    @Builder.Default private Date eventDate = new Date();
    @Builder.Default private Date updatedDate = new Date();
}
