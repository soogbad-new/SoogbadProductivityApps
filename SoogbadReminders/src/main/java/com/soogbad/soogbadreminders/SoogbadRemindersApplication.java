package com.soogbad.soogbadreminders;

import android.app.Application;

import com.soogbad.sharedmodule.ItemsManager;
import com.soogbad.sharedmodule.StorageManager;

public class SoogbadRemindersApplication extends Application {

    private ItemsManager<Reminder, Reminder.ReminderOptions> remindersManager;
    public ItemsManager<Reminder, Reminder.ReminderOptions> getRemindersManager() { return remindersManager; }

    @Override
    public void onCreate() {
        super.onCreate();
        remindersManager = new ItemsManager<>(new StorageManager(getFilesDir().toPath()), Reminder::create, Reminder.ReminderOptions::fromJson);
        remindersManager.loadItems();
    }

}
