package com.soogbad.soogbadcalendar;

import android.os.Bundle;
import android.view.View;

import androidx.core.view.ViewCompat;

import com.soogbad.sharedmodule.ui.ItemListActivity;
import com.soogbad.sharedmodule.core.ItemsManager;
import com.soogbad.sharedmodule.core.Utility;

public class EventListActivity extends ItemListActivity {

    private ItemsManager<Event, Event.Options> itemsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.setWindowProperties(this, R.layout.event_list_activity, R.id.toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), this::onApplyWindowInsetsListener);
        itemsManager = ((SoogbadCalendarApplication)getApplication()).getItemsManager();
    }

    public void onAddButtonClick(View view) {
        Event event = itemsManager.createItem(Event.getDefaultOptions(), Utility.getAppUtility(this)::onItemOptionsChanged);
        if(itemList.getAdapter() != null)
            itemList.getAdapter().notifyItemInserted(0);
        launchItem(event.UUID);
    }

    @Override protected View getToolbar() { return findViewById(R.id.toolbar); }

}
