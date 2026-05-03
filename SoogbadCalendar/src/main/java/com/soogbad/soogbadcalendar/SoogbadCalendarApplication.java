package com.soogbad.soogbadcalendar;

import com.soogbad.sharedmodule.ItemApplication;
import com.soogbad.sharedmodule.ItemsManager;
import com.soogbad.sharedmodule.StorageManager;

public class SoogbadCalendarApplication extends ItemApplication<Event, Event.EventOptions> {

    @Override
    public void onCreate() {
        super.onCreate();
        itemsManager = new ItemsManager<>(new StorageManager(getFilesDir().toPath()), Event::create, Event.EventOptions::fromJson);
        itemsManager.loadItems();
    }

}
