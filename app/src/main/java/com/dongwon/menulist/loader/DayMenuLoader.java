package com.dongwon.menulist.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import com.dongwon.menulist.database.DataBaseHelper;
import com.dongwon.menulist.type.MealMenu;
import com.dongwon.menulist.ui.adapter.MenuListAdapter;
import com.dongwon.menulist.util.DateHelper;
import com.dongwon.menulist.util.TrackHelper;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by dongwon-dev on 2015-04-20.
 */
public class DayMenuLoader extends AsyncTaskLoader<List<MenuListAdapter.DayMenuData>> {
    private List<MenuListAdapter.DayMenuData> items;

    public DayMenuLoader(Context context) {
        super(context);
    }

    @Override
    public List<MenuListAdapter.DayMenuData> loadInBackground() {
        items = new ArrayList<>();
        DataBaseHelper dataBaseHelper = OpenHelperManager.getHelper(getContext(), DataBaseHelper.class);
        try {
            //범위 설정
            Calendar calendar = DateHelper.getMidnightCalendar();
            calendar.add(Calendar.MONTH, -1);
            Date start = calendar.getTime();
            calendar.add(Calendar.MONTH, 2);
            Date end = calendar.getTime();

            Dao<MealMenu, Integer> mealModelDao = dataBaseHelper.getMealModelDao();
            QueryBuilder<MealMenu, Integer> queryBuilder = mealModelDao.queryBuilder();
            queryBuilder.where().between("date", start, end);
            queryBuilder.orderBy("date", true);
            List<MealMenu> mealMenus = queryBuilder.query();
            SimpleDateFormat format = new SimpleDateFormat("MMM/dd\n(E)", Locale.ENGLISH);
            for(int i = 0; i < mealMenus.size()-1; i += 2){
                MealMenu[] days = new MealMenu[]{mealMenus.get(i), mealMenus.get(i+1)};
                if(days[0].getDistinction() == MealMenu.Distinction.Dinner){
                    MealMenu temp = days[0];
                    days[0] = days[1];
                    days[1] = temp;
                }
                if(days[0].getDate().getTime() == days[1].getDate().getTime()){
                    String[] str = format.format(days[0].getDate()).split("/");
                    items.add(new MenuListAdapter.DayMenuData(str[0],str[1], days[0].getDate(), days[0].getMenus(), days[1].getMenus(), days[0].isHoliday()));
                }else{
                    TrackHelper.sendException(new Exception("days[0].getDate().getTime() : " + days[0].getDate().getTime() + "  days[1].getDate().getTime() : " + days[1].getDate().getTime()));
                }
            }
        } catch (SQLException e) {
            TrackHelper.sendException(e);
        }

        OpenHelperManager.releaseHelper();
        return items;
    }

    @Override
    public void deliverResult(List<MenuListAdapter.DayMenuData> data) {
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
