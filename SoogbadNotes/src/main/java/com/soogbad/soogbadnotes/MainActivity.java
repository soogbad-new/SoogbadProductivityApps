package com.soogbad.soogbadnotes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.soogbad.sharedmodule.ItemListAdapter;
import com.soogbad.sharedmodule.ItemsManager;
import com.soogbad.sharedmodule.Utility;

public class MainActivity extends AppCompatActivity {

    private RecyclerView noteList;
    private ItemsManager<Note, Note.NoteOptions> notesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.setWindowProperties(this, R.layout.activity_main, R.id.toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.constraintLayout), this::onApplyWindowInsetsListener);
        noteList = findViewById(R.id.noteList);
        notesManager = ((SoogbadNotesApplication)getApplication()).getNotesManager();
        noteList.setLayoutManager(new LinearLayoutManager(this));
        noteList.setAdapter(new ItemListAdapter(notesManager.getItems(), R.layout.note_list_item, R.id.itemTitleTextView));
    }

    public void onAddButtonClick(View view) {
        String uuid = notesManager.createItem(new Note.NoteOptions(System.currentTimeMillis()));
        if(noteList.getAdapter() != null)
            noteList.getAdapter().notifyItemInserted(0);
        startActivity(new Intent(this, NoteActivity.class).putExtra("item_uuid", uuid));
    }

    public void onNoteListItemClick(View view) {
        if(view.getTag() != null)
            startActivity(new Intent(this, NoteActivity.class).putExtra("item_uuid", view.getTag().toString()));
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();
        notesManager.getItems().sort((a, b) -> Long.compare(b.Options.LastViewed, a.Options.LastViewed));
        if(noteList != null && noteList.getAdapter() != null)
            noteList.getAdapter().notifyDataSetChanged();
    }

    public WindowInsetsCompat onApplyWindowInsetsListener(View view, WindowInsetsCompat insets) {
        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        view.getRootView().findViewById(R.id.toolbar).setPadding(0, systemBars.top, 0, 0);
        view.setPadding(0, 0, 0, systemBars.bottom);
        return insets;
    }

}
