package com.soogbad.soogbadcalendar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.soogbad.sharedmodule.core.ItemsManager;
import com.soogbad.sharedmodule.core.Utility;
import com.soogbad.sharedmodule.ui.ItemListActivity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;

public class DayListActivity extends ItemListActivity {

    private ItemsManager<Event, Event.Options> itemsManager;
    private LocalDate day;
    private final ArrayList<Event> dayEvents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.setWindowProperties(this, R.layout.day_list_activity, R.id.toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), this::onApplyWindowInsetsListener);
        itemsManager = ((SoogbadCalendarApplication)getApplication()).getItemsManager();
        day = LocalDate.parse(getIntent().getStringExtra("date"));
        setTitle(day.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)));
    }
    @Override
    protected RecyclerView.Adapter<?> createItemListAdapter() {
        return new EventListAdapter(dayEvents, this::reloadDayEvents);
    }

    @Override
    protected void onResume() {
        reloadDayEvents();
        super.onResume();
    }
    private void reloadDayEvents() {
        dayEvents.clear();
        dayEvents.addAll(Event.getEventsOn(itemsManager.getItems(), day));
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onAddButtonClick(View view) {
        Event event = itemsManager.createItem(Event.getDefaultOptions(day), Utility.getAppUtility(this)::onItemOptionsChanged);
        launchItem(event.UUID);
    }

    @Override protected View getToolbar() { return findViewById(R.id.toolbar); }

}
