package com.soogbad.soogbadcalendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.soogbad.sharedmodule.ui.ItemOptionsDialog;

import java.time.LocalDateTime;
import java.util.function.Consumer;

public class EventOptionsDialog extends ItemOptionsDialog<Event.Options> {

    private DatePicker datePicker;
    private TimePicker timePicker;

    public EventOptionsDialog(Context context, Event.Options initialOptions, Consumer<Event.Options> callback) { super(context, initialOptions, callback); }

    @SuppressLint("InflateParams")
    @Override
    public void show() {
        View view = LayoutInflater.from(context).inflate(R.layout.event_options_dialog, null);
        datePicker = view.findViewById(R.id.datePicker); timePicker = view.findViewById(R.id.timePicker);
        LocalDateTime time = initialOptions.Time;
        datePicker.updateDate(time.getYear(), time.getMonthValue() - 1, time.getDayOfMonth());
        timePicker.setIs24HourView(true); timePicker.setHour(time.getHour()); timePicker.setMinute(time.getMinute());
        showDialog(view);
    }

    @Override
    protected void onConfirm() {
        LocalDateTime time = LocalDateTime.of(datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth(), timePicker.getHour(), timePicker.getMinute());
        callback.accept(new Event.Options(time, initialOptions.EventColor, initialOptions.RepeatSchedule));
    }

}
