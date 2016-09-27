package com.dongwon.menulist.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import com.dongwon.menulist.database.DataBaseHelper;
import com.dongwon.menulist.type.CafeteriaMenu;
import com.dongwon.menulist.util.TrackHelper;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dongwon-dev on 2015-04-20.
 */
public class CafeteriaMenuLoader extends AsyncTaskLoader<List<CafeteriaMenu>> {
    private List<CafeteriaMenu> items;

    public CafeteriaMenuLoader(Context context) {
        super(context);
    }

    @Override
    public List<CafeteriaMenu> loadInBackground() {
        items = new ArrayList<>();
        DataBaseHelper dataBaseHelper = OpenHelperManager.getHelper(getContext(), DataBaseHelper.class);
        try {
            Dao<CafeteriaMenu, Integer> cafeteriaLogDataDao = dataBaseHelper.getCafeteriaMenuDao();
            items = cafeteriaLogDataDao.queryBuilder()
                    .orderBy("price", true)
                    .query();
        } catch (SQLException e) {
            TrackHelper.sendException(e);
        }

        OpenHelperManager.releaseHelper();
        return items;
    }

    @Override
    public void deliverResult(List<CafeteriaMenu> data) {
        if(isReset()){
            return;
        }
        if(isStarted()){
            super.deliverResult(data);
        }

    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if(items != null){
            deliverResult(items);
        }

        if(takeContentChanged() || items == null){
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
        items = null;
    }

}
