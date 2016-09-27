package com.dongwon.menulist.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.dongwon.menulist.R;
import com.dongwon.menulist.util.EtcHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dongwon on 2015-06-04.
 */
public class CafeteriaDepositAdapter extends ListBaseAdapter<Integer> {
    private LayoutInflater inflater;
    private Context context;

    public CafeteriaDepositAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);

        List<Integer> items = new ArrayList<>();
        items.add(100);
        items.add(500);
        items.add(1000);
        items.add(5000);
        items.add(10000);
        items.add(50000);
        setData(items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_cafeteria_menu, parent, false);
        }
        if(convertView.getTag() != null){
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if(viewHolder == null){
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        viewHolder.tv_name.setText(R.string.cafeteria_save);
        viewHolder.tv_price.setTextColor(context.getResources().getColor(R.color.cafeteria_deposit_background));
        viewHolder.tv_price.setText(EtcHelper.makePriceString(getItem(position)));

        return convertView;
    }

    public class ViewHolder{
        @InjectView(R.id.tv_name)
        TextView tv_name;

        @InjectView(R.id.tv_price)
        TextView tv_price;

        public ViewHolder(View root) {
            ButterKnife.inject(this, root);
        }
    }
}
