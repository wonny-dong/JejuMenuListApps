package com.dongwon.menulist.type;

/**
 * Created by dongwon-dev on 2015-04-21.
 */
public class DayMenuJson {
    int month;
    int day;
    int year;
    String[] lunchMenus;
    String[] dinnerMenus;
    boolean isHoliday;

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getYear() {
        return year;
    }

    public String[] getLunchMenus() {
        return lunchMenus;
    }

    public String[] getDinnerMenus() {
        return dinnerMenus;
    }

    public boolean isHoliday() {
        return isHoliday;
    }
}
