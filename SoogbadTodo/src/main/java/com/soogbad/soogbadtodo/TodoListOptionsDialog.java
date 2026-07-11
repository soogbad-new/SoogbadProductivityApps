package com.soogbad.soogbadtodo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.Context;
import android.text.SpannedString;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;

import com.soogbad.sharedmodule.core.Item;
import com.soogbad.sharedmodule.core.Utility;
import com.soogbad.sharedmodule.richtext.RichTextSerializer;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

public class TodoListOptionsDialog {

    public interface OptionsDialogCallback {
        void onConfirm(TodoList.DayOfWeek day, int hour, int minute, boolean skipNextRun);
    }

    @SuppressLint("ScheduleExactAlarm")
    public static void launchEditItemOptionsDialog(Context context, Item<?> item, Consumer<Item.Options> callback) {
        TodoList todoList = (TodoList)item;
        showOptionsDialog(context, todoList.Options, (day, hour, minute, skipNextRun) -> {
            todoList.Options.Day = day; todoList.Options.Hour = hour; todoList.Options.Minute = minute; todoList.Options.SkipNextRun = skipNextRun;
            launchEditDefaultContentActivity(context, todoList.Options.DefaultContent, defaultContent -> {
                todoList.Options.DefaultContent = defaultContent;
                callback.accept(todoList.Options);
                Utility.getAppUtility(context).getItemScheduler().scheduleItem(todoList);
            });
        });
    }
    @SuppressLint("ScheduleExactAlarm")
    public static void launchCreateItemOptionsDialog(Context context, Function<Item.Options, String> callback) {
        showOptionsDialog(context, TodoList.getDefaultOptions(), (day, hour, minute, skipNextRun) ->
            launchEditDefaultContentActivity(context, new SpannedString(""), defaultContent -> {
                String uuid = callback.apply(new TodoList.Options(day, hour, minute, defaultContent, skipNextRun));
                Utility.getAppUtility(context).getItemScheduler().scheduleItem((TodoList)Utility.getItemsManager(context).getItem(uuid));
            })
        );
    }

    private static void showOptionsDialog(Context context, TodoList.Options initialOptions, OptionsDialogCallback callback) {
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
        new MaterialAlertDialogBuilder(context, com.soogbad.sharedmodule.R.style.OptionsDialogTheme).setTitle("Edit Options").setView(view)
                .setPositiveButton("OK", (dialog, which) ->
                    callback.onConfirm(TodoList.DayOfWeek.values()[spinner.getSelectedItemPosition()], timePicker.getHour(), timePicker.getMinute(), skipNextRunSwitch.isChecked()))
                .setNegativeButton("Cancel", null).show();
    }

    private static void launchEditDefaultContentActivity(Context context, SpannedString initialDefaultContent, Consumer<SpannedString> onResult) {
        ActivityResultLauncher<Intent> launcher = ((ComponentActivity)context).registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getData() != null)
                onResult.accept(RichTextSerializer.deserialize(result.getData().getStringExtra("default_text")));
        });
        launcher.launch(new Intent(context, EditDefaultContentActivity.class).putExtra("initial_default_text", RichTextSerializer.serialize(initialDefaultContent)));
    }

}
