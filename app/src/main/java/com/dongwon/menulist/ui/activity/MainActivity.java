package com.dongwon.menulist.ui.activity;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.dongwon.menulist.R;
import com.dongwon.menulist.ui.fragment.BaseFragment;
import com.dongwon.menulist.ui.fragment.MenuListFragment;
import com.dongwon.menulist.ui.fragment.SettingsFragment;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{
    public final static String EXTRA_SELECTED_MENU = "EXTRA_SELECTED_MENU";
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @InjectView(R.id.left_drawer)
    NavigationView leftDrawer;

    ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        setSupportActionBar(toolbar);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.left_drawer_open, R.string.left_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        leftDrawer.setNavigationItemSelectedListener(this);

        if(savedInstanceState == null){
            int number = getIntent().getIntExtra(EXTRA_SELECTED_MENU, 0);
            onNavigationItemSelected(leftDrawer.getMenu().getItem(number));
        }else{
            Fragment nowFragment = getFragmentManager().findFragmentById(R.id.body_fragment);
            if(nowFragment instanceof MenuListFragment){
                leftDrawer.getMenu().getItem(0).setChecked(true);
            }else if(nowFragment instanceof SettingsFragment){
                leftDrawer.getMenu().getItem(1).setChecked(true);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int number = intent.getIntExtra(EXTRA_SELECTED_MENU, 0);
        onNavigationItemSelected(leftDrawer.getMenu().getItem(number));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.body_fragment);
        if(fragment != null && fragment instanceof BaseFragment){
            if(((BaseFragment)fragment).onBackPressed()){
                return;
            }
        }

        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawers();
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        drawerLayout.closeDrawer(leftDrawer);
        if(menuItem.isChecked()){
            return false;
        }else{
            int id = menuItem.getItemId();
            Fragment replaceFragment = null;
            if(id == R.id.left_drawer_1){
                replaceFragment = new MenuListFragment();
            } else if (id == R.id.left_drawer_2) {
                replaceFragment = new SettingsFragment();
            }
            if(replaceFragment != null){
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.body_fragment, replaceFragment)
                        .commit();
            }
            if(getSupportActionBar() != null){
                getSupportActionBar().setTitle(menuItem.getTitle());
            }
            menuItem.setChecked(true);
            return true;
        }
    }

}
