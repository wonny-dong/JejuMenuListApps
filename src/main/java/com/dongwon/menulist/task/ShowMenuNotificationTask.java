package com.dongwon.menulist.task;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import com.dongwon.menulist.R;
import com.dongwon.menulist.database.DataBaseHelper;
import com.dongwon.menulist.database.Values;
import com.dongwon.menulist.receiver.CommandReceiver;
import com.dongwon.menulist.type.MealMenu;
import com.dongwon.menulist.ui.activity.MainActivity;
import com.dongwon.menulist.util.DateHelper;
import com.dongwon.menulist.util.Logger;
import com.dongwon.menulist.util.TrackHelper;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by dongwon-dev on 2015-04-20.
 */
public class ShowMenuNotificationTask extends AsyncTask<String, Void, MealMenu> {
    private static final int ID_NOTIFICATION_LUNCH = 201;
    private static final int ID_NOTIFICATION_DINNER = 202;

    private Context context;
    private Values values;
    private NotificationManager notificationManager;

    public ShowMenuNotificationTask(Context context) {
        this.context = context;
        this.values = Values.getInstance();
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    protected MealMenu doInBackground(String... params) {
        try {
            MealMenu.Distinction distinction;
            switch (params[0]){
                case CommandReceiver.ACTION_SHOW_LUNCH_NOTIFICATION :
                    distinction = MealMenu.Distinction.Lunch;
                    break;

                case CommandReceiver.ACTION_SHOW_DINNER_NOTIFICATION :
                    distinction = MealMenu.Distinction.Dinner;
                    break;

                default:
                    Logger.d("???????? error -> " + getClass().getSimpleName());
                    return null;
            }

            Calendar calendar = DateHelper.getMidnightCalendar();
            Date start = calendar.getTime();
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            Date last = calendar.getTime();

            DataBaseHelper dataBaseHelper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
            Dao<MealMenu, Integer> mealModelDao = dataBaseHelper.getMealModelDao();
            QueryBuilder<MealMenu, Integer> queryBuilder = mealModelDao.queryBuilder();
            queryBuilder.where().between("date", start, last).and().eq("distinction", distinction);
            List<MealMenu> list = queryBuilder.query();
            OpenHelperManager.releaseHelper();
            if(list.isEmpty()){
                return null;
            }else{
                return list.get(0);
            }
        } catch (SQLException e) {
            TrackHelper.sendException(e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(MealMenu data) {
        super.onPostExecute(data);
        if(data == null){
            return;
        }
        StringBuilder sb = new StringBuilder();
        StringBuilder sbExtended = new StringBuilder();
        for (int i = 0; i < data.getMenus().length; ++i) {
            if (i != 0) {
                sb.append(", ");
                sbExtended.append("\n");
            }
            sb.append(data.getMenus()[i]);
            sbExtended.append(data.getMenus()[i]);
        }
        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.notification_meal_menu);
        remoteView.setTextViewText(R.id.contentView, sb.toString());

        RemoteViews extendRemoteView = new RemoteViews(context.getPackageName(), R.layout.notification_meal_menu_extended);
        extendRemoteView.setTextViewText(R.id.contentView, sbExtended.toString());

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_SELECTED_MENU, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder
                .setAutoCancel(true)
                .setTicker(context.getString(R.string.notification_menu_ticket))
                .setSmallIcon(R.drawable.ic_notification_alarm_small)
                .setContentIntent(PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
                .setDefaults(Notification.DEFAULT_ALL)
                .setLights(Color.GREEN, 2000, 2000)
                .setPriority(Notification.PRIORITY_HIGH);
        if(Build.VERSION.SDK_INT >= 21){
            builder
                .setVisibility(Notification.VISIBILITY_PUBLIC);
        }

        Notification notification = builder.build();
        notification.contentView = remoteView;
        if (Build.VERSION.SDK_INT >= 16) {
            notification.bigContentView = extendRemoteView;
        }
        switch (data.getDistinction()) {
            case Lunch:
                if (values.get(Values.BoolType.UseAlarmForLunch) && data.isHoliday() == false) {
                    remoteView.setTextViewText(R.id.titleView, context.getString(R.string.notification_lunch_title));
                    remoteView.setImageViewResource(R.id.imageView, R.drawable.img_notification_robot_lunch_extended);
                    extendRemoteView.setTextViewText(R.id.titleView, context.getString(R.string.notification_lunch_title));
                    extendRemoteView.setImageViewResource(R.id.imageView, R.drawable.img_notification_robot_lunch_extended);
                    notificationManager.cancel(ID_NOTIFICATION_LUNCH);
                    notificationManager.notify(ID_NOTIFICATION_LUNCH, notification);
                    TrackHelper.sendEvent("Alarm", "Lunch", Values.getInstance().get(Values.StringType.AlarmShowTimeForLunch));
                }
                break;

            case Dinner:
                if (values.get(Values.BoolType.UseAlarmForDinner) && data.isHoliday() == false) {
                    remoteView.setTextViewText(R.id.titleView, context.getString(R.string.notification_dinner_title));
                    remoteView.setImageViewResource(R.id.imageView, R.drawable.img_notification_robot_dinner_extended);
                    remoteView.setInt(R.id.imageView, "setBackgroundResource", R.color.notification_dinner_background);
                    extendRemoteView.setTextViewText(R.id.titleView, context.getString(R.string.notification_dinner_title));
                    extendRemoteView.setImageViewResource(R.id.imageView, R.drawable.img_notification_robot_dinner_extended);
                    extendRemoteView.setInt(R.id.imageView, "setBackgroundResource", R.color.notification_dinner_background);
                    notificationManager.cancel(ID_NOTIFICATION_DINNER);
                    notificationManager.notify(ID_NOTIFICATION_DINNER, notification);
                    TrackHelper.sendEvent("Alarm", "Dinner", Values.getInstance().get(Values.StringType.AlarmShowTimeForDinner));
                }
                break;
        }
    }
}
