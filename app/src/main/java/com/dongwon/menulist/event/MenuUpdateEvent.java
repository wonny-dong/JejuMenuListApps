package com.dongwon.menulist.event;

import com.dongwon.menulist.service.MenuListUpdateService;

/**
 * Created by Dongwon on 2015-04-27.
 */
public class MenuUpdateEvent {
    public static final int STATE_PRE = 1;
    public static final int STATE_POST = 2;
    public static final int STATE_POST_FAILED = 3;
    private MenuListUpdateService.Task taskType;
    private int state;

    public MenuUpdateEvent(MenuListUpdateService.Task taskType, int state) {
        this.taskType = taskType;
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public MenuListUpdateService.Task getTaskType() {
        return taskType;
    }
}
