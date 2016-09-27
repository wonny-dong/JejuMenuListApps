package com.dongwon.menulist.ui.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.dongwon.menulist.R;

/**
 * Created by Dongwon on 2015-04-28.
 */
public class LoadingFragment extends BaseFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_loading, container, false);
    }
}
