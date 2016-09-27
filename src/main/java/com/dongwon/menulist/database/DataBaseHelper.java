package com.dongwon.menulist.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.dongwon.menulist.type.CafeteriaLogData;
import com.dongwon.menulist.type.CafeteriaMenu;
import com.dongwon.menulist.type.MealMenu;
import com.dongwon.menulist.util.TrackHelper;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by Dongwon on 2015-04-22.
 */
public class DataBaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DB_NAME = "menuList.db";
    private static final int VERSION = 3;
    private Dao<MealMenu, Integer> menuModelDao;
    private Dao<CafeteriaLogData, Integer> cafeteriaLogDataDao;
    private Dao<CafeteriaMenu, Integer> cafeteriaMenuDao;

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, MealMenu.class);
            TableUtils.createTable(connectionSource, CafeteriaLogData.class);
            TableUtils.createTable(connectionSource, CafeteriaMenu.class);

            init(0, VERSION);
        } catch (SQLException e) {
            TrackHelper.sendException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        try {
            if (i < 2) {
                TableUtils.createTable(connectionSource, CafeteriaLogData.class);
                TableUtils.createTable(connectionSource, CafeteriaMenu.class);
            }
            init(i, i1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void init(int pre, int now) {
        if (pre < 3) {
            try {
                TableUtils.clearTable(connectionSource, CafeteriaMenu.class);
                getCafeteriaMenuDao().create(new CafeteriaMenu("아메리카노", 500));
                getCafeteriaMenuDao().create(new CafeteriaMenu("콜라", 500));
                getCafeteriaMenuDao().create(new CafeteriaMenu("환타", 500));
                getCafeteriaMenuDao().create(new CafeteriaMenu("맥콜", 500));
                getCafeteriaMenuDao().create(new CafeteriaMenu("과일주스", 300));
                getCafeteriaMenuDao().create(new CafeteriaMenu("레쓰비", 400));
                getCafeteriaMenuDao().create(new CafeteriaMenu("2프로", 600));
                getCafeteriaMenuDao().create(new CafeteriaMenu("스파크링", 600));
                getCafeteriaMenuDao().create(new CafeteriaMenu("레몬에이드", 800));
                getCafeteriaMenuDao().create(new CafeteriaMenu("아침헛개", 800));

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Dao<MealMenu, Integer> getMealModelDao() throws SQLException {
        if (menuModelDao == null) {
            menuModelDao = getDao(MealMenu.class);
        }
        return menuModelDao;
    }

    public Dao<CafeteriaLogData, Integer> getCafeteriaLogDataDao() throws SQLException {
        if (cafeteriaLogDataDao == null) {
            cafeteriaLogDataDao = getDao(CafeteriaLogData.class);
        }
        return cafeteriaLogDataDao;
    }

    public Dao<CafeteriaMenu, Integer> getCafeteriaMenuDao() throws SQLException {
        if (cafeteriaMenuDao == null) {
            cafeteriaMenuDao = getDao(CafeteriaMenu.class);
        }
        return cafeteriaMenuDao;
    }
}
