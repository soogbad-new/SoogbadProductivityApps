package com.soogbad.soogbadnotes;

import android.content.Context;
import android.util.AttributeSet;

import com.soogbad.sharedmodule.ItemActionBar;

public class NoteActionBar extends ItemActionBar {

    public NoteActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected String getItemUuidPrefix() { return "NOTE-"; }

}
