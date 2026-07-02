package com.soogbad.soogbadcalendar;

import android.os.Bundle;
import android.view.View;

import androidx.core.view.ViewCompat;

import com.soogbad.sharedmodule.ui.ItemListActivity;
import com.soogbad.sharedmodule.core.ItemsManager;
import com.soogbad.sharedmodule.core.Utility;
import com.soogbad.sharedmodule.core.Schedule;

import java.util.Date;

public class EventListActivity extends ItemListActivity {

    private ItemsManager<Event, Event.EventOptions> typedItemsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.setWindowProperties(this, R.layout.event_list_activity, R.id.toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), this::onApplyWindowInsetsListener);
        typedItemsManager = ((SoogbadCalendarApplication)getApplication()).getItemsManager();
    }

    public void onAddButtonClick(View view) {
        Event.EventOptions options = launchCreateItemOptionsDialog();
        String uuid = typedItemsManager.createItem(options);
        super.onAddButtonClick(uuid);
    }

    @Override protected View getToolbar() { return findViewById(R.id.toolbar); }

}
