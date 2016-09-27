package com.dongwon.menulist.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Dongwon on 2015-05-23.
 */
public abstract class ListBaseAdapter<T extends Object> extends BaseAdapter{
    private List<T> items;
    
    public void setData(List<T> items){
        this.items = items;
    }

    @Override
    public int getCount() {
        if(items == null){
            return 0;
        }else{
            return items.size();
        }
    }

    @Override
    public T getItem(int position) {
        if(items == null){
            return null;
        }else{
            return items.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
