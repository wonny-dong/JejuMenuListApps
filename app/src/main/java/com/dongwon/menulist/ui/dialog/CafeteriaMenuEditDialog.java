package com.dongwon.menulist.ui.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.dongwon.menulist.R;
import com.dongwon.menulist.database.DataBaseHelper;
import com.dongwon.menulist.event.CafeteriaMenuChangeEvent;
import com.dongwon.menulist.type.CafeteriaMenu;
import com.dongwon.menulist.util.TrackHelper;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import de.greenrobot.event.EventBus;

/**
 * Created by dongwon on 2015-06-12.
 */
public class CafeteriaMenuEditDialog extends DialogFragment {
    public static final String EXTRA_MENU = "Menu";
    public static final String EXTRA_STATE = "State";

    public enum State {MenuListAdd, MenuListEdit}

    @InjectView(R.id.et_menu_name)
    EditText et_menuName;
    @InjectView(R.id.et_menu_price)
    EditText et_menuPrice;

    private CafeteriaMenu menu;
    private State state;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle;
        if(savedInstanceState == null){
            bundle = getArguments();
        }else{
            bundle = savedInstanceState;
        }

        if(bundle == null){
            dismiss();
        }else{
            state = State.valueOf(bundle.getString(EXTRA_STATE));
            switch (state){
                case MenuListAdd:
                    menu = new CafeteriaMenu();
                    break;

                case MenuListEdit:
                    menu = bundle.getParcelable(EXTRA_MENU);
                    break;
            }
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View root = getActivity().getLayoutInflater().inflate(R.layout.dialog_menu_edit, null);
        ButterKnife.inject(this, root);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.ic_cafeteria_menu_edit);
        builder.setTitle(R.string.cafeteria_menu);
        builder.setView(root);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    menuSave();
                    DataBaseHelper dataBaseHelper = OpenHelperManager.getHelper(getActivity(), DataBaseHelper.class);
                    Dao<CafeteriaMenu, Integer> dao = dataBaseHelper.getCafeteriaMenuDao();
                    switch (state){
                        case MenuListEdit:
                            dao.update(menu);
                            TrackHelper.sendEvent("Cafeteria", "Edit", menu.getName());
                            Toast.makeText(getActivity().getApplicationContext(), R.string.cafeteria_menu_edit_toast, Toast.LENGTH_SHORT).show();
                            break;

                        case MenuListAdd:
                            dao.create(menu);
                            TrackHelper.sendEvent("Cafeteria", "Add", menu.getName());
                            Toast.makeText(getActivity().getApplicationContext(), R.string.cafeteria_menu_add_toast, Toast.LENGTH_SHORT).show();
                            break;
                    }
                    OpenHelperManager.releaseHelper();
                    EventBus.getDefault().post(new CafeteriaMenuChangeEvent(menu, state == State.MenuListAdd ? CafeteriaMenuChangeEvent.Work.Insert : CafeteriaMenuChangeEvent.Work.Update));
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), R.string.cafeteria_input_check_toast, Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        if(state == State.MenuListEdit){
            builder.setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(menu != null){
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(CafeteriaMenuDelAlertDialog.EXTRA_MENU, menu);
                        DialogFragment dialogFragment = new CafeteriaMenuDelAlertDialog();
                        dialogFragment.setArguments(bundle);
                        dialogFragment.show(getFragmentManager(), "delDialog");
                    }
                }
            });
        }

        return builder.create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        et_menuName.setText(menu.getName());
        et_menuPrice.setText(menu.getPrice() == 0 ? "" : String.valueOf(menu.getPrice()));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            menuSave();
            outState.putParcelable(EXTRA_MENU, menu);
            outState.putString(EXTRA_STATE, state.name());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void menuSave() throws Exception{
        String name = et_menuName.getText().toString();
        String price = et_menuPrice.getText().toString();
        if(TextUtils.isEmpty(name.trim()) || TextUtils.isEmpty(price.trim())){
            throw new NullPointerException();

        }
        menu.setName(name);
        menu.setPrice(Integer.parseInt(price));
    }
}
