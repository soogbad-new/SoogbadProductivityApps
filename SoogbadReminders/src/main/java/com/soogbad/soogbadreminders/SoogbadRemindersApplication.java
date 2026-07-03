package com.soogbad.soogbadreminders;

import com.soogbad.sharedmodule.ui.ItemActivity;
import com.soogbad.sharedmodule.core.ItemApplication;
import com.soogbad.sharedmodule.core.ItemsManager;
import com.soogbad.sharedmodule.core.StorageManager;

public class SoogbadRemindersApplication extends ItemApplication<Reminder, Reminder.ReminderOptions> {

    @Override
    public void onCreate() {
        super.onCreate();
        itemsManager = new ItemsManager<>(new StorageManager(getFilesDir().toPath()), Reminder::create, Reminder.ReminderOptions::fromJson);
        itemsManager.loadItems();
        itemsManager.getItems().sort((a, b) -> Long.compare(b.Options.Time.getTime(), a.Options.Time.getTime()));
    }

    @Override
    public AppUtility getAppUtility() {
        return new AppUtility() {
            @Override public String getAppName() { return "SoogbadReminders"; }
            @Override public String getItemName() { return "Reminder"; }
            @Override public Class<? extends ItemActivity> getItemActivityClass() { return ReminderActivity.class; }
            @Override public boolean hasConfigurableOptions() { return true; }
            @Override public void launchEditItemOptionsDialog() {

            }
            @Override public Reminder.ReminderOptions launchCreateItemOptionsDialog() {
                return null;
            }
        };
    }

}
