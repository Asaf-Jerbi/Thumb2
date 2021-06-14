package com.example.thumb2;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Helper {
    public enum Validation {VALID, INVALID}

    enum UserType {SOLDIER, DRIVER}


    public String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        String hour = Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
        String minute = Integer.toString(calendar.get(Calendar.MINUTE));

        return hour + ":" + minute;
    }
}
