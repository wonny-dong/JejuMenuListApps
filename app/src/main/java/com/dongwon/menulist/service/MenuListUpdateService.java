package com.dongwon.menulist.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.dongwon.menulist.database.DataBaseHelper;
import com.dongwon.menulist.database.Values;
import com.dongwon.menulist.event.MenuUpdateEvent;
import com.dongwon.menulist.task.HttpRequest;
import com.dongwon.menulist.type.DayMenuJson;
import com.dongwon.menulist.type.FileInfoJson;
import com.dongwon.menulist.type.MealMenu;
import com.dongwon.menulist.util.DateHelper;
import com.dongwon.menulist.util.HashHelper;
import com.dongwon.menulist.util.TrackHelper;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import de.greenrobot.event.EventBus;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Dongwon on 2015-04-26.
 */
public class MenuListUpdateService extends Service implements Runnable {
    public static final String EXTRA_TASK = "task";

    public enum Task {AutoUpdate, PassiveUpdate}

    public Task nowTask = Task.AutoUpdate;
    private Thread taskThread = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String extraTask = intent.getStringExtra(EXTRA_TASK);
            if (extraTask != null) {
                nowTask = Task.valueOf(intent.getStringExtra(EXTRA_TASK));
            }
        }

        if (nowTask == null) {
            nowTask = Task.AutoUpdate;
        }

        if (taskThread == null || taskThread.isDaemon()) {
            taskThread = new Thread(this);
            taskThread.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void run() {
        Task runningTask = nowTask;
        EventBus.getDefault().post(new MenuUpdateEvent(runningTask, MenuUpdateEvent.STATE_PRE));
        boolean updateSuccess = false;
        try {
            HttpRequest request = new HttpRequest(this);
            Values values = Values.getInstance();
            FileInfoJson fileInfoJson = request.getFileInfo();

            boolean wantUpdateApk = HashHelper.md5Hex(new FileInputStream(getPackageCodePath())).equals(fileInfoJson.getApkHash()) == false;
            boolean wantUpdateMenu = values.get(Values.StringType.MenuFileHash).equals(fileInfoJson.getMenuListHash()) == false;

            values.put(Values.BoolType.isNewApkUpdate, wantUpdateApk);
            if (wantUpdateMenu) {
                values.put(Values.StringType.MenuFileHash, "");
            }

            DayMenuJson[] dayMenus = request.getDayMenu(fileInfoJson.getMenuListFileName());
            DataBaseHelper dataBaseHelper = OpenHelperManager.getHelper(this, DataBaseHelper.class);
            Dao<MealMenu, Integer> menuModelDao = dataBaseHelper.getMealModelDao();
            menuModelDao.deleteBuilder().delete();
            for (DayMenuJson json : dayMenus) {
                menuModelDao.create(new MealMenu(json.getLunchMenus(), DateHelper.getDate(json.getYear(), json.getMonth(), json.getDay()), MealMenu.Distinction.Lunch, json.isHoliday()));
                menuModelDao.create(new MealMenu(json.getDinnerMenus(), DateHelper.getDate(json.getYear(), json.getMonth(), json.getDay()), MealMenu.Distinction.Dinner, json.isHoliday()));
            }
            OpenHelperManager.releaseHelper();
            startService(new Intent(this, ScheduleManagedService.class));
            updateSuccess = true;
        } catch (SQLException | IOException e) {
            TrackHelper.sendException(e);
        }

        EventBus.getDefault().post(new MenuUpdateEvent(runningTask, updateSuccess ? MenuUpdateEvent.STATE_POST : MenuUpdateEvent.STATE_POST_FAILED));
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
