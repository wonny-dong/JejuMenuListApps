package com.dongwon.menulist.type;

import android.os.Parcel;
import android.os.Parcelable;
import com.j256.ormlite.field.DatabaseField;

/**
 * Created by Dongwon on 2015-05-23.
 */
public class CafeteriaMenu implements Parcelable {
    @DatabaseField(generatedId = true)
    int id = -1;
    @DatabaseField
    String name;
    @DatabaseField
    int price;

    public CafeteriaMenu() {
    }

    public CafeteriaMenu(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.price);
    }

    private CafeteriaMenu(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.price = in.readInt();
    }

    public static final Parcelable.Creator<CafeteriaMenu> CREATOR = new Parcelable.Creator<CafeteriaMenu>() {
        public CafeteriaMenu createFromParcel(Parcel source) {
            return new CafeteriaMenu(source);
        }

        public CafeteriaMenu[] newArray(int size) {
            return new CafeteriaMenu[size];
        }
    };
}
