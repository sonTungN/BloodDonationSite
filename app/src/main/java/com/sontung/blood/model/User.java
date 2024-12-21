package com.sontung.blood.model;

import com.sontung.blood.shared.Types;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private String userId;
    private String username;
    private String email;
    private String password;
    private String bloodType;

    @Builder.Default private String hostedSite = null;
    @Builder.Default private String profileUrl = null;
    @Builder.Default private String userRole = Types.USER_PERMISSION;
    @Builder.Default private List<String> listOfRegisteredSites = new ArrayList<>();
    @Builder.Default private List<String> listOfVolunteerSites = new ArrayList<>();
    @Builder.Default private List<String> listOfRequests = new ArrayList<>();
}