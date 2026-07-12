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
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;

import com.soogbad.sharedmodule.core.Item;
import com.soogbad.sharedmodule.core.Utility;
import com.soogbad.sharedmodule.richtext.RichTextSerializer;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

public class TodoListOptionsDialog {

    private static SpannedString defaultContent;

    public interface OptionsDialogCallback {
        void onConfirm(TodoList.DayOfWeek day, int hour, int minute, boolean skipNextRun);
    }

    @SuppressLint("ScheduleExactAlarm")
    public static void launchEditItemOptionsDialog(Context context, Item<?> item, Consumer<Item.Options> callback) {
        TodoList todoList = (TodoList)item;
        defaultContent = todoList.Options.DefaultContent;
        showOptionsDialog(context, todoList.Options, (day, hour, minute, skipNextRun) -> {
            todoList.Options.Day = day; todoList.Options.Hour = hour; todoList.Options.Minute = minute; todoList.Options.SkipNextRun = skipNextRun;
            todoList.Options.DefaultContent = defaultContent;
            callback.accept(todoList.Options);
            Utility.getAppUtility(context).getItemScheduler().scheduleItem(todoList);
        });
    }
    @SuppressLint("ScheduleExactAlarm")
    public static void launchCreateItemOptionsDialog(Context context, Function<Item.Options, String> callback) {
        showOptionsDialog(context, TodoList.getDefaultOptions(), (day, hour, minute, skipNextRun) -> {
                String uuid = callback.apply(new TodoList.Options(day, hour, minute, defaultContent, skipNextRun));
                Utility.getAppUtility(context).getItemScheduler().scheduleItem((TodoList) Utility.getItemsManager(context).getItem(uuid));
            }
        );
    }

    private static void showOptionsDialog(Context context, TodoList.Options initialOptions, OptionsDialogCallback optionsDialogCallback) {
        View view = LayoutInflater.from(context).inflate(R.layout.todo_list_options_dialog, null);
        ArrayList<String> dayNames = new ArrayList<>();
        for(TodoList.DayOfWeek day : TodoList.DayOfWeek.values())
            dayNames.add(day.displayName());
        TimePicker timePicker = view.findViewById(R.id.timePicker); Spinner dayOfWeekSpinner = view.findViewById(R.id.dayOfWeekSpinner); SwitchMaterial skipNextRunSwitch = view.findViewById(R.id.skipNextRunSwitch); MaterialButton editDefaultContentButton = view.findViewById(R.id.editDefaultContentButton);
        dayOfWeekSpinner.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, dayNames.toArray())); dayOfWeekSpinner.setSelection(initialOptions.Day.ordinal());
        timePicker.setIs24HourView(true); timePicker.setHour(initialOptions.Hour); timePicker.setMinute(initialOptions.Minute);
        skipNextRunSwitch.setChecked(initialOptions.SkipNextRun);
        editDefaultContentButton.setOnClickListener((v) -> launchEditDefaultContentActivity(context, initialOptions.DefaultContent, TodoListOptionsDialog::onEditDefaultContentActivityResult));
        new MaterialAlertDialogBuilder(context, com.soogbad.sharedmodule.R.style.OptionsDialogTheme).setTitle("Edit Options").setView(view)
                .setPositiveButton("OK", (dialog, which) ->
                        optionsDialogCallback.onConfirm(TodoList.DayOfWeek.values()[dayOfWeekSpinner.getSelectedItemPosition()], timePicker.getHour(), timePicker.getMinute(), skipNextRunSwitch.isChecked()))
                .setNegativeButton("Cancel", null).show();
    }

    private static ActivityResultLauncher<Intent> launcher = null;
    private static void launchEditDefaultContentActivity(Context context, SpannedString initialDefaultContent, Consumer<SpannedString> onActivityResult) {
        ActivityResultRegistry registry = ((ComponentActivity)Utility.getActivity(context)).getActivityResultRegistry();
        launcher = registry.register("edit_default_content", new ActivityResultContracts.StartActivityForResult(), result -> {
            launcher.unregister(); launcher = null;
            if(result.getData() != null)
                onActivityResult.accept(RichTextSerializer.deserialize(result.getData().getStringExtra("default_content")));
        });
        launcher.launch(new Intent(context, EditDefaultContentActivity.class).putExtra("initial_default_content", RichTextSerializer.serialize(initialDefaultContent)));
    }
    private static void onEditDefaultContentActivityResult(SpannedString defaultContent) {
        TodoListOptionsDialog.defaultContent = defaultContent;
    }

}
