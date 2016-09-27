package com.dongwon.menulist.util;

import android.util.Log;

/**
 * Created by dongwon-dev on 2015-04-21.
 */
public class Logger {
    private static final String LOG_TITLE = "dongwon";

    public static void d(String content){
        if(content != null){
            Log.d(LOG_TITLE, content);
        }else{
            Log.d(LOG_TITLE, locate());
        }
    }

    public static void e(String content){
        if(content != null){
            Log.e(LOG_TITLE, content);
        }else{
            Log.e(LOG_TITLE, locate());
        }
    }

    public static void i(String content){
        if(content != null){
            Log.i(LOG_TITLE, content);
        }else{
            Log.i(LOG_TITLE, locate());
        }
    }

    public static void e(Throwable throwable){
        Log.e(LOG_TITLE, "", throwable);
    }

    public static String locate(){
        return "at " + new Throwable().getStackTrace()[2];
    }
}
