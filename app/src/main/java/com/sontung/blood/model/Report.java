package com.sontung.blood.model;

import java.util.Date;

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
public class Report {
    private String reportId;
    private String userId;
    private String siteId;
    private String bloodType;
    private String bloodVolume;
    private String note;
    
    @Builder.Default private Date createdDate = new Date();
}
