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
import com.soogbad.sharedmodule.StorageManager;
import com.soogbad.sharedmodule.Utility;

public class MainActivity extends AppCompatActivity {

    private RecyclerView eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.setWindowProperties(this, R.layout.activity_main, R.id.toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.constraintLayout), this::onApplyWindowInsetsListener);
        eventList = findViewById(R.id.eventList);
        StorageManager.setDirectory(getFilesDir().toPath());
        StorageManager.loadItems(Event::create);
        eventList.setLayoutManager(new LinearLayoutManager(this));
        eventList.setAdapter(new ItemListAdapter(StorageManager.getItems(), R.layout.event_list_item, R.id.itemTitleTextView));
    }

    public void onAddButtonClick(View view) {
        String uuid = StorageManager.createItem(Event::create);
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
        view.getRootView().findViewById(R.id.toolbar).setPadding(0, systemBars.top, 0, 0);
        view.setPadding(0, 0, 0, systemBars.bottom);
        return insets;
    }

}
