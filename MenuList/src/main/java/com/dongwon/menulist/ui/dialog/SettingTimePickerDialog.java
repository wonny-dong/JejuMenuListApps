package com.dongwon.menulist.ui.dialog;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

/**
 * Created by Dongwon on 2015-04-26.
 */
public class SettingTimePickerDialog extends DialogPreference {
    private TimePicker picker;
    private int hour;
    private int minute;

    public SettingTimePickerDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View onCreateDialogView() {
        picker = new TimePicker(getContext());

        return picker;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        picker.setCurrentHour(hour);
        picker.setCurrentMinute(minute);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if(positiveResult){
            String value = String.format("%02d:%02d",picker.getCurrentHour(), picker.getCurrentMinute());
            if (callChangeListener(value)) {
                setTime(value);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        super.onSetInitialValue(restorePersistedValue, defaultValue);
        String timeStr = restorePersistedValue ? getPersistedString("00:00") : (String)defaultValue;
        setTime(timeStr);
    }

    public void setTime(String str) {
        String[] split = str.split(":");
        this.hour = Integer.parseInt(split[0]);
        this.minute = Integer.parseInt(split[1]);
        persistString(str);
    }
}
