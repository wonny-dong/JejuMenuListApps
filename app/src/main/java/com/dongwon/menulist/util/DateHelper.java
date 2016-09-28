package com.dongwon.menulist.util;

import android.content.Context;
import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class DateHelper {
    public static final long MILLISECOND_SECOND = 1000;
    public static final long MILLISECOND_MINUTE = MILLISECOND_SECOND * 60;
    public static final long MILLISECOND_HOUR = MILLISECOND_MINUTE * 60;
    public static final long MILLISECOND_DAY = MILLISECOND_HOUR * 24;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);

    public static Date getDate(int year, int month, int day, int hour, int min){
        try {
            return dateFormat.parse(String.format(Locale.KOREA, "%4d-%2d-%2d %2d:%2d", year, month, day, hour, min));
        } catch (ParseException e) {
            TrackHelper.sendException(e);
            return new Date();
        }
    }

    public static Date getDate(int year, int month, int day){
        return getDate(year, month, day, 0, 0);
    }

    public static Calendar getMidnightCalendar(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static CharSequence getTimeString(Context context, long value){
        if(DateUtils.isToday(value)){
            return DateUtils.getRelativeTimeSpanString(value, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        }else{
            return DateUtils.formatDateTime(context, value, DateUtils.FORMAT_SHOW_DATE);
        }
    }
}
