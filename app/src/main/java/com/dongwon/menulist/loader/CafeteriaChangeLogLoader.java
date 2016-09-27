package com.dongwon.menulist.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import com.dongwon.menulist.database.DataBaseHelper;
import com.dongwon.menulist.type.CafeteriaLogData;
import com.dongwon.menulist.util.TrackHelper;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import de.greenrobot.event.EventBus;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dongwon-dev on 2015-04-20.
 */
public class CafeteriaChangeLogLoader extends AsyncTaskLoader<List<CafeteriaLogData>> {
    private static final long MAX_ROW_SIZE = 80;
    private List<CafeteriaLogData> items;

    public CafeteriaChangeLogLoader(Context context) {
        super(context);
    }

    @Override
    public List<CafeteriaLogData> loadInBackground() {
        items = new ArrayList<>();
        DataBaseHelper dataBaseHelper = OpenHelperManager.getHelper(getContext(), DataBaseHelper.class);
        try {
            Dao<CafeteriaLogData, Integer> cafeteriaLogDataDao = dataBaseHelper.getCafeteriaLogDataDao();
            long count = cafeteriaLogDataDao.countOf();
            items = cafeteriaLogDataDao
                    .queryBuilder()
                    .limit(MAX_ROW_SIZE)
                    .offset(count - MAX_ROW_SIZE < 0 ? 0 : count - MAX_ROW_SIZE)
                    .orderBy("date", true)
                    .query();
        } catch (SQLException e) {
            TrackHelper.sendException(e);
        }
        OpenHelperManager.releaseHelper();
        return items;
    }

    @Override
    public void deliverResult(List<CafeteriaLogData> data) {
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
