package com.dongwon.menulist.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.dongwon.menulist.type.MealMenu;
import com.dongwon.menulist.util.TrackHelper;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class DataBaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DB_NAME = "menuList.db";
    private static final int VERSION = 3;
    private Dao<MealMenu, Integer> menuModelDao;

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, MealMenu.class);

            init(0, VERSION);
        } catch (SQLException e) {
            TrackHelper.sendException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {

    }

    private void init(int pre, int now) {

    }

    public Dao<MealMenu, Integer> getMealModelDao() throws SQLException {
        if (menuModelDao == null) {
            menuModelDao = getDao(MealMenu.class);
        }
        return menuModelDao;
    }
}
