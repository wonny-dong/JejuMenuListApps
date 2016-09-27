package com.dongwon.menulist.event;

import com.dongwon.menulist.type.CafeteriaLogData;

/**
 * Created by Dongwon on 2015-05-28.
 */
public class CafeteriaLogChangeEvent {
    public enum Work{Insert, Update, Delete}

    private Work work;
    private CafeteriaLogData data;

    public CafeteriaLogChangeEvent(Work work, CafeteriaLogData data) {
        this.work = work;
        this.data = data;
    }

    public Work getWork() {
        return work;
    }

    public CafeteriaLogData getData() {
        return data;
    }
}
