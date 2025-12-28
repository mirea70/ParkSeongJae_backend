package com.wirebarly.utils;

public class MyStringUtils {
    public static boolean isPositiveNumber(String input) {
        if(isEmpty(input)) return false;

        return input.matches("^0*[1-9]\\d*$");
    }

    public static boolean isEmpty(String input) {
        return input == null || input.isBlank();
    }
}
