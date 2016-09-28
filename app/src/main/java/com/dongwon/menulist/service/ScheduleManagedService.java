package com.dongwon.menulist.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import com.dongwon.menulist.database.Values;
import com.dongwon.menulist.receiver.CommandReceiver;
import com.dongwon.menulist.util.DateHelper;

import java.util.Calendar;


public class ScheduleManagedService extends Service implements Runnable{
    private static final int ID_ALARM_LUNCH = 201;
    private static final int ID_ALARM_DINNER = 202;

    private Values values;
    private AlarmManager alarmManager;
    private PendingIntent lunchIntent;
    private PendingIntent dinnerIntent;

    @Override
    public void onCreate() {
        super.onCreate();
        values = Values.getInstance();
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        lunchIntent = PendingIntent.getBroadcast(this, ID_ALARM_LUNCH, new Intent(CommandReceiver.ACTION_SHOW_LUNCH_NOTIFICATION), PendingIntent.FLAG_UPDATE_CURRENT);
        dinnerIntent = PendingIntent.getBroadcast(this, ID_ALARM_DINNER, new Intent(CommandReceiver.ACTION_SHOW_DINNER_NOTIFICATION), PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(this).start();

        return Service.START_NOT_STICKY;
    }

    @Override
    public void run() {
//        new ShowMenuNotificationTask(this).execute(CommandReceiver.ACTION_SHOW_DINNER_NOTIFICATION);
        cancelNotificationSchedule();
        if (values.get(Values.BoolType.UseAlarmForLunch) || values.get(Values.BoolType.UseAlarmForDinner)) {
            registerNotificationSchedule();
        }
        registerNextSchedule();
        registerUpdateSchedule();
        stopSelf();
    }

    private void cancelNotificationSchedule() {
        alarmManager.cancel(lunchIntent);
        alarmManager.cancel(dinnerIntent);
    }

    private void registerNotificationSchedule() {
        if (values.get(Values.BoolType.UseAlarmForLunch)) {
            String[] split = values.get(Values.StringType.AlarmShowTimeForLunch).split(":");
            int hour = Integer.parseInt(split[0]);
            int minute = Integer.parseInt(split[1]);
            Calendar calendar = DateHelper.getMidnightCalendar();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            if(calendar.getTimeInMillis() >= System.currentTimeMillis()){
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), lunchIntent);
            }
        }
        if (values.get(Values.BoolType.UseAlarmForDinner)) {
            String[] split = values.get(Values.StringType.AlarmShowTimeForDinner).split(":");
            int hour = Integer.parseInt(split[0]);
            int minute = Integer.parseInt(split[1]);
            Calendar calendar = DateHelper.getMidnightCalendar();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            if(calendar.getTimeInMillis() >= System.currentTimeMillis()){
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), dinnerIntent);
            }
        }
    }

    private void registerNextSchedule() {
        Intent intent = new Intent(this, ScheduleManagedService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);

        Calendar calendar = DateHelper.getMidnightCalendar();
        calendar.add(Calendar.DATE, 1);
        alarmManager.cancel(pendingIntent);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void registerUpdateSchedule(){
        Calendar calendar = DateHelper.getMidnightCalendar();
        calendar.add(Calendar.HOUR, 4);
        if(calendar.getTimeInMillis() > System.currentTimeMillis()){
            Intent intent = new Intent(this, MenuListUpdateService.class);
            intent.putExtra(MenuListUpdateService.EXTRA_TASK, MenuListUpdateService.Task.AutoUpdate.name());
            PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);

            alarmManager.cancel(pendingIntent);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
