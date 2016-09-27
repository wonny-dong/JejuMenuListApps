package com.dongwon.menulist.task;

import android.os.Handler;

/**
 * Created by Dongwon on 2015-06-24.
 */
public class TextViewRealTimeUpdateTask implements Runnable{


    @Override
    public void run() {

    }

    public void start(Handler handler){
        handler.post(this);
    }

    public void stop(Handler handler){
        handler.removeCallbacks(this);
    }
}
