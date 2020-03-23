package com.koopey.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeHelper {

    private final static String LOG_HEADER = "DATETIME:HELPER";

    public static Date StartOfToday(){
        Date today = new Date();
        today.setHours(0);
        today.setSeconds(0);
        return today;
    }

    public static String epochToString(long epoch, String timeZone) {
        Date date = new Date(epoch);
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone(timeZone));
        return format.format(date);
    }
    public static Date epochToDate(long epoch) {
        return new Date(epoch);
    }

}
