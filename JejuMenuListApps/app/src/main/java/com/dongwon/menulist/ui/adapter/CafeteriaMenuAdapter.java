package com.dongwon.menulist.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.dongwon.menulist.R;
import com.dongwon.menulist.type.CafeteriaMenu;
import com.dongwon.menulist.util.EtcHelper;

/**
 * Created by Dongwon on 2015-05-23.
 */
public class CafeteriaMenuAdapter extends ListBaseAdapter<CafeteriaMenu>{
    private LayoutInflater inflater;
    private Context context;

    public CafeteriaMenuAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
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

        CafeteriaMenu item = getItem(position);
        viewHolder.tv_name.setText(item.getName());
        viewHolder.tv_price.setTextColor(context.getResources().getColor(R.color.cafeteria_menu_list_background));
        viewHolder.tv_price.setText(EtcHelper.makePriceString(Math.abs(item.getPrice())));

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
