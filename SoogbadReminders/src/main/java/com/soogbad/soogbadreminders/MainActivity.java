package com.soogbad.soogbadreminders;

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

    private RecyclerView reminderList;
    private ItemsManager<Reminder, Reminder.ReminderOptions> remindersManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.setWindowProperties(this, R.layout.activity_main, R.id.toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.constraintLayout), this::onApplyWindowInsetsListener);
        reminderList = findViewById(R.id.reminderList);
        remindersManager = ((SoogbadRemindersApplication)getApplication()).getRemindersManager();
        reminderList.setLayoutManager(new LinearLayoutManager(this));
        reminderList.setAdapter(new ItemListAdapter(remindersManager.getItems(), R.layout.reminder_list_item, R.id.itemTitleTextView));
    }

    public void onAddButtonClick(View view) {
        String uuid = remindersManager.createItem(new Reminder.ReminderOptions(new Date(), Schedule.NONE));
        if(reminderList.getAdapter() != null)
            reminderList.getAdapter().notifyItemInserted(0);
        startActivity(new Intent(this, ReminderActivity.class).putExtra("item_uuid", uuid));
    }

    public void onReminderListItemClick(View view) {
        startActivity(new Intent(this, ReminderActivity.class).putExtra("item_uuid", view.getTag().toString()));
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();
        if(reminderList != null && reminderList.getAdapter() != null)
            reminderList.getAdapter().notifyDataSetChanged();
    }

    public WindowInsetsCompat onApplyWindowInsetsListener(View view, WindowInsetsCompat insets) {
        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        view.getRootView().findViewById(R.id.toolbar).setPadding(0, systemBars.top, 0, 0);
        view.setPadding(0, 0, 0, systemBars.bottom);
        return insets;
    }

}
