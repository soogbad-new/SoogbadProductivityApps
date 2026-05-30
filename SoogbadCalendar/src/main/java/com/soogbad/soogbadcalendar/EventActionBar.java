package com.soogbad.soogbadcalendar;

import android.content.Context;
import android.util.AttributeSet;

import com.soogbad.sharedmodule.Item;
import com.soogbad.sharedmodule.ItemActionBar;
import com.soogbad.sharedmodule.ItemLayout;

public class EventActionBar extends ItemActionBar {

    public EventActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void init(ItemLayout itemLayout, Item<?> item, boolean readOnly) {
        super.init(itemLayout, item, readOnly);
        //scheduleButton.setVisibility(View.VISIBLE);
    }

}
