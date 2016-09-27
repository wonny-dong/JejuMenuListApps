package com.dongwon.menulist.type;

import com.j256.ormlite.field.DatabaseField;

import java.util.Date;

/**
 * Created by dongwon-dev on 2015-05-06.
 */
public class CafeteriaLogData {
    @DatabaseField(generatedId = true)
    int id;
    @DatabaseField
    Date date;
    @DatabaseField
    int price;
    @DatabaseField
    String itemName;

    public CafeteriaLogData() {
    }

    public CafeteriaLogData(int price, Date date) {
        this("", price, date);
    }

    public CafeteriaLogData(String itemName, int price, Date date) {
        this.itemName = itemName;
        this.price = price;
        this.date = date;
    }

    public String getItemName() {
        return itemName;
    }

    public int getPrice() {
        return price;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        CafeteriaLogData target = (CafeteriaLogData)o;
        return super.equals(o) && id == target.id;
    }
}
