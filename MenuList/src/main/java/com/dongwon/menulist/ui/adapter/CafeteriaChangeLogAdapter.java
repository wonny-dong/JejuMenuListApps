package com.dongwon.menulist.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.dongwon.menulist.R;
import com.dongwon.menulist.database.DataBaseHelper;
import com.dongwon.menulist.database.Values;
import com.dongwon.menulist.event.CafeteriaLogChangeEvent;
import com.dongwon.menulist.task.TextViewDateUpdater;
import com.dongwon.menulist.type.CafeteriaLogData;
import com.dongwon.menulist.util.DateHelper;
import com.dongwon.menulist.util.EtcHelper;
import com.dongwon.menulist.util.TrackHelper;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import de.greenrobot.event.EventBus;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by dongwon-dev on 2015-05-06.
 */
public class CafeteriaChangeLogAdapter extends RecyclerView.Adapter<CafeteriaChangeLogAdapter.LogViewHolder> {
    List<CafeteriaLogData> items;
    Context context;
    TextViewDateUpdater updater;

    public CafeteriaChangeLogAdapter(Context context, TextViewDateUpdater updater) {
        this.context = context;
        this.updater = updater;
    }

    @Override
    public LogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_cafeteria_detail_log, parent, false);
        return new LogViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LogViewHolder holder, final int position) {
        final CafeteriaLogData data = items.get(position);
        holder.tv_comment.setText(data.getItemName());
        holder.tv_date.setText(DateHelper.getTimeString(context, data.getDate().getTime()));
        holder.tv_date.setTag(data.getDate().getTime());
        holder.tv_price.setText(EtcHelper.makePriceString(Math.abs(data.getPrice())));
        holder.btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    v.setClickable(false);
                    OpenHelperManager.getHelper(context, DataBaseHelper.class)
                            .getCafeteriaLogDataDao()
                            .delete(data);
                    OpenHelperManager.releaseHelper();
                    Values.getInstance().put(Values.IntType.CafeteriaMoney, Values.getInstance().get(Values.IntType.CafeteriaMoney) - data.getPrice());
                    if(data.getPrice() > 0){
                        TrackHelper.sendEvent("Cafeteria", "Cancel ", EtcHelper.makePriceString(Math.abs(data.getPrice())));
                    }else{
                        TrackHelper.sendEvent("Cafeteria", "Cancel ", data.getItemName());
                    }
                    EventBus.getDefault().post(new CafeteriaLogChangeEvent(CafeteriaLogChangeEvent.Work.Delete, data));
                } catch (SQLException e) {
                    e.printStackTrace();
                    v.setClickable(true);
                }
            }
        });
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
        @InjectView(R.id.btn_del)
        ImageButton btn_del;

        public LogViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
