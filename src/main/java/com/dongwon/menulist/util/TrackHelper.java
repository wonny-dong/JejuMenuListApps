package com.dongwon.menulist.util;

import android.content.Context;
import com.dongwon.menulist.MenuListApp;
import com.dongwon.menulist.R;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by dongwon-dev on 2015-05-06.
 */
public class TrackHelper {
    private static Tracker tracker;

    public static Tracker getTracker() {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(MenuListApp.getInstance());
        if(tracker == null){
            synchronized (TrackHelper.class){
                if(tracker == null){
                    tracker = analytics.newTracker(R.xml.global_tracker);
                }
            }
        }
        return tracker;
    }

    public static void sendScreen(Class cls){
        Tracker tracker = getTracker();
        tracker.setScreenName(cls.getSimpleName());
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public static void sendException(Throwable throwable){
        Tracker tracker = getTracker();
        tracker.send(new HitBuilders.ExceptionBuilder()
            .setDescription(throwable.getStackTrace()[0].toString())
            .setFatal(false)
            .build());
    }

    public static void sendEvent(String category, String action, String label){
        Tracker tracker = getTracker();
        HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder(category, action);
        if(label != null){
            eventBuilder.setLabel(label);
        }

        tracker.send(eventBuilder.build());
    }
}
