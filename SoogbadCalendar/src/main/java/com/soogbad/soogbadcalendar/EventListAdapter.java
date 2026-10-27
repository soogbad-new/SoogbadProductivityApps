package com.soogbad.soogbadcalendar;

import android.annotation.SuppressLint;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.soogbad.sharedmodule.core.Item;
import com.soogbad.sharedmodule.ui.ItemListAdapter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class EventListAdapter extends ItemListAdapter {

    private final Runnable reloadDayList;

    public EventListAdapter(ArrayList<Event> events, Runnable reloadDayList) {
        super(events, R.layout.day_event_list_item, R.id.eventTitleTextView);
        this.reloadDayList = reloadDayList;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ((TextView)holder.itemView.findViewById(R.id.eventTimeTextView)).setText(((Event)items.get(position)).Options.Time.format(DateTimeFormatter.ofPattern("HH:mm")));
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onItemOptionsSaved() {
        reloadDayList.run();
        notifyDataSetChanged();
    }

    @Override
    protected void onItemDeleted(ViewHolder itemHolder, Item<?> item) {
        reloadDayList.run();
        super.onItemDeleted(itemHolder, item);
    }

}
