package com.sontung.blood.utils;

import java.util.regex.Pattern;

public class FieldValidation {
    public static boolean isValidStringInRange(String input, int min, int max) {
        return input.length() < min || input.length() > max;
    }
    
    public static boolean isValidNumberInRange(int number, int min, int max) {
        return number < min || number > max;
    }
    
    public static boolean isValidEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return Pattern.matches(emailPattern, email);
    }
}
