package com.soogbad.soogbadreminders;

import com.soogbad.sharedmodule.Item;
import com.soogbad.sharedmodule.ItemApplication;
import com.soogbad.sharedmodule.ItemsManager;
import com.soogbad.sharedmodule.StorageManager;

public class SoogbadRemindersApplication extends ItemApplication<Reminder, Reminder.ReminderOptions> {

    @Override
    public void onCreate() {
        super.onCreate();
        itemsManager = new ItemsManager<>(new StorageManager(getFilesDir().toPath()), Reminder::create, Reminder.ReminderOptions::fromJson);
        itemsManager.loadItems();
    }

    @Override
    public AppUtility getAppUtility() {
        return new AppUtility() {
            @Override public void deleteItem(Item<?> item) { itemsManager.deleteItem(item); }
            @Override public String getItemUuidPrefix() { return "REMINDER-"; }
        };
    }

}
