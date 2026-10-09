package com.soogbad.soogbadcalendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.soogbad.sharedmodule.ui.ItemOptionsDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.function.Consumer;

public class EventOptionsDialog extends ItemOptionsDialog<Event.Options> {

    private DatePicker datePicker;
    private TimePicker timePicker;
    private Spinner repeatScheduleSpinner;

    public EventOptionsDialog(Context context, Event.Options initialOptions, Consumer<Event.Options> callback) { super(context, initialOptions, callback); }

    @SuppressLint("InflateParams")
    @Override
    public void show() {
        View view = LayoutInflater.from(context).inflate(R.layout.event_options_dialog, null);
        datePicker = view.findViewById(R.id.datePicker); timePicker = view.findViewById(R.id.timePicker); repeatScheduleSpinner = view.findViewById(R.id.repeatScheduleSpinner);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(initialOptions.Time);
        datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        timePicker.setIs24HourView(true); timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY)); timePicker.setMinute(calendar.get(Calendar.MINUTE));
        ArrayList<String> scheduleNames = new ArrayList<>();
        for(Event.Schedule schedule : Event.Schedule.values()) scheduleNames.add(schedule.displayName());
        repeatScheduleSpinner.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, scheduleNames.toArray())); repeatScheduleSpinner.setSelection(initialOptions.RepeatSchedule.ordinal());
        showDialog(view);
    }

    @Override
    protected void onConfirm() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getHour(), timePicker.getMinute(), 0);
        Event.Schedule repeatSchedule = Event.Schedule.values()[repeatScheduleSpinner.getSelectedItemPosition()];
        callback.accept(new Event.Options(calendar.getTime(), repeatSchedule));
    }

}
