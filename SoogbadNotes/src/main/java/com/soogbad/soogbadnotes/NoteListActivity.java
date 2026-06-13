package com.soogbad.soogbadnotes;

import android.os.Bundle;
import android.view.View;

import androidx.core.view.ViewCompat;

import com.soogbad.sharedmodule.ItemListActivity;
import com.soogbad.sharedmodule.Utility;

public class NoteListActivity extends ItemListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.setWindowProperties(this, R.layout.activity_note_list, R.id.toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), this::onApplyWindowInsetsListener);
    }

    @Override protected View getToolbar() { return findViewById(R.id.toolbar); }

}
