package com.dongwon.menulist.ui.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import com.dongwon.menulist.R;
import com.dongwon.menulist.database.DataBaseHelper;
import com.dongwon.menulist.event.CafeteriaMenuChangeEvent;
import com.dongwon.menulist.type.CafeteriaMenu;
import com.dongwon.menulist.util.TrackHelper;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import de.greenrobot.event.EventBus;

import java.sql.SQLException;

/**
 * Created by Dongwon on 2015-06-23.
 */
public class CafeteriaMenuDelAlertDialog extends DialogFragment{
    public static final String EXTRA_MENU = "Menu";

    private CafeteriaMenu menu;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.cafeteria_menu_del_title)
                .setMessage(R.string.cafeteria_menu_del_content)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            DataBaseHelper dataBaseHelper = OpenHelperManager.getHelper(getActivity(), DataBaseHelper.class);
                            Dao<CafeteriaMenu, Integer> dao = dataBaseHelper.getCafeteriaMenuDao();
                            dao.delete(menu);
                            OpenHelperManager.releaseHelper();
                            EventBus.getDefault().post(new CafeteriaMenuChangeEvent(menu, CafeteriaMenuChangeEvent.Work.Delete));
                            TrackHelper.sendEvent("Cafeteria", "Delete", menu.getName());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle;
        if(savedInstanceState == null){
            bundle = getArguments();
        }else{
            bundle = savedInstanceState;
        }

        if(bundle == null){
            dismiss();
        }else{
            menu = bundle.getParcelable(EXTRA_MENU);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            outState.putParcelable(EXTRA_MENU, menu);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
