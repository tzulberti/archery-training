package ar.com.tzulberti.archerytraining.helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by tzulberti on 4/19/17.
 */

public class DatetimeHelper {

    public static final SimpleDateFormat DATETIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("HH:mm");

    public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");


    public static long getTodayZeroHours() {
        Calendar date = new GregorianCalendar();
        return DatetimeHelper.getTimeInMillis(date);
    }

    public static long getTodayLastSecond() {
        Calendar date = new GregorianCalendar();
        return DatetimeHelper.getTimeInMillisEndOfDate(date);
    }



    public static long getTomorrowZeroHours() {
        Calendar date = new GregorianCalendar();
        date.add(Calendar.DAY_OF_MONTH, 1);
        return DatetimeHelper.getTimeInMillis(date);
    }

    public static long getFirstDateOfMonth() {
        Calendar date = new GregorianCalendar();
        date.set(Calendar.DAY_OF_MONTH, 1);
        return DatetimeHelper.getTimeInMillis(date);
    }

    public static long getLastDateOfMonth() {
        Calendar date = new GregorianCalendar();
        date.set(Calendar.DATE, date.getActualMaximum(Calendar.DATE));
        return DatetimeHelper.getTimeInMillisEndOfDate(date);
    }

    public static long getLastWeeDate() {
        Calendar date = new GregorianCalendar();
        date.add(Calendar.DATE, -7);
        return DatetimeHelper.getTimeInMillis(date);
    }

    public static long getDateInMillis(int year, int month, int date, boolean beginning) {
        Calendar res = new GregorianCalendar();
        res.set(Calendar.DATE, date);
        res.set(Calendar.YEAR, year);
        res.set(Calendar.MONTH, month);
        if (beginning) {
            return DatetimeHelper.getTimeInMillis(res);
        } else {
            return DatetimeHelper.getTimeInMillisEndOfDate(res);
        }
    }

    private static long getTimeInMillis(Calendar date) {
        // reset hour, minutes, seconds and millis
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        return date.getTimeInMillis() / 1000;
    }

    private static long getTimeInMillisEndOfDate(Calendar date) {
        // reset hour, minutes, seconds and millis
        date.set(Calendar.HOUR_OF_DAY, 23);
        date.set(Calendar.MINUTE, 59);
        date.set(Calendar.SECOND, 59);
        date.set(Calendar.MILLISECOND, 999);

        return date.getTimeInMillis() / 1000;
    }



    public static long getCurrentTime() {
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeZone(TimeZone.getDefault());
        return calendar.getTimeInMillis() / 1000;
    }

    public static Date databaseValueToDate(long utcSeconds) {
        return new Date(utcSeconds * 1000);
    }

    public static Calendar databaseValueToCalendar(long utcSeconds) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(utcSeconds * 1000);
        return calendar;
    }

    public static long dateToDatabaseValue(Date datetime) {
        return datetime.getTime() / 1000;
    }

}
