package com.soogbad.soogbadcalendar;

import android.app.KeyguardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AlarmActivity extends AppCompatActivity {

    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        event = ((SoogbadCalendarApplication)getApplication()).getItemsManager().getItem(getIntent().getStringExtra("item_uuid"));
        if(event == null) { finish(); return; }
        setContentView(R.layout.alarm_activity);
        ((TextView)findViewById(R.id.alarmTitle)).setText(event.Title);
        ((TextView)findViewById(R.id.alarmTime)).setText(event.Options.Time.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
    }

    public void onDismissButtonClick(View view) {
        if(((SoogbadCalendarApplication)getApplication()).getItemsManager().getItem(event.UUID) != null)
            EventAlarmReceiver.showNotification(this, event, Event.Importance.SILENT); // create new silent notification instead of the ringing alarm notification
        finish();
    }

    public void onOpenButtonClick(View view) {
        NotificationManagerCompat.from(this).cancel(event.UUID.hashCode());
        getSystemService(KeyguardManager.class).requestDismissKeyguard(this, new KeyguardManager.KeyguardDismissCallback() {
            @Override public void onDismissSucceeded() { // when the phone is unlocked
                TaskStackBuilder.create(AlarmActivity.this).addNextIntentWithParentStack(new Intent(AlarmActivity.this, DayListActivity.class).putExtra("date", LocalDate.now().toString()))
                        .addNextIntent(new Intent(AlarmActivity.this, EventActivity.class).putExtra("item_uuid", event.UUID)).startActivities();
                finish();
            }
        });
    }

}
