package com.soogbad.soogbadreminders;

import android.os.Bundle;
import android.view.View;

import androidx.core.view.ViewCompat;

import com.soogbad.sharedmodule.ui.ItemListActivity;
import com.soogbad.sharedmodule.core.ItemsManager;
import com.soogbad.sharedmodule.core.Utility;
import com.soogbad.sharedmodule.core.Schedule;

import java.util.Date;

public class ReminderListActivity extends ItemListActivity {

    private ItemsManager<Reminder, Reminder.ReminderOptions> typedItemsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.setWindowProperties(this, R.layout.reminder_list_activity, R.id.toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), this::onApplyWindowInsetsListener);
        typedItemsManager = ((SoogbadRemindersApplication)getApplication()).getItemsManager();
        typedItemsManager.getItems().sort((a, b) -> Long.compare(b.Options.Time.getTime(), a.Options.Time.getTime()));
    }

    public void onAddButtonClick(View view) {
        String uuid = typedItemsManager.createItem(new Reminder.ReminderOptions(new Date(), Schedule.NONE, , false));
        super.onAddButtonClick(uuid);
    }

    @Override protected View getToolbar() { return findViewById(R.id.toolbar); }

}
