package com.dongwon.menulist.event;

import com.dongwon.menulist.type.CafeteriaMenu;

/**
 * Created by dongwon on 2015-06-12.
 */
public class CafeteriaMenuChangeEvent {
    public enum Work{Insert, Update, Delete}

    private CafeteriaMenu cafeteriaMenu;
    private Work work;

    public CafeteriaMenuChangeEvent(CafeteriaMenu cafeteriaMenu, Work work) {
        this.cafeteriaMenu = cafeteriaMenu;
        this.work = work;
    }

    public Work getWork() {
        return work;
    }

    public CafeteriaMenu getCafeteriaMenu() {
        return cafeteriaMenu;
    }
}
