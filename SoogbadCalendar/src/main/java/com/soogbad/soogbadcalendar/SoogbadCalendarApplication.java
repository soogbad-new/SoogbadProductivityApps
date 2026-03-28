package com.soogbad.soogbadcalendar;

import android.app.Application;

import com.soogbad.sharedmodule.ItemsManager;
import com.soogbad.sharedmodule.StorageManager;

public class SoogbadCalendarApplication extends Application {

    private ItemsManager<Event, Event.EventOptions> eventsManager;
    public ItemsManager<Event, Event.EventOptions> getEventsManager() { return eventsManager; }

    @Override
    public void onCreate() {
        super.onCreate();
        eventsManager = new ItemsManager<>(new StorageManager(getFilesDir().toPath()), Event::create, Event.EventOptions::fromJson);
        eventsManager.loadItems();
    }

}
