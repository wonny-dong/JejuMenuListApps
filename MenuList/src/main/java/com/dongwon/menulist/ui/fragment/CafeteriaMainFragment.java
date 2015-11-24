package com.dongwon.menulist.ui.fragment;

import android.animation.Animator;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.*;
import butterknife.*;
import com.dongwon.menulist.R;
import com.dongwon.menulist.database.DataBaseHelper;
import com.dongwon.menulist.database.Values;
import com.dongwon.menulist.event.CafeteriaLogChangeEvent;
import com.dongwon.menulist.event.CafeteriaMenuChangeEvent;
import com.dongwon.menulist.loader.CafeteriaChangeLogLoader;
import com.dongwon.menulist.loader.CafeteriaMenuLoader;
import com.dongwon.menulist.task.TextViewDateUpdater;
import com.dongwon.menulist.type.CafeteriaLogData;
import com.dongwon.menulist.type.CafeteriaMenu;
import com.dongwon.menulist.ui.adapter.CafeteriaChangeLogAdapter;
import com.dongwon.menulist.ui.adapter.CafeteriaDepositAdapter;
import com.dongwon.menulist.ui.adapter.CafeteriaMenuAdapter;
import com.dongwon.menulist.ui.adapter.CafeteriaSimpleChangeLogAdapter;
import com.dongwon.menulist.ui.dialog.CafeteriaMenuEditDialog;
import com.dongwon.menulist.util.EtcHelper;
import com.dongwon.menulist.util.Logger;
import com.dongwon.menulist.util.TrackHelper;
import com.dongwon.menulist.util.UIHelper;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import de.greenrobot.event.EventBus;
import jp.wasabeef.recyclerview.animators.ScaleInBottomAnimator;
import jp.wasabeef.recyclerview.animators.ScaleInRightAnimator;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Created by Dongwon on 2015-05-23.
 */
public class CafeteriaMainFragment extends BaseFragment{
    private static final int ID_CHANGE_LOG_LOADER = 1;
    private static final int ID_CAFETERIA_MENU_LOADER = 2;
    enum BottomState {MenuList, Deposit, MenuListEdit}

    @InjectView(R.id.lv_change_log)
    RecyclerView lv_changeLog;
    @InjectView(R.id.layout_cafeteria_bottom)
    View layout_cafeteriaBottom;
    @InjectView(R.id.tv_total_price)
    TextView tv_totalPrice;
    @InjectView(R.id.btn_floating)
    FloatingActionButton btn_floating;
    @InjectView(R.id.grid_view)
    GridView grid_view;
    @InjectView(R.id.layout_title)
    View layout_bottomTitle;
    @InjectView(R.id.btn_title_menu_1)
    ImageButton btn_bottomMenu1;
    @InjectView(R.id.lv_change_log_drawer)
    RecyclerView lv_changeLogDrawer;
    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @InjectView(R.id.iv_bottom_cache)
    ImageView iv_bottomCache;

