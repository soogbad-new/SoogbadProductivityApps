package com.soogbad.soogbadreminders;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.soogbad.sharedmodule.ItemActionBar;
import com.soogbad.sharedmodule.ItemLayout;
import com.soogbad.sharedmodule.ItemsManager;
import com.soogbad.sharedmodule.Utility;

public class ReminderActivity extends AppCompatActivity {

    private ItemLayout reminderLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.setWindowProperties(this, R.layout.activity_reminder, R.id.toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.constraintLayout), this::onApplyWindowInsetsListener);
        reminderLayout = findViewById(R.id.reminderLayout); ItemActionBar reminderActionBar = findViewById(R.id.itemActionBar);
        ItemsManager<Reminder, Reminder.ReminderOptions> remindersManager = ((SoogbadRemindersApplication)getApplication()).getRemindersManager();
        Reminder reminder = remindersManager.getItem(getIntent().getStringExtra("item_uuid"));
        if(reminder == null) {
            Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show();
            finishAndRemoveTask();
            return;
        }
        reminderLayout.init(reminderActionBar, remindersManager, reminder);
    }

    @Override
    protected void onPause() {
        super.onPause();
        reminderLayout.save();
    }

    public WindowInsetsCompat onApplyWindowInsetsListener(View view, WindowInsetsCompat insets) {
        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        view.getRootView().findViewById(R.id.toolbar).setPadding(0, systemBars.top, 0, 0);
        view.setPadding(0, 0, 0, systemBars.bottom);
        Insets keyboard = insets.getInsets(WindowInsetsCompat.Type.ime());
        reminderLayout.getFormattingToolbar().setPadding(0, 0, 0, keyboard.bottom - systemBars.bottom);
        return insets;
    }

}
