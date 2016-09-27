package com.dongwon.menulist.type;

import com.j256.ormlite.field.DatabaseField;

import java.util.Date;

/**
 * Created by dongwon-dev on 2015-04-20.
 */
public class MealMenu {
    public enum Distinction {Lunch, Dinner}

    @DatabaseField(generatedId = true)
    int id;
    @DatabaseField
    private String menus;
    @DatabaseField
    private Date date;
    @DatabaseField
    private Distinction distinction;
    @DatabaseField
    private boolean isHoliday;
    private String[] menuArray;

    public MealMenu() {}

    public MealMenu(String[] menus, Date date, Distinction distinction, boolean isHoliday) {
        StringBuilder sb = new StringBuilder();
        if(menus != null){
            for(String menu : menus){
                sb.append(menu).append(";");
            }
        }
        this.menus = sb.toString();
        this.menuArray = menus;
        this.date = date;
        this.distinction = distinction;
        this.isHoliday = isHoliday;
    }

    public String[] getMenus() {
        if(menuArray == null){
            menuArray = menus.split(";");
        }
        return menuArray;
    }

    public Date getDate() {
        return date;
    }

    public Distinction getDistinction() {
        return distinction;
    }

    public boolean isHoliday() {
        return isHoliday;
    }
}
