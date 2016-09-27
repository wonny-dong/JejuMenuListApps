package com.dongwon.menulist.task;

import android.content.Context;
import android.os.Handler;
import android.widget.TextView;
import com.dongwon.menulist.util.DateHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dongwon on 2015-07-02.
 */
public class TextViewDateUpdater implements Runnable{

    private Handler handler;
    private Context context;
    private List<TextView> list = new ArrayList<>();

    public TextViewDateUpdater(Context context, Handler handler) {
        this.handler = handler;
        this.context = context;
    }

    public void start(){
        handler.post(this);
    }

    public void stop(){
        handler.removeCallbacks(this);
    }

    public void add(TextView tv){
        list.add(tv);
    }

    public void remove(TextView tv){
        list.remove(tv);
    }

    @Override
    public void run() {
        for(TextView tv : list){
            long value = (long)tv.getTag();
            tv.setText(DateHelper.getTimeString(context, value));
        }
        handler.postDelayed(this, 1000);
    }
}
