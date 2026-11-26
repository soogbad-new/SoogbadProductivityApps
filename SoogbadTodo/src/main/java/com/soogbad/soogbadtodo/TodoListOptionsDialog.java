package com.soogbad.soogbadtodo;

import android.content.Intent;
import android.content.Context;
import android.text.SpannedString;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.soogbad.sharedmodule.core.Utility;
import com.soogbad.sharedmodule.richtext.RichTextSerializer;
import com.soogbad.sharedmodule.ui.ItemActivity;
import com.soogbad.sharedmodule.ui.ItemOptionsDialog;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.function.Consumer;

public class TodoListOptionsDialog extends ItemOptionsDialog<TodoList.Options> {

    private Spinner dayOfWeekSpinner;
    private MaterialButton timePickerButton;

    private LocalTime selectedTime;
    private boolean skipNextRun, resetRequested;
    private SpannedString defaultContent = null;

    public TodoListOptionsDialog(Context context, TodoList.Options initialOptions, Consumer<TodoList.Options> callback) { super(context, initialOptions, callback); }

    @Override
    public void show() {
        View view = inflateDialogView(R.layout.todo_list_options_dialog_content);
        dayOfWeekSpinner = view.findViewById(R.id.dayOfWeekSpinner); timePickerButton = view.findViewById(R.id.timePickerButton);
        timePickerButton.setOnClickListener(v-> showMaterialTimePicker());
        skipNextRun = initialOptions.SkipNextRun; resetRequested = false;
        addActionButton(R.drawable.edit_default_content, "Edit Default Content", this::launchEditDefaultContentActivity);
        addToggleButton(R.drawable.skip_next_run, "Skip Next Run", skipNextRun, isChecked -> skipNextRun = isChecked);
        addToggleButton(R.drawable.reset_todo_list, "Reset", resetRequested, isChecked -> resetRequested = isChecked);
        ArrayList<String> dayNames = new ArrayList<>();
        for(TodoList.DayOfWeek day : TodoList.DayOfWeek.values()) dayNames.add(day.displayName());
        dayOfWeekSpinner.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, dayNames.toArray())); dayOfWeekSpinner.setSelection(initialOptions.Day.ordinal());
        selectedTime = LocalTime.of(initialOptions.Hour, initialOptions.Minute);
        timePickerButton.setText(selectedTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)));
        showDialog();
    }

    @Override
    protected void onConfirm() {
        TodoList.Options options = new TodoList.Options(TodoList.DayOfWeek.values()[dayOfWeekSpinner.getSelectedItemPosition()], selectedTime.getHour(), selectedTime.getMinute(), skipNextRun, defaultContent != null ? defaultContent : initialOptions.DefaultContent);
        options.ResetRequested = resetRequested;
        callback.accept(options);
        if(options.ResetRequested && Utility.getActivity(context) instanceof ItemActivity itemActivity)
            itemActivity.reloadContent();
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

    private ActivityResultLauncher<Intent> launcher = null;
    private void launchEditDefaultContentActivity() {
        ActivityResultRegistry registry = ((ComponentActivity)Utility.getActivity(context)).getActivityResultRegistry();
        launcher = registry.register("edit_default_content", new ActivityResultContracts.StartActivityForResult(), this::onEditDefaultContentActivityResult);
        String serializedDefaultContent = RichTextSerializer.serialize(defaultContent != null ? defaultContent : initialOptions.DefaultContent);
        launcher.launch(new Intent(context, EditDefaultContentActivity.class).putExtra("initial_default_content", serializedDefaultContent));
    }
    private void onEditDefaultContentActivityResult(ActivityResult result) {
        launcher.unregister(); launcher = null;
        if(result.getData() != null)
            defaultContent = RichTextSerializer.deserialize(result.getData().getStringExtra("default_content"));
    }

}
