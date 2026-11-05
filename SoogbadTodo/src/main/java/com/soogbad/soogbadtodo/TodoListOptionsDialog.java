package com.soogbad.soogbadtodo;

import android.content.Intent;
import android.content.Context;
import android.text.SpannedString;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.android.material.button.MaterialButton;
import com.soogbad.sharedmodule.core.Utility;
import com.soogbad.sharedmodule.richtext.RichTextSerializer;
import com.soogbad.sharedmodule.ui.ItemActivity;
import com.soogbad.sharedmodule.ui.ItemOptionsDialog;

import java.util.ArrayList;
import java.util.function.Consumer;

public class TodoListOptionsDialog extends ItemOptionsDialog<TodoList.Options> {

    private Spinner dayOfWeekSpinner;
    private TimePicker timePicker;
    private MaterialButton skipNextRunButton, resetButton;

    private SpannedString currentDefaultContent = null;

    public TodoListOptionsDialog(Context context, TodoList.Options initialOptions, Consumer<TodoList.Options> callback) { super(context, initialOptions, callback); }

    @Override
    public void show() {
        View view = inflateDialogView(R.layout.todo_list_options_dialog_content);
        dayOfWeekSpinner = view.findViewById(R.id.dayOfWeekSpinner); timePicker = view.findViewById(R.id.timePicker);
        skipNextRunButton = addToggleButton(R.drawable.skip_next, "Skip Next Run", initialOptions.SkipNextRun);
        addActionButton(R.drawable.edit_note, "Edit Default Content", this::launchEditDefaultContentActivity);
        resetButton = addToggleButton(R.drawable.restore_page, "Reset", false);
        ArrayList<String> dayNames = new ArrayList<>();
        for(TodoList.DayOfWeek day : TodoList.DayOfWeek.values()) dayNames.add(day.displayName());
        dayOfWeekSpinner.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, dayNames.toArray())); dayOfWeekSpinner.setSelection(initialOptions.Day.ordinal());
        timePicker.setIs24HourView(true); timePicker.setHour(initialOptions.Hour); timePicker.setMinute(initialOptions.Minute);
        showDialog();
    }
    @Override
    protected void onConfirm() {
        TodoList.Options options = new TodoList.Options(TodoList.DayOfWeek.values()[dayOfWeekSpinner.getSelectedItemPosition()], timePicker.getHour(), timePicker.getMinute(), skipNextRunButton.isChecked(), currentDefaultContent != null ? currentDefaultContent : initialOptions.DefaultContent);
        options.ResetRequested = resetButton.isChecked();
        callback.accept(options);
        if(options.ResetRequested && Utility.getActivity(context) instanceof ItemActivity itemActivity)
            itemActivity.reloadContent();
    }

    private ActivityResultLauncher<Intent> launcher = null;
    private void launchEditDefaultContentActivity() {
        ActivityResultRegistry registry = ((ComponentActivity)Utility.getActivity(context)).getActivityResultRegistry();
        launcher = registry.register("edit_default_content", new ActivityResultContracts.StartActivityForResult(), this::onEditDefaultContentActivityResult);
        String serializedDefaultContent = RichTextSerializer.serialize(currentDefaultContent != null ? currentDefaultContent : initialOptions.DefaultContent);
        launcher.launch(new Intent(context, EditDefaultContentActivity.class).putExtra("initial_default_content", serializedDefaultContent));
    }
    private void onEditDefaultContentActivityResult(ActivityResult result) {
        launcher.unregister(); launcher = null;
        if(result.getData() != null)
            currentDefaultContent = RichTextSerializer.deserialize(result.getData().getStringExtra("default_content"));
    }

}
