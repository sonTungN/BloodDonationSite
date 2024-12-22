package com.sontung.blood.utils;

public class FieldValidation {
    public static boolean isValidStringInRange(String input, int min, int max) {
        return input.length() >= min && input.length() <= max;
    }
    
    public static boolean isValidNumberInRange(int number, int min, int max) {
        return number >= min && number <= max;
    }
}
