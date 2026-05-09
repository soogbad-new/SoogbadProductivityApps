package com.soogbad.soogbadreminders;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.soogbad.sharedmodule.Item;
import com.soogbad.sharedmodule.ItemActionBar;
import com.soogbad.sharedmodule.ItemLayout;
import com.soogbad.sharedmodule.Schedule;

public class ReminderActionBar extends ItemActionBar {

    public ReminderActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void init(ItemLayout itemLayout, Item<?> item) {
        super.init(itemLayout, item);
        scheduleButton.setVisibility(View.VISIBLE);
    }

    @Override
    protected String getItemUuidPrefix() { return "REMINDER-"; }

}