    CafeteriaDepositAdapter cafeteriaDepositAdapter;
    CafeteriaMenuAdapter cafeteriaMenuAdapter;
    CafeteriaSimpleChangeLogAdapter changeLogAdapter;
    CafeteriaChangeLogAdapter changeLogDetailAdapter;
    BottomState bottomState = BottomState.MenuList;
    private List<CafeteriaLogData> cafeteriaLogDataList;
    private List<CafeteriaMenu> cafeteriaMenuList;
    Handler handler = new Handler();
    TextViewDateUpdater dateUpdater;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dateUpdater = new TextViewDateUpdater(getActivity(), handler);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        dateUpdater.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        dateUpdater.stop();
    }

    @Override
    public boolean onBackPressed() {
        if(drawerLayout.isDrawerOpen(lv_changeLogDrawer)){
            drawerLayout.closeDrawer(lv_changeLogDrawer);
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_cafeteria, container, false);
        ButterKnife.inject(this, root);

        changeLogAdapter = new CafeteriaSimpleChangeLogAdapter(getActivity(), dateUpdater);
        changeLogDetailAdapter = new CafeteriaChangeLogAdapter(getActivity(), dateUpdater);
        lv_changeLog.setAdapter(changeLogAdapter);
        lv_changeLog.setLayoutManager(new LinearLayoutManager(getActivity()));
        lv_changeLogDrawer.setAdapter(changeLogDetailAdapter);
        lv_changeLogDrawer.setLayoutManager(new LinearLayoutManager(getActivity()));

        cafeteriaMenuAdapter = new CafeteriaMenuAdapter(getActivity());
        cafeteriaDepositAdapter = new CafeteriaDepositAdapter(getActivity());

        initListViewAnimation();
        moveFloatingBtn();
        changeBottom();

        return root;
    }

    private void initListViewAnimation(){
        lv_changeLog.setItemAnimator(new ScaleInBottomAnimator());
        lv_changeLog.getItemAnimator().setAddDuration(300);
        lv_changeLog.getItemAnimator().setRemoveDuration(300);
        lv_changeLog.getItemAnimator().setMoveDuration(300);
        lv_changeLog.getItemAnimator().setChangeDuration(300);

        lv_changeLogDrawer.setItemAnimator(new ScaleInRightAnimator());
        lv_changeLogDrawer.getItemAnimator().setAddDuration(300);
        lv_changeLogDrawer.getItemAnimator().setRemoveDuration(300);
        lv_changeLogDrawer.getItemAnimator().setMoveDuration(300);
        lv_changeLogDrawer.getItemAnimator().setChangeDuration(300);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(ID_CHANGE_LOG_LOADER, null, changeLogCallback);
        getLoaderManager().initLoader(ID_CAFETERIA_MENU_LOADER, null, cafeteriaMenuCallback);
    }

    @OnClick(R.id.btn_floating)
    public void clickFloatingBtn(){
        BottomState pre = bottomState;
        switch (bottomState){
            case MenuList:
                bottomState = BottomState.Deposit;
                break;

            case Deposit:
                bottomState = BottomState.MenuList;
                break;

            case MenuListEdit:
                bottomState = BottomState.MenuList;
                break;
        }

        if(pre == BottomState.MenuListEdit){
            changeBottom();
        }else{
            showChangeAnimation();
        }
    }

    @OnClick(R.id.btn_title_menu_1)
    public void clickTitleMenuBtn(){
        switch (bottomState){
            case MenuList:
                bottomState = BottomState.MenuListEdit;
                changeBottom();
                break;

            case MenuListEdit:
                Bundle bundle = new Bundle();
                bundle.putString(CafeteriaMenuEditDialog.EXTRA_STATE, CafeteriaMenuEditDialog.State.MenuListAdd.name());
                CafeteriaMenuEditDialog dialog = new CafeteriaMenuEditDialog();
                dialog.setArguments(bundle);
                dialog.show(getFragmentManager(), "EditDialog");
                break;
        }


    }

    @OnItemClick(R.id.grid_view)
    public void gridItemClick(int position){
        switch (bottomState){
            case MenuList:
                try {
                    CafeteriaMenu menu = cafeteriaMenuAdapter.getItem(position);
                    CafeteriaLogData newLog = new CafeteriaLogData(menu.getName(), -menu.getPrice(), new Date(System.currentTimeMillis()));
                    OpenHelperManager.getHelper(getActivity(), DataBaseHelper.class)
                            .getCafeteriaLogDataDao()
                            .create(newLog);
                    OpenHelperManager.releaseHelper();
                    Values.getInstance().put(Values.IntType.CafeteriaMoney, Values.getInstance().get(Values.IntType.CafeteriaMoney)+ newLog.getPrice());
                    TrackHelper.sendEvent("Cafeteria", "Buy", menu.getName());
                    EventBus.getDefault().post(new CafeteriaLogChangeEvent(CafeteriaLogChangeEvent.Work.Insert, newLog));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;

            case Deposit:
                try {
                    int price = cafeteriaDepositAdapter.getItem(position);
                    CafeteriaLogData newLog = new CafeteriaLogData(getString(R.string.cafeteria_save), price, new Date(System.currentTimeMillis()));
                    OpenHelperManager.getHelper(getActivity(), DataBaseHelper.class)
                            .getCafeteriaLogDataDao()
                            .create(newLog);
                    OpenHelperManager.releaseHelper();
                    Values.getInstance().put(Values.IntType.CafeteriaMoney, Values.getInstance().get(Values.IntType.CafeteriaMoney) + newLog.getPrice());
                    TrackHelper.sendEvent("Cafeteria", "Deposit", EtcHelper.makePriceString(Math.abs(price)));
                    EventBus.getDefault().post(new CafeteriaLogChangeEvent(CafeteriaLogChangeEvent.Work.Insert, newLog));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;

            case MenuListEdit:
                CafeteriaMenu product = cafeteriaMenuAdapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putParcelable(CafeteriaMenuEditDialog.EXTRA_MENU, product);
                bundle.putString(CafeteriaMenuEditDialog.EXTRA_STATE, CafeteriaMenuEditDialog.State.MenuListEdit.name());
                DialogFragment dialog = new CafeteriaMenuEditDialog();
                dialog.setArguments(bundle);
                dialog.show(getFragmentManager(), "dialog");
                break;
        }

    }

    @OnTouch(R.id.lv_change_log)
    public boolean gridViewTouch(MotionEvent event){
        if(event.getAction() == MotionEvent.ACTION_UP){
            changeLogDetailAdapter.notifyDataSetChanged();
            drawerLayout.openDrawer(lv_changeLogDrawer);
        }
        return true;
    }

    private void changeBottom(){
        switch (bottomState){
            case MenuList:
                btn_floating.setRippleColor(getResources().getColor(R.color.cafeteria_deposit_background));
                btn_floating.setImageResource(R.drawable.ic_cafeteria_deposit);
                layout_bottomTitle.setBackgroundColor(getResources().getColor(R.color.cafeteria_menu_list_background));
                layout_cafeteriaBottom.setBackgroundColor(getResources().getColor(R.color.cafeteria_menu_list_background_1));
                btn_bottomMenu1.setVisibility(View.VISIBLE);
                btn_bottomMenu1.setImageResource(R.drawable.ic_cafeteria_menu_edit);
                grid_view.setAdapter(cafeteriaMenuAdapter);
                break;

            case Deposit:
                btn_floating.setRippleColor(getResources().getColor(R.color.cafeteria_menu_list_background));
                btn_floating.setImageResource(R.drawable.ic_cafeteria_menu_list);
                layout_bottomTitle.setBackgroundColor(getResources().getColor(R.color.cafeteria_deposit_background));
                layout_cafeteriaBottom.setBackgroundColor(getResources().getColor(R.color.cafeteria_deposit_background_1));
                btn_bottomMenu1.setVisibility(View.GONE);
                grid_view.setAdapter(cafeteriaDepositAdapter);
                break;

            case MenuListEdit:
                btn_floating.setRippleColor(getResources().getColor(R.color.cafeteria_floting_btn_menu_edit_background));
                btn_floating.setImageResource(R.drawable.ic_cafeteria_edit_exit);
                layout_bottomTitle.setBackgroundColor(getResources().getColor(R.color.cafeteria_menu_list_background));
                layout_cafeteriaBottom.setBackgroundColor(getResources().getColor(R.color.cafeteria_menu_list_background_1));
                btn_bottomMenu1.setVisibility(View.VISIBLE);
                btn_bottomMenu1.setImageResource(R.drawable.ic_cafeteria_menu_add);
                grid_view.setAdapter(cafeteriaMenuAdapter);
                break;

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    public void onEvent(CafeteriaMenuChangeEvent event){
        if(cafeteriaMenuList != null){
            switch (event.getWork()){
                case Insert:
                    cafeteriaMenuList.add(event.getCafeteriaMenu());
                    break;

                case Delete:
                    cafeteriaMenuList.remove(event.getCafeteriaMenu());
                    break;
            }
        }
        cafeteriaMenuAdapter.notifyDataSetChanged();
    }

    public void onEvent(CafeteriaLogChangeEvent event){
        if(cafeteriaLogDataList != null){
            switch (event.getWork()){
                case Insert:
                    cafeteriaLogDataList.add(event.getData());
                    changeLogAdapter.notifyItemInserted(cafeteriaLogDataList.size());
                    changeLogDetailAdapter.notifyItemInserted(cafeteriaLogDataList.size());
                    lv_changeLog.scrollToPosition(cafeteriaLogDataList.size()-1);
                    lv_changeLogDrawer.scrollToPosition(cafeteriaLogDataList.size()-1);
                    break;

                case Delete:
                    int index = cafeteriaLogDataList.indexOf(event.getData());
                    cafeteriaLogDataList.remove(index);
                    changeLogAdapter.notifyItemRemoved(index);
                    changeLogDetailAdapter.notifyItemRemoved(index);
                    break;
            }
        }
        tv_totalPrice.setText(EtcHelper.makePriceString(Values.getInstance().get(Values.IntType.CafeteriaMoney)));
    }

    private void showChangeAnimation(){
        if(Build.VERSION.SDK_INT >= 21){
            int btn_cx = (btn_floating.getLeft() + btn_floating.getRight()) /2;
            int btn_cy = (btn_floating.getTop() + btn_floating.getBottom()) / 2;
            Animator anim = ViewAnimationUtils.createCircularReveal(layout_cafeteriaBottom, btn_cx, btn_cy, 0, Math.max(layout_cafeteriaBottom.getWidth(), layout_cafeteriaBottom.getHeight())*2);
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    layout_cafeteriaBottom.destroyDrawingCache();
                    layout_cafeteriaBottom.buildDrawingCache();
                    iv_bottomCache.setImageBitmap(layout_cafeteriaBottom.getDrawingCache());
                    changeBottom();
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    iv_bottomCache.setImageBitmap(null);
                    layout_cafeteriaBottom.destroyDrawingCache();
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                    iv_bottomCache.setImageBitmap(null);
                    layout_cafeteriaBottom.destroyDrawingCache();
                }

                @Override
                public void onAnimationRepeat(Animator animator) {}
            });
            anim.setDuration(800);
            anim.start();
        }else{
            changeBottom();
        }
    }

    private void moveFloatingBtn(){
        btn_floating.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) btn_floating.getLayoutParams();
                params.bottomMargin = -btn_floating.getHeight() / 2;

                if (UIHelper.convertPixelsToDp(btn_floating.getWidth(), getActivity()) > 60) {
                    params.leftMargin = (int)UIHelper.convertDpToPixel(4, getActivity());
                } else {
                    params.leftMargin = (int)UIHelper.convertDpToPixel(10,getActivity());
                }
                btn_floating.setLayoutParams(params);
                btn_floating.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    LoaderManager.LoaderCallbacks<List<CafeteriaLogData>> changeLogCallback = new LoaderManager.LoaderCallbacks<List<CafeteriaLogData>>(){


        @Override
        public Loader<List<CafeteriaLogData>> onCreateLoader(int id, Bundle args) {
            return new CafeteriaChangeLogLoader(getActivity());
        }

        @Override
        public void onLoadFinished(Loader<List<CafeteriaLogData>> loader, List<CafeteriaLogData> data) {
            cafeteriaLogDataList = data;
            changeLogAdapter.setData(data);
            changeLogAdapter.notifyDataSetChanged();
            changeLogDetailAdapter.setData(data);
            changeLogDetailAdapter.notifyDataSetChanged();
            if(data.isEmpty() == false){
                lv_changeLog.scrollToPosition(data.size()-1);
                lv_changeLogDrawer.scrollToPosition(data.size()-1);
            }
            tv_totalPrice.setText(EtcHelper.makePriceString(Values.getInstance().get(Values.IntType.CafeteriaMoney)));
        }

        @Override
        public void onLoaderReset(Loader<List<CafeteriaLogData>> loader) {

        }
    };


    LoaderManager.LoaderCallbacks<List<CafeteriaMenu>> cafeteriaMenuCallback = new LoaderManager.LoaderCallbacks<List<CafeteriaMenu>>(){
        @Override
        public Loader<List<CafeteriaMenu>> onCreateLoader(int id, Bundle args) {
            return new CafeteriaMenuLoader(getActivity());
        }

        @Override
        public void onLoadFinished(Loader<List<CafeteriaMenu>> loader, List<CafeteriaMenu> data) {
            cafeteriaMenuList = data;
            cafeteriaMenuAdapter.setData(data);
            cafeteriaMenuAdapter.notifyDataSetChanged();
        }

        @Override
        public void onLoaderReset(Loader<List<CafeteriaMenu>> loader) {
        }
    };
}
