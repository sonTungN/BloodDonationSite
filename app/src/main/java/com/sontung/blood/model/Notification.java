package com.sontung.blood.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
public class Notification {
    private String notificationId;
    private String senderId;
    private String senderEmail;
    private String receiverId;
    private String siteId;
    private String title;
    private String desc;
    
    @Builder.Default private Date sentDate = new Date();
    
}
