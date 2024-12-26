package com.sontung.blood.utils;

import java.util.Calendar;
import java.util.Date;

public class DateComparer {
    public static boolean isEventDatePassed(Date eventDate) {
        Calendar eventCal = Calendar.getInstance();
        eventCal.setTime(eventDate);
        
        Calendar currentCal = Calendar.getInstance();
        currentCal.setTime(new Date());
        
        return eventCal.get(Calendar.YEAR) != currentCal.get(Calendar.YEAR) ||
                eventCal.get(Calendar.MONTH) != currentCal.get(Calendar.MONTH) ||
                eventCal.get(Calendar.DAY_OF_MONTH) < currentCal.get(Calendar.DAY_OF_MONTH);
    }
}
