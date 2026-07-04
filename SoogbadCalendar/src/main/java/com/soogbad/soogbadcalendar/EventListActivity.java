package com.soogbad.soogbadcalendar;

import android.os.Bundle;
import android.view.View;

import androidx.core.view.ViewCompat;

import com.soogbad.sharedmodule.ui.ItemListActivity;
import com.soogbad.sharedmodule.core.ItemsManager;
import com.soogbad.sharedmodule.core.Utility;

public class EventListActivity extends ItemListActivity {

    private ItemsManager<Event, Event.EventOptions> itemsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.setWindowProperties(this, R.layout.event_list_activity, R.id.toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), this::onApplyWindowInsetsListener);
        itemsManager = ((SoogbadCalendarApplication)getApplication()).getItemsManager();
    }

    public void onAddButtonClick(View view) {
        Utility.getAppUtility(this).launchCreateItemOptionsDialog(this, options -> {
            String uuid = itemsManager.createItem((Event.EventOptions)options);
            createItem(uuid);
        });
    }

    @Override protected View getToolbar() { return findViewById(R.id.toolbar); }

}
