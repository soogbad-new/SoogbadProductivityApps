package com.soogbad.soogbadtodo;

import android.app.AlertDialog;
import android.content.Context;
import android.text.SpannedString;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.google.android.material.switchmaterial.SwitchMaterial;

import com.soogbad.sharedmodule.core.Item;
import com.soogbad.sharedmodule.core.Utility;

import java.util.ArrayList;
import java.util.function.Consumer;

public class TodoListOptionsDialog {

    public static void launchEditItemOptionsDialog(Context context, Item<?> item, Consumer<Item.Options> callback) {
        TodoList todoList = (TodoList)item;
        showOptionsDialog(context, todoList.Options, (day, hour, minute, skipNextRun) -> {
            todoList.Options.Day = day; todoList.Options.Hour = hour; todoList.Options.Minute = minute; todoList.Options.SkipNextRun = skipNextRun;
            todoList.Options.DefaultText = launchEditDefaultTextActivity();
            callback.accept(todoList.Options);
        });
    }
    public static void launchCreateItemOptionsDialog(Context context, Consumer<Item.Options> callback) {
        showOptionsDialog(context, TodoList.getDefaultOptions(), (day, hour, minute, skipNextRun) -> {
            SpannedString defaultText = launchEditDefaultTextActivity();
            callback.accept(new TodoList.Options(day, hour, minute, defaultText, skipNextRun));
        });
    }

    private static void showOptionsDialog(Context context, TodoList.Options initialOptions, Callback callback) {
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
        SwitchMaterial skipNextRunSwitch = view.findViewById(R.id.skipNextRunSwitch);
        skipNextRunSwitch.setChecked(initialOptions.SkipNextRun);
        new AlertDialog.Builder(context).setTitle("Edit Options").setView(view)
                .setPositiveButton("OK", (dialog, which) ->
                    callback.onConfirm(TodoList.DayOfWeek.values()[spinner.getSelectedItemPosition()], timePicker.getHour(), timePicker.getMinute(), skipNextRunSwitch.isChecked()))
                .setNegativeButton("Cancel", null).show();
    }
    public interface Callback {
        void onConfirm(TodoList.DayOfWeek day, int hour, int minute, boolean skipNextRun);
    }

    private static SpannedString launchEditDefaultTextActivity() {
        return new SpannedString("");
    }

}
