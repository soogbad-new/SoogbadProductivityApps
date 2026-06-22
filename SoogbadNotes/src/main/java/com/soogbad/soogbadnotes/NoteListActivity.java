package com.soogbad.soogbadnotes;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.core.view.ViewCompat;

import com.soogbad.sharedmodule.ui.ItemListActivity;
import com.soogbad.sharedmodule.core.ItemsManager;
import com.soogbad.sharedmodule.core.Utility;

public class NoteListActivity extends ItemListActivity {

    private ItemsManager<Note, Note.NoteOptions> typedItemsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.setWindowProperties(this, R.layout.note_list_activity, R.id.toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), this::onApplyWindowInsetsListener);
        typedItemsManager = ((SoogbadNotesApplication)getApplication()).getItemsManager();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();
        typedItemsManager.getItems().sort((a, b) -> Long.compare(b.Options.LastViewed, a.Options.LastViewed));
        if(itemList.getAdapter() != null)
            itemList.getAdapter().notifyDataSetChanged();
    }

    public void onAddButtonClick(View view) {
        String uuid = typedItemsManager.createItem(new Note.NoteOptions(System.currentTimeMillis()));
        super.onAddButtonClick(uuid);
    }

    @Override protected View getToolbar() { return findViewById(R.id.toolbar); }

}
