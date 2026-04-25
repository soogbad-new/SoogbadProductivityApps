package com.soogbad.soogbadcalendar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.soogbad.sharedmodule.ItemListAdapter;
import com.soogbad.sharedmodule.ItemsManager;
import com.soogbad.sharedmodule.Schedule;
import com.soogbad.sharedmodule.Utility;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private RecyclerView eventList;
    private ItemsManager<Event, Event.EventOptions> eventsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.setWindowProperties(this, R.layout.activity_main, R.id.actionBar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.constraintLayout), this::onApplyWindowInsetsListener);
        eventList = findViewById(R.id.eventList);
        eventsManager = ((SoogbadCalendarApplication)getApplication()).getEventsManager();
        eventList.setLayoutManager(new LinearLayoutManager(this));
        eventList.setAdapter(new ItemListAdapter(eventsManager.getItems(), R.layout.event_list_item, R.id.itemTitleTextView));
    }

    public void onAddButtonClick(View view) {
        String uuid = eventsManager.createItem(new Event.EventOptions(new Date(), Schedule.NONE));
        if(eventList.getAdapter() != null)
            eventList.getAdapter().notifyItemInserted(0);
        startActivity(new Intent(this, EventActivity.class).putExtra("item_uuid", uuid));
    }

    public void onEventListItemClick(View view) {
        startActivity(new Intent(this, EventActivity.class).putExtra("item_uuid", view.getTag().toString()));
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();
        if(eventList != null && eventList.getAdapter() != null)
            eventList.getAdapter().notifyDataSetChanged();
    }

    public WindowInsetsCompat onApplyWindowInsetsListener(View view, WindowInsetsCompat insets) {
        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        view.getRootView().findViewById(R.id.actionBar).setPadding(0, systemBars.top, 0, 0);
        view.setPadding(0, 0, 0, systemBars.bottom);
        return insets;
    }

}
