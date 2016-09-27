package com.dongwon.menulist;

import android.app.Application;
import android.content.Intent;
import com.dongwon.menulist.database.Values;
import com.dongwon.menulist.service.ScheduleManagedService;
import com.dongwon.menulist.util.TrackHelper;
import com.google.android.gms.analytics.ExceptionReporter;

public class MenuListApp extends Application{
    private static MenuListApp instance;

    public static MenuListApp getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Values.getInstance().init(this);
        settingUncaughtException();
        startService(new Intent(this, ScheduleManagedService.class));
    }

    private void settingUncaughtException(){
        Thread.UncaughtExceptionHandler myHandler = new ExceptionReporter(
                TrackHelper.getTracker(),                                        // Currently used Tracker.
                Thread.getDefaultUncaughtExceptionHandler(),      // Current default uncaught exception handler.
                this);                                         // Context of the application.
        Thread.setDefaultUncaughtExceptionHandler(myHandler);
    }
}
