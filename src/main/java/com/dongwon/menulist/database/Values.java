package com.dongwon.menulist.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import com.dongwon.menulist.MenuListApp;
import com.dongwon.menulist.type.CafeteriaLogData;
import com.dongwon.menulist.util.Logger;
import com.dongwon.menulist.util.TrackHelper;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by dongwon-dev on 2015-04-22.
 */
public class Values {
    public interface Type<T>{
        void init(SharedPreferences.Editor editor);
        void put(SharedPreferences.Editor editor,T data);
        String name();
        T get(SharedPreferences preferences);
    }

    public enum BoolType implements  Type<Boolean>{
        UseAlarmForLunch(true),
        UseAlarmForDinner(true),
        isNewApkUpdate(false)
        ;

        private boolean defaultValue;
        BoolType(boolean value){
            defaultValue = value;
        }

        @Override
        public void init(SharedPreferences.Editor editor) {
            editor.putBoolean(name(), defaultValue);
        }

        @Override
        public void put(SharedPreferences.Editor editor, Boolean data){
            editor.putBoolean(name(), data);
        }

        @Override
        public Boolean get(SharedPreferences preferences) {
            return preferences.getBoolean(name(), defaultValue);
        }
    }

    public enum StringType implements  Type<String>{
        AlarmShowTimeForLunch("11:50"),
        AlarmShowTimeForDinner("18:00"),
        MenuFileHash("")
        ;

        private String defaultValue;
        StringType(String value){
            defaultValue = value;
        }

        @Override
        public void init(SharedPreferences.Editor editor) {
            editor.putString(name(), defaultValue);
        }

        @Override
        public void put(SharedPreferences.Editor editor, String data) {
            editor.putString(name(), data);
        }

        @Override
        public String get(SharedPreferences preferences) {
            return preferences.getString(name(), defaultValue);
        }
    }

    public enum IntType implements  Type<Integer>{
        lastVersion(0),
        CafeteriaMoney(0),
        ;

        private int defaultValue;
        IntType(int value){
            defaultValue = value;
        }

        @Override
        public void init(SharedPreferences.Editor editor) {
            editor.putInt(name(), defaultValue);
        }

        @Override
        public void put(SharedPreferences.Editor editor, Integer data) {
            editor.putInt(name(), data);
        }

        @Override
        public Integer get(SharedPreferences preferences) {
            return preferences.getInt(name(), defaultValue);
        }
    }

    private static Values instance;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private boolean isReady = false;

    public static Values getInstance(){
        if(instance == null){
            synchronized (Values.class){
                if(instance == null){
                    instance = new Values();
                }
            }
        }
        return instance;
    }
    private Values() {}

    public synchronized void init(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();

        int nowVersion = 0;
        try {
            nowVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            TrackHelper.sendException(e);
        }
        if(get(IntType.lastVersion) != nowVersion){
            Type[][] typeValues = {BoolType.values(), StringType.values(), IntType.values()};
            for(Type[] types : typeValues){
                for(Type t : types){
                    if(preferences.contains(t.name()) == false){
                        t.init(editor);
                    }
                }
            }
            productUpdate(get(IntType.lastVersion), nowVersion);
            put(BoolType.isNewApkUpdate, false);
            put(IntType.lastVersion, nowVersion);
            editor.apply();
        }
        isReady = true;
    }

    private void productUpdate(int pre, int now){
        if(pre < 103){
            try {
                int total = 0;
                List<CafeteriaLogData> list = new DataBaseHelper(MenuListApp.getInstance()).getCafeteriaLogDataDao().queryForAll();
                for(CafeteriaLogData data : list){
                    total += data.getPrice();
                }
                put(IntType.CafeteriaMoney,  total);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void put(BoolType tag, boolean data){
        if(isReady == false){
            synchronized (this){}
        }
        tag.put(editor, data);
        editor.commit();
    }

    public boolean get(BoolType tag){
        if(isReady == false){
            synchronized (this){}
        }
        return tag.get(preferences);
    }

    public void put(StringType tag, String data){
        if(isReady == false){
            synchronized (this){}
        }
        tag.put(editor, data);
        editor.commit();
    }

    public String get(StringType tag){
        if(isReady == false){
            synchronized (this){}
        }
        return tag.get(preferences);
    }

    public void put(IntType tag, int data){
        if(isReady == false){
            synchronized (this){}
        }
        tag.put(editor, data);
        editor.commit();
    }

    public int get(IntType tag){
        if(isReady == false){
            synchronized (this){}
        }
        return tag.get(preferences);
    }
}
