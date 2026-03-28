package com.soogbad.soogbadnotes;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.soogbad.sharedmodule.ItemLayout;
import com.soogbad.sharedmodule.ItemsManager;
import com.soogbad.sharedmodule.Utility;

public class NoteActivity extends AppCompatActivity {

    private ItemsManager<Note, Note.NoteOptions> notesManager;
    private Note note;

    private ItemLayout noteLayout;
    private ConstraintLayout noteToolbar;
    private EditText titleEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.setWindowProperties(this, R.layout.activity_note, R.id.toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.constraintLayout), this::onApplyWindowInsetsListener);
        noteLayout = findViewById(R.id.noteLayout); noteToolbar = findViewById(R.id.noteToolbar); titleEditText = findViewById(R.id.titleEditText);
        notesManager = ((SoogbadNotesApplication)getApplication()).getNotesManager();
        note = notesManager.getItem(getIntent().getStringExtra("item_uuid"));
        titleEditText.setText(note.Title);
        noteLayout.init(findViewById(R.id.noteEditText), notesManager, note);
    }

    public void onBoldButtonClick(View view) { noteLayout.onBoldButtonClick(); }
    public void onItalicButtonClick(View view) { noteLayout.onItalicButtonClick(); }

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
        view.getRootView().findViewById(R.id.toolbar).setPadding(0, systemBars.top, 0, 0);
        view.setPadding(0, 0, 0, systemBars.bottom);
        Insets keyboard = insets.getInsets(WindowInsetsCompat.Type.ime());
        noteToolbar.setPadding(0, 0, 0, keyboard.bottom - systemBars.bottom);
        return insets;
    }

}
