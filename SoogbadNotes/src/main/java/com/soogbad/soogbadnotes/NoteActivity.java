package com.soogbad.soogbadnotes;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.soogbad.sharedmodule.ItemLayout;
import com.soogbad.sharedmodule.ItemsManager;
import com.soogbad.sharedmodule.Utility;

public class NoteActivity extends AppCompatActivity {

    private ItemLayout noteLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.setWindowProperties(this, R.layout.activity_note, R.id.actionBar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.constraintLayout), this::onApplyWindowInsetsListener);
        noteLayout = findViewById(R.id.noteLayout);
        ItemsManager<Note, Note.NoteOptions> notesManager = ((SoogbadNotesApplication)getApplication()).getNotesManager();
        Note note = notesManager.getItem(getIntent().getStringExtra("item_uuid"));
        note.Options.LastViewed = System.currentTimeMillis();
        notesManager.saveItemOptions(note);
        noteLayout.init(notesManager, note, findViewById(R.id.titleEditText));
    }

    public void onDeleteButtonClick(View view) {
        noteLayout.delete();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        noteLayout.save();
    }

    public WindowInsetsCompat onApplyWindowInsetsListener(View view, WindowInsetsCompat insets) {
        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        view.getRootView().findViewById(R.id.actionBar).setPadding(0, systemBars.top, 0, 0);
        view.setPadding(0, 0, 0, systemBars.bottom);
        Insets keyboard = insets.getInsets(WindowInsetsCompat.Type.ime());
        noteLayout.getFormattingToolbar().setPadding(0, 0, 0, keyboard.bottom - systemBars.bottom);
        return insets;
    }

}
