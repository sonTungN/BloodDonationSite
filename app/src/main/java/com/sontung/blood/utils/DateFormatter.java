package com.sontung.blood.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {
    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    
    public static String toDateString(Date date) {
        return dateFormat.format(date);
    }
    
    public static Date toDate(String date) {
        try {
            return dateFormat.parse(date);
            
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
