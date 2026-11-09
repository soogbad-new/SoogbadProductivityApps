package com.soogbad.soogbadcalendar;

import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.soogbad.sharedmodule.ui.ItemOptionsDialog;

import java.time.LocalDateTime;
import java.util.function.Consumer;

public class EventOptionsDialog extends ItemOptionsDialog<Event.Options> {

    private DatePicker datePicker;
    private TimePicker timePicker;

    private Event.EventColor eventColor;
    private Event.Importance importance;
    private Event.Schedule repeatSchedule;

    public EventOptionsDialog(Context context, Event.Options initialOptions, Consumer<Event.Options> callback) { super(context, initialOptions, callback); }

    @Override
    public void show() {
        View view = inflateDialogView(R.layout.event_options_dialog_content);
        datePicker = view.findViewById(R.id.datePicker); timePicker = view.findViewById(R.id.timePicker);
        eventColor = initialOptions.EventColor; importance = initialOptions.Importance; repeatSchedule = initialOptions.RepeatSchedule;
        Event.EventColor[] colors = Event.EventColor.values();
        int[] colorValues = new int[colors.length];
        for(int i = 0; i < colors.length; i++) colorValues[i] = colors[i].color;
        addColorSelectionButton(R.drawable.palette, "Event Color", colorValues, eventColor.ordinal(), i -> eventColor = colors[i]);
        Event.Importance[] importances = Event.Importance.values();
        String[] importanceNames = new String[importances.length];
        for(int i = 0; i < importances.length; i++) importanceNames[i] = importances[i].displayName();
        addTextSelectionButton(R.drawable.event_importance, "Importance", importanceNames, importance.ordinal(), i -> importance = importances[i]);
        Event.Schedule[] schedules = Event.Schedule.values();
        String[] scheduleNames = new String[schedules.length];
        for(int i = 0; i < schedules.length; i++) scheduleNames[i] = schedules[i].displayName();
        addTextSelectionButton(R.drawable.event_repeat, "Repeat Schedule", scheduleNames, repeatSchedule.ordinal(), i -> repeatSchedule = schedules[i]);
        LocalDateTime time = initialOptions.Time;
        datePicker.updateDate(time.getYear(), time.getMonthValue() - 1, time.getDayOfMonth());
        timePicker.setIs24HourView(true); timePicker.setHour(time.getHour()); timePicker.setMinute(time.getMinute());
        showDialog();
    }

    @Override
    protected void onConfirm() {
        LocalDateTime time = LocalDateTime.of(datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth(), timePicker.getHour(), timePicker.getMinute());
        callback.accept(new Event.Options(time, eventColor, importance, repeatSchedule, initialOptions.DeletedOccurrences));
    }

}
