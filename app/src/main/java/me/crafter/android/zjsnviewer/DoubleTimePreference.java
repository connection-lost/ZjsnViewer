package me.crafter.android.zjsnviewer;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DoubleTimePreference extends DialogPreference {
    private Calendar calendar;
    private Calendar calendar2;
    private View picker = null;

    TimePicker startPicker = null;
    TimePicker endPicker = null;

    public DoubleTimePreference(Context context) {
        this(context, null);
    }

    public DoubleTimePreference(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.dialogPreferenceStyle);
    }

    public DoubleTimePreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setPositiveButtonText(R.string.settings_yes);
        setNegativeButtonText(R.string.settings_no);
        calendar = new GregorianCalendar();
        calendar2 = new GregorianCalendar();
    }

    @Override
    protected View onCreateDialogView() {
        picker = LayoutInflater.from(getContext()).inflate(R.layout.double_time_preference, null);
        return (picker);
    }

    @Override
    protected void onBindDialogView(@NonNull View view) {
        super.onBindDialogView(view);
        startPicker = (TimePicker)picker.findViewById(R.id.timePicker);
        endPicker = (TimePicker)picker.findViewById(R.id.timePicker2);
        startPicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        startPicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
        startPicker.setIs24HourView(true);
        endPicker.setCurrentHour(calendar2.get(Calendar.HOUR_OF_DAY));
        endPicker.setCurrentMinute(calendar2.get(Calendar.MINUTE));
        endPicker.setIs24HourView(true);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            calendar.set(Calendar.HOUR_OF_DAY, startPicker.getCurrentHour());
            calendar.set(Calendar.MINUTE, startPicker.getCurrentMinute());
            long startTime = (calendar.getTimeInMillis() % 86400000);
            calendar2.set(Calendar.HOUR_OF_DAY, endPicker.getCurrentHour());
            calendar2.set(Calendar.MINUTE, endPicker.getCurrentMinute());
            long endTime = (calendar2.getTimeInMillis() % 86400000);
            setSummary(getSummary());
            if (callChangeListener(toOneString(startTime, endTime))) {
                persistString(toOneString(startTime, endTime));
                notifyChanged();
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return (a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String value;
        if (restoreValue) {
            if (defaultValue == null){
                value = getPersistedString("0:0");
            } else {
                value = getPersistedString(defaultValue.toString());
            }
        } else {
            if (defaultValue == null){
                value = getPersistedString("0:0");
            } else {
                value = defaultValue.toString();
            }
        }
        long[] times = toTwoLongs(value);
        calendar.setTimeInMillis(times[0]);
        calendar2.setTimeInMillis(times[1]);
        setSummary(getSummary());
    }

    @Override
    public CharSequence getSummary() {
        if (calendar == null || calendar2 == null) {
            return null;
        }
        String summary = "";
        summary += DateFormat.getTimeFormat(getContext()).format(new Date(calendar.getTimeInMillis()));
        summary += " - ";
        summary += DateFormat.getTimeFormat(getContext()).format(new Date(calendar2.getTimeInMillis()));
        return summary;
    }

    public long[] toTwoLongs(String in){
        try {
            String[] splitted = in.split(":");
            return new long[]{Long.parseLong(splitted[0]), Long.parseLong(splitted[1])};
        } catch (Exception ex) {
            return new long[]{0L, 0L};
        }
    }

    public String toOneString(long first, long second){
        return first + ":" + second;
    }

}