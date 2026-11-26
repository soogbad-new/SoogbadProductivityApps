package com.soogbad.soogbadcalendar;

import android.content.Context;
import android.icu.util.Calendar;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.soogbad.sharedmodule.core.Utility;
import com.soogbad.sharedmodule.ui.ItemOptionsDialog;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.function.Consumer;

public class EventOptionsDialog extends ItemOptionsDialog<Event.Options> {

    private MaterialButton datePickerButton, timePickerButton, importanceButton, repeatButton;

    private LocalDate selectedDate;
    private LocalTime selectedTime;
    private Event.EventColor eventColor;
    private Event.Importance importance;
    private Event.Schedule repeatSchedule;

    public EventOptionsDialog(Context context, Event.Options initialOptions, Consumer<Event.Options> callback) { super(context, initialOptions, callback); }

    @Override
    public void show() {
        View view = inflateDialogView(R.layout.event_options_dialog_content);
        datePickerButton = view.findViewById(R.id.datePickerButton); timePickerButton = view.findViewById(R.id.timePickerButton);
        datePickerButton.setOnClickListener(v -> showMaterialDatePicker()); timePickerButton.setOnClickListener(v -> showMaterialTimePicker());
        eventColor = initialOptions.EventColor; importance = initialOptions.Importance; repeatSchedule = initialOptions.RepeatSchedule;
        Event.EventColor[] colors = Event.EventColor.values();
        int[] colorValues = new int[colors.length];
        for(int i = 0; i < colors.length; i++) colorValues[i] = colors[i].color;
        addColorSelectionButton(R.drawable.event_color, "Event Color", colorValues, eventColor.ordinal(), i -> eventColor = colors[i]);
        Event.Importance[] importances = Event.Importance.values();
        String[] importanceNames = new String[importances.length];
        for(int i = 0; i < importances.length; i++) importanceNames[i] = importances[i].displayName();
        int[] importanceIcons = { R.drawable.event_importance_silent, R.drawable.event_importance_sound, R.drawable.event_importance_alarm };
        importanceButton = addTextSelectionButton(importanceIcons[importance.ordinal()], "Importance", importanceNames, importance.ordinal(), i -> {
            importance = importances[i];
            importanceButton.setIconResource(importanceIcons[i]);
        });
        Event.Schedule[] schedules = Event.Schedule.values();
        String[] scheduleNames = new String[schedules.length];
        for(int i = 0; i < schedules.length; i++) scheduleNames[i] = schedules[i].displayName();
        repeatButton = addTextSelectionButton(R.drawable.event_repeat_schedule, "Repeat Schedule", scheduleNames, repeatSchedule.ordinal(), i -> {
            repeatSchedule = schedules[i];
            toggleButton(repeatButton, repeatSchedule != Event.Schedule.NONE);
        });
        toggleButton(repeatButton, repeatSchedule != Event.Schedule.NONE);
        selectedDate = initialOptions.Time.toLocalDate(); selectedTime = initialOptions.Time.toLocalTime();
        datePickerButton.setText(selectedDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
        timePickerButton.setText(selectedTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)));
        showDialog();
    }

    private void toggleButton(MaterialButton button, boolean enabled) {
        button.setIconTint(ContextCompat.getColorStateList(context, enabled ? com.soogbad.sharedmodule.R.color.selectedGreen : com.soogbad.sharedmodule.R.color.white));
    }

    @Override
    protected void onConfirm() {
        callback.accept(new Event.Options(LocalDateTime.of(selectedDate, selectedTime), eventColor, importance, repeatSchedule, initialOptions.DeletedOccurrences));
    }

    private void showMaterialDatePicker() {
        long selection = selectedDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select Date")
                .setCalendarConstraints(new CalendarConstraints.Builder().setFirstDayOfWeek(Calendar.SUNDAY).build()).setSelection(selection).build();
        datePicker.addOnPositiveButtonClickListener(selectionMs -> {
            selectedDate = Instant.ofEpochMilli(selectionMs).atZone(ZoneOffset.UTC).toLocalDate();
            datePickerButton.setText(selectedDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
        });
        datePicker.addOnDismissListener(dialog -> {
            WindowInsetsController controller = datePickerButton.getWindowInsetsController();
            if(controller != null) controller.hide(WindowInsets.Type.ime());
        });
        datePicker.show(((FragmentActivity)Utility.getActivity(context)).getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
    }
    private void showMaterialTimePicker() {
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder().setTitleText("Select Time")
                .setTimeFormat(TimeFormat.CLOCK_24H).setHour(selectedTime.getHour()).setMinute(selectedTime.getMinute()).build();
        timePicker.addOnPositiveButtonClickListener(view -> {
            selectedTime = LocalTime.of(timePicker.getHour(), timePicker.getMinute());
            timePickerButton.setText(selectedTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)));
        });
        timePicker.addOnDismissListener(dialog -> {
            WindowInsetsController controller = timePickerButton.getWindowInsetsController();
            if(controller != null) controller.hide(WindowInsets.Type.ime());
        });
        timePicker.show(((FragmentActivity)Utility.getActivity(context)).getSupportFragmentManager(), "MATERIAL_TIME_PICKER");
    }

}
