package com.soogbad.soogbadnotes;

import android.os.Bundle;

import androidx.core.view.ViewCompat;

import com.soogbad.sharedmodule.Item;
import com.soogbad.sharedmodule.ItemActivity;
import com.soogbad.sharedmodule.Utility;

public class NoteActivity extends ItemActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.setWindowProperties(this, R.layout.activity_note, R.id.toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), this::onApplyWindowInsetsListener);
    }

    @Override
    protected void onItemLoaded(Item<?> item) {
        ((Note)item).Options.LastViewed = System.currentTimeMillis();
        itemsManager.saveItemMetadata(item);
    }

}
