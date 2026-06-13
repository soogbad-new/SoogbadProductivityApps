package com.soogbad.soogbadreminders;

import android.os.Bundle;
import android.view.View;

import androidx.core.view.ViewCompat;

import com.soogbad.sharedmodule.ItemListActivity;
import com.soogbad.sharedmodule.ItemsManager;
import com.soogbad.sharedmodule.Utility;
import com.soogbad.sharedmodule.Schedule;

import java.util.Date;

public class ReminderListActivity extends ItemListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.setWindowProperties(this, R.layout.activity_reminder_list, R.id.toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), this::onApplyWindowInsetsListener);
    }

    public void onAddButtonClick(View view) {
        ItemsManager<Reminder, Reminder.ReminderOptions> itemsManager = ((SoogbadRemindersApplication)getApplication()).getItemsManager();
        String uuid = itemsManager.createItem(new Reminder.ReminderOptions(new Date(), Schedule.NONE));
        super.onAddButtonClick(uuid);
    }

    @Override protected View getToolbar() { return findViewById(R.id.toolbar); }

}
