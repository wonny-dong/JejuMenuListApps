package com.dongwon.menulist.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.dongwon.menulist.task.ShowMenuNotificationTask;
import com.dongwon.menulist.util.Logger;

/**
 * Created by dongwon-dev on 2015-04-22.
 */
public class CommandReceiver extends BroadcastReceiver{
    public static final String ACTION_SHOW_LUNCH_NOTIFICATION = "action.show.lunch.notification";
    public static final String ACTION_SHOW_DINNER_NOTIFICATION = "action.show.dinner.notification";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        new ShowMenuNotificationTask(context).execute(action);
    }
}
