package com.soogbad.soogbadreminders;

import android.os.Bundle;
import android.view.View;

import androidx.core.view.ViewCompat;

import com.soogbad.sharedmodule.ItemListActivity;
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

    @Override
    protected String createItem() {
        return ((SoogbadRemindersApplication)getApplication()).getItemsManager().createItem(new Reminder.ReminderOptions(new Date(), Schedule.NONE));
    }

    @Override protected View getToolbar() { return findViewById(R.id.toolbar); }

}
