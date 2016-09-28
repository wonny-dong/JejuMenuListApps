package com.dongwon.menulist.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import com.dongwon.menulist.R;
import com.dongwon.menulist.database.Values;
import com.dongwon.menulist.service.ApkUpdateService;
import com.dongwon.menulist.service.ScheduleManagedService;
import com.dongwon.menulist.util.Logger;
import com.dongwon.menulist.util.TrackHelper;


public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private Values preference;

    private Preference lunchTime;
    private Preference dinnerTime;
    private Preference productUpdate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        preference = Values.getInstance();
        lunchTime = findPreference("AlarmShowTimeForLunch");
        dinnerTime = findPreference("AlarmShowTimeForDinner");
        productUpdate = findPreference("productUpdate");
        productUpdate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                getActivity().startService(new Intent(getActivity(), ApkUpdateService.class));
                return false;
            }
        });
        try {
            String version = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
            Preference versionPreference = findPreference("version");
            versionPreference.setSummary(version);
        } catch (PackageManager.NameNotFoundException e) {
            TrackHelper.sendException(e);
        }
        TrackHelper.sendScreen(this.getClass());
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        lunchTime.setSummary(preference.get(Values.StringType.AlarmShowTimeForLunch));
        dinnerTime.setSummary(preference.get(Values.StringType.AlarmShowTimeForDinner));
        productUpdate.setSummary(preference.get(Values.BoolType.isNewApkUpdate) ? R.string.setting_product_release_summary : R.string.setting_product_check_summary);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(keyEquals(key, Values.StringType.AlarmShowTimeForLunch)){
            lunchTime.setSummary(preference.get(Values.StringType.AlarmShowTimeForLunch));
        }else if( keyEquals(key, Values.StringType.AlarmShowTimeForDinner)){
            dinnerTime.setSummary(preference.get(Values.StringType.AlarmShowTimeForDinner));
        }else if(keyEquals(key, Values.BoolType.isNewApkUpdate)){
            if(preference.get(Values.BoolType.isNewApkUpdate)){
                productUpdate.setSummary(R.string.setting_product_release_summary);
            }else{
                productUpdate.setSummary(R.string.setting_product_check_summary);
            }
        }

        if(keyEquals(key, Values.StringType.AlarmShowTimeForLunch)
                || keyEquals(key, Values.StringType.AlarmShowTimeForDinner)
                || keyEquals(key, Values.BoolType.UseAlarmForLunch)
                || keyEquals(key, Values.BoolType.UseAlarmForDinner)){
            getActivity().startService(new Intent(getActivity(), ScheduleManagedService.class));
        }
    }

    private boolean keyEquals(String key, Values.Type type){
        return key.equals(type.name());
    }
}
