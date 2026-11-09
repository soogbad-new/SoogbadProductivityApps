package com.soogbad.soogbadcalendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.soogbad.sharedmodule.core.Item;
import com.soogbad.sharedmodule.core.Utility;
import com.soogbad.sharedmodule.ui.ItemListAdapter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class EventListAdapter extends ItemListAdapter {

    private final LocalDate day;
    private final Runnable reloadDayList;

    public EventListAdapter(ArrayList<Event> events, LocalDate day, Runnable reloadDayList) {
        super(events, R.layout.day_event_list_item, R.id.eventTitleTextView);
        this.day = day; this.reloadDayList = reloadDayList;
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
    protected void deleteItem(ViewHolder itemHolder, Item<?> item) {
        Event event = (Event)item;
        if(event.Options.RepeatSchedule == Event.Schedule.NONE) {
            super.deleteItem(itemHolder, item);
            return;
        }
        else {
            Context context = itemHolder.itemView.getContext();
            new MaterialAlertDialogBuilder(context, com.soogbad.sharedmodule.R.style.AlertDialogTheme).setTitle("Delete Repeating Event").setItems(new CharSequence[] { "This event", "All events" }, (dialog, which) -> {
                if(which == 0) {
                    event.Options.DeletedOccurrences.add(day);
                    Utility.getItemsManager(context).saveItemOptions(event.UUID, event.Options, Utility.getAppUtility(context)::onItemOptionsChanged);
                    onItemDeleted(itemHolder, item);
                }
                else
                    super.deleteItem(itemHolder, item);
            }).show();
        }
    }
    @Override
    protected void onItemDeleted(ViewHolder itemHolder, Item<?> item) {
        reloadDayList.run();
        super.onItemDeleted(itemHolder, item);
    }

}
