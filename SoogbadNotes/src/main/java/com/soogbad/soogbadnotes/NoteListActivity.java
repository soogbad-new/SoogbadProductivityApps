package com.soogbad.soogbadnotes;

import android.os.Bundle;
import android.view.View;

import androidx.core.view.ViewCompat;

import com.soogbad.sharedmodule.ui.ItemListActivity;
import com.soogbad.sharedmodule.core.ItemsManager;
import com.soogbad.sharedmodule.core.Utility;

public class NoteListActivity extends ItemListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.setWindowProperties(this, R.layout.activity_note_list, R.id.toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), this::onApplyWindowInsetsListener);
    }

    public void onAddButtonClick(View view) {
        ItemsManager<Note, Note.NoteOptions> itemsManager = ((SoogbadNotesApplication)getApplication()).getItemsManager();
        String uuid = itemsManager.createItem(new Note.NoteOptions(System.currentTimeMillis()));
        super.onAddButtonClick(uuid);
    }

    @Override protected View getToolbar() { return findViewById(R.id.toolbar); }

}
