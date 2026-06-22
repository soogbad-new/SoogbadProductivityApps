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
        Utility.setWindowProperties(this, R.layout.note_list_activity, R.id.toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), this::onApplyWindowInsetsListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((ItemsManager<Note, Note.NoteOptions>)itemsManager).getItems().sort((a, b) -> Long.compare(b.Options.LastViewed, a.Options.LastViewed));
        notifyDataSetChanged();
    }

    public void onAddButtonClick(View view) {
        String uuid = ((ItemsManager<Note, Note.NoteOptions>)itemsManager).createItem(new Note.NoteOptions(System.currentTimeMillis()));
        super.onAddButtonClick(uuid);
    }

    @Override protected View getToolbar() { return findViewById(R.id.toolbar); }

}
