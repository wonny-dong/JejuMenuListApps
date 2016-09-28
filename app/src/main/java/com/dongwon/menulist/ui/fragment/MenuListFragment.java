package com.dongwon.menulist.ui.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.dongwon.menulist.R;
import com.dongwon.menulist.event.MenuUpdateEvent;
import com.dongwon.menulist.loader.DayMenuLoader;
import com.dongwon.menulist.service.MenuListUpdateService;
import com.dongwon.menulist.ui.adapter.MenuListAdapter;
import com.dongwon.menulist.util.DateHelper;
import com.dongwon.menulist.util.PackHelper;
import com.dongwon.menulist.util.TrackHelper;
import de.greenrobot.event.EventBus;

import java.util.Date;
import java.util.List;


public class MenuListFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<List<MenuListAdapter.DayMenuData>> {
    private static final int ID_LOADER_DAY_MENU = 1;
    private static final String TAG_LOADING_FRAGMENT = "loadingFragment";

    @InjectView(R.id.lv_menu)
    RecyclerView recyclerView;

    MenuListAdapter menuListAdapter;
    RecyclerView.LayoutManager layoutManager;
    boolean isNewLoading = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isNewLoading = savedInstanceState == null;
        EventBus.getDefault().register(this);
        TrackHelper.sendScreen(this.getClass());
        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_menu_list, container, false);
        ButterKnife.inject(this, root);

        menuListAdapter = new MenuListAdapter(getActivity());
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setAdapter(menuListAdapter);
        recyclerView.setLayoutManager(layoutManager);

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showLoadingLayout();
        getLoaderManager().initLoader(ID_LOADER_DAY_MENU, null, this);
    }

    @Override
    public Loader<List<MenuListAdapter.DayMenuData>> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ID_LOADER_DAY_MENU:
                return new DayMenuLoader(getActivity());
        }
        return null;
    }

    @Override
    public void onStop() {
        super.onStop();
        dismissLoadingLayout(false);
    }

    public void onEventMainThread(MenuUpdateEvent event){
        switch (event.getState()){
            case MenuUpdateEvent.STATE_PRE:
                showLoadingLayout();
                break;

            case MenuUpdateEvent.STATE_POST:
                getLoaderManager().restartLoader(ID_LOADER_DAY_MENU, null, this);
                break;

            case MenuUpdateEvent.STATE_POST_FAILED:
                dismissLoadingLayout(true);
                break;
        }
    }

    private void showLoadingLayout(){
        if(getFragmentManager().findFragmentByTag(TAG_LOADING_FRAGMENT) == null){
            getFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.animator.slide_in_top, R.animator.slide_out_bottom)
                    .add(R.id.body_fragment, new LoadingFragment(), TAG_LOADING_FRAGMENT)
                    .commitAllowingStateLoss();
        }
    }

    private void dismissLoadingLayout(boolean useAnimation){
        Fragment fragment = getFragmentManager().findFragmentByTag(TAG_LOADING_FRAGMENT);
        if(fragment != null){
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if(useAnimation){
                ft.setCustomAnimations(R.animator.slide_in_top, R.animator.slide_out_top);
            }
            ft.remove(fragment);
            ft.commitAllowingStateLoss();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.activity_main, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_update:
                Intent intent = new Intent(getActivity(), MenuListUpdateService.class);
                intent.putExtra(MenuListUpdateService.EXTRA_TASK, MenuListUpdateService.Task.PassiveUpdate.name());
                getActivity().startService(intent);
                TrackHelper.sendEvent("Menu", "Update", "ActionBar");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onLoadFinished(Loader<List<MenuListAdapter.DayMenuData>> loader, List<MenuListAdapter.DayMenuData> data) {
        menuListAdapter.setData(data);
        menuListAdapter.notifyDataSetChanged();
        int todayPosition = getTodayPosition(data);
        if(todayPosition >= 0) {
            menuListAdapter.setTodayPosition(todayPosition);
            recyclerView.smoothScrollToPosition(todayPosition);
        }else if(isNewLoading){
            updateMenuList();
        }
        isNewLoading = false;
        if(PackHelper.isMyServiceRunning(getActivity(), MenuListUpdateService.class) == false){
            dismissLoadingLayout(true);
        }
    }


    @Override
    public void onLoaderReset(Loader<List<MenuListAdapter.DayMenuData>> loader) {

    }

    private void updateMenuList(){
        Intent intent = new Intent(getActivity(), MenuListUpdateService.class);
        intent.putExtra(MenuListUpdateService.EXTRA_TASK, MenuListUpdateService.Task.PassiveUpdate.name());
        getActivity().startService(intent);
    }

    private int getTodayPosition(List<MenuListAdapter.DayMenuData> data) {
        Date date = DateHelper.getMidnightCalendar().getTime();
        for (int i = 0; i < data.size(); ++i) {
            MenuListAdapter.DayMenuData item = data.get(i);
            if (item.getDate().equals(date) || item.getDate().after(date)) {
                return i;
            }
        }
        return -1;
    }
}
