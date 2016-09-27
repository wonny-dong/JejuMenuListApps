package com.dongwon.menulist.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.dongwon.menulist.R;
import com.dongwon.menulist.task.TextViewDateUpdater;
import com.dongwon.menulist.type.CafeteriaLogData;
import com.dongwon.menulist.util.DateHelper;
import com.dongwon.menulist.util.EtcHelper;

import java.util.List;

/**
 * Created by dongwon-dev on 2015-05-06.
 */
public class CafeteriaSimpleChangeLogAdapter extends RecyclerView.Adapter<CafeteriaSimpleChangeLogAdapter.LogViewHolder> {
    List<CafeteriaLogData> items;
    Context context;
    TextViewDateUpdater updater;

    public CafeteriaSimpleChangeLogAdapter(Context context, TextViewDateUpdater updater) {
        this.context = context;
        this.updater = updater;
    }

    @Override
    public LogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_cafeteria_simple_log, parent, false);
        return new LogViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LogViewHolder holder, int position) {
        CafeteriaLogData data = items.get(position);
        holder.tv_comment.setText(data.getItemName());
        holder.tv_date.setText(DateHelper.getTimeString(context, data.getDate().getTime()));
        holder.tv_date.setTag(data.getDate().getTime());
        holder.tv_price.setText(EtcHelper.makePriceString(Math.abs(data.getPrice())));
        if(data.getPrice() > 0){
            holder.icon.setImageResource(R.drawable.ic_cafeteria_log_plug);
            holder.tv_price.setTextColor(context.getResources().getColor(R.color.cafeteria_deposit_background));
        }else{
            holder.icon.setImageResource(R.drawable.ic_cafeteria_log_minus);
            holder.tv_price.setTextColor(context.getResources().getColor(R.color.cafeteria_menu_list_background));
        }
    }

    @Override
    public void onViewAttachedToWindow(LogViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        updater.add(holder.tv_date);
    }

    @Override
    public void onViewDetachedFromWindow(LogViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        updater.remove(holder.tv_date);
    }

    public void setData(List<CafeteriaLogData> items){
        this.items = items;
    }

    @Override
    public int getItemCount() {
        if(items == null){
            return 0;
        }else{
            return items.size();
        }
    }

    public static class LogViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.tv_comment)
        TextView tv_comment;
        @InjectView(R.id.tv_date)
        TextView tv_date;
        @InjectView(R.id.tv_price)
        TextView tv_price;
        @InjectView(R.id.icon)
        ImageView icon;

        public LogViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
