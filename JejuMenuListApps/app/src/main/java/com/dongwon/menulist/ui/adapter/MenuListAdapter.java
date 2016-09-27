package com.dongwon.menulist.ui.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.dongwon.menulist.R;

import java.util.*;

/**
 * Created by Dongwon on 2015-04-24.
 */
public class MenuListAdapter extends RecyclerView.Adapter<MenuListAdapter.MenuViewHolder>{
    private List<DayMenuData> items;
    private Context context;
    private int todayPosition;

    public MenuListAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<DayMenuData> items){
        this.items = items;
        todayPosition = -1;
    }

    public void setTodayPosition(int todayPosition) {
        this.todayPosition = todayPosition;
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_today_menu, parent, false);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        DayMenuData data = items.get(position);
        holder.tv_month.setText(data.getMonth());
        holder.tv_day.setText(data.getDay());
        holder.tv_lunch_menus.setText(toStringInArrays(data.getLunchMenus()));
        holder.tv_dinner_menus.setText(toStringInArrays(data.getDinnerMenus()));
        int backgroundColor = R.color.day_menu_card_view_background;
        if(position == todayPosition){
            backgroundColor = R.color.day_menu_card_view_background_today;
        }else if(data.isHoliday()){
            backgroundColor = R.color.day_menu_card_view_background_holiday;
        }
        holder.cardBox.setCardBackgroundColor(context.getResources().getColor(backgroundColor));
    }

    private String toStringInArrays(String[] arrays){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arrays.length; ++i) {
            if (i != 0) {
                sb.append("\n");
            }
            sb.append(arrays[i]);
        }
        return sb.toString();
    }

    @Override
    public int getItemCount() {
        if(items == null){
            return 0;
        }else{
            return items.size();
        }
    }

    public static class MenuViewHolder extends RecyclerView.ViewHolder{
        @InjectView(R.id.tv_month)
        TextView tv_month;
        @InjectView(R.id.tv_day)
        TextView tv_day;
        @InjectView(R.id.tv_lunch_menus)
        TextView tv_lunch_menus;
        @InjectView(R.id.tv_dinner_menus)
        TextView tv_dinner_menus;
        @InjectView(R.id.card_box)
        CardView cardBox;

        public MenuViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    public static class DayMenuData{
        private String month;
        private String day;
        private Date date;
        private String[] lunchMenus;
        private String[] dinnerMenus;
        private boolean isHoliday;

        public DayMenuData(String month, String day, Date date, String[] lunchMenus, String[] dinnerMenus, boolean isHoliday) {
            this.month = month;
            this.day = day;
            this.date = date;
            this.lunchMenus = lunchMenus;
            this.dinnerMenus = dinnerMenus;
            this.isHoliday = isHoliday;
        }

        public String getMonth() {
            return month;
        }

        public String getDay() {
            return day;
        }

        public Date getDate() {
            return date;
        }

        public String[] getLunchMenus() {
            return lunchMenus;
        }

        public String[] getDinnerMenus() {
            return dinnerMenus;
        }

        public boolean isHoliday() {
            return isHoliday;
        }
    }
}
