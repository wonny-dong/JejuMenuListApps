package com.dongwon.menulist.ui.activity;

import android.support.v7.app.AppCompatActivity;
import com.dongwon.menulist.model.BaseModel;

/**
 * Created by Dongwon on 2015-04-26.
 */
public abstract class BaseActivity<T extends BaseModel> extends AppCompatActivity {
    private final String TAG_MODEL = "model";

    public T getModel(){
        return null;
    }
}
