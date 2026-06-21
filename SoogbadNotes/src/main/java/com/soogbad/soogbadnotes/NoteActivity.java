package com.soogbad.soogbadnotes;

import android.os.Bundle;
import android.view.View;

import androidx.core.view.ViewCompat;

import com.soogbad.sharedmodule.core.Item;
import com.soogbad.sharedmodule.ui.ItemActivity;
import com.soogbad.sharedmodule.core.Utility;

public class NoteActivity extends ItemActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.setWindowProperties(this, R.layout.note_activity, R.id.toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), this::onApplyWindowInsetsListener);
    }

    @Override
    protected void onItemLoaded(Item<?> item) {
        ((Note)item).Options.LastViewed = System.currentTimeMillis();
        itemLayout.markOptionsChanged();
        itemLayout.save();
    }

    @Override protected View getToolbar() { return findViewById(R.id.toolbar); }

}
