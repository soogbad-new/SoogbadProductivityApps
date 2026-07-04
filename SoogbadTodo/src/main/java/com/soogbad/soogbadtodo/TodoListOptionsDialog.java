package com.soogbad.soogbadtodo;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.util.ArrayList;

public class TodoListOptionsDialog {

    public static void show(Context context, TodoList.TodoListOptions initialOptions, Callback callback) {
        View view = LayoutInflater.from(context).inflate(R.layout.todo_list_options_dialog, null);
        ArrayList<String> dayNames = new ArrayList<>();
        for(TodoList.DayOfWeek day : TodoList.DayOfWeek.values())
            dayNames.add(day.displayName());
        Spinner spinner = view.findViewById(R.id.dayOfWeekSpinner);
        spinner.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, dayNames.toArray()));
        spinner.setSelection(initialOptions.Day.ordinal());
        TimePicker timePicker = view.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        timePicker.setHour(initialOptions.Hour); timePicker.setMinute(initialOptions.Minute);
        new AlertDialog.Builder(context).setTitle("Edit Options").setView(view)
                .setPositiveButton("OK", (dialog, which) ->
                    callback.onConfirm(TodoList.DayOfWeek.values()[spinner.getSelectedItemPosition()], timePicker.getHour(), timePicker.getMinute()))
                .setNegativeButton("Cancel", null).show();
    }
    public interface Callback {
        void onConfirm(TodoList.DayOfWeek day, int hour, int minute);
    }

}
