package com.soogbad.soogbadnotes;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.core.view.ViewCompat;

import com.soogbad.sharedmodule.ui.ItemListActivity;
import com.soogbad.sharedmodule.core.ItemsManager;
import com.soogbad.sharedmodule.core.Utility;

public class NoteListActivity extends ItemListActivity {

    private ItemsManager<Note, Note.Options> itemsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.setWindowProperties(this, R.layout.note_list_activity, R.id.toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), this::onApplyWindowInsetsListener);
        itemsManager = ((SoogbadNotesApplication)getApplication()).getItemsManager();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();
        itemsManager.getItems().sort((a, b) -> Long.compare(b.Options.LastViewed, a.Options.LastViewed));
        if(itemList.getAdapter() != null)
            itemList.getAdapter().notifyDataSetChanged();
    }

    public void onAddButtonClick(View view) {
        Note note = itemsManager.createItem(Note.getDefaultOptions(), Utility.getAppUtility(this)::onItemOptionsChanged);
        if(itemList.getAdapter() != null)
            itemList.getAdapter().notifyItemInserted(0);
        launchItem(note.UUID);
    }

    @Override protected View getToolbar() { return findViewById(R.id.toolbar); }

}
