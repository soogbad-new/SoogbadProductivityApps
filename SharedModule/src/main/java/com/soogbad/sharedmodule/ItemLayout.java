package com.soogbad.sharedmodule;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannedString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.EditText;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

public class ItemLayout extends ConstraintLayout implements RichEditText.StyleStateListener {

    private ItemsManager<?, ?> itemsManager;
    private Item<?> item;
    private RichEditText contentEditText;
    private EditText titleEditText;
    private boolean itemDeleted = false;
    private Button boldButton, italicButton, underlineButton;

    public ItemLayout(Context context, AttributeSet attrs) { super(context, attrs); }

    public void init(ItemsManager<?, ?> itemsManager, Item<?> item, RichEditText contentEditText, EditText titleEditText, Button boldButton, Button italicButton, Button underlineButton) {
        this.itemsManager = itemsManager; this.item = item; this.contentEditText = contentEditText; this.titleEditText = titleEditText; this.boldButton = boldButton; this.italicButton = italicButton; this.underlineButton = underlineButton;
        contentEditText.setStyleStateListener(this);
        titleEditText.setText(item.Title);
        itemsManager.loadItemContent(item);
        contentEditText.setIgnoreTextChanges(true); contentEditText.setText(item.Content); contentEditText.setIgnoreTextChanges(false);
    }

    public void save() {
        if(itemDeleted)
            return;
        itemsManager.saveItemTitle(item, titleEditText.getText().toString());
        itemsManager.saveItemContent(item, new SpannedString(contentEditText.getText()));
    }

    public void delete() {
        itemDeleted = true;
        itemsManager.deleteItem(item);
    }

    public void onBoldButtonClick() { contentEditText.toggleStyle(StyleSpan.class, Typeface.BOLD); }
    public void onItalicButtonClick() { contentEditText.toggleStyle(StyleSpan.class, Typeface.ITALIC); }
    public void onUnderlineButtonClick() { contentEditText.toggleStyle(UnderlineSpan.class, 0); }

    @Override
    public void onStyleStateChanged(boolean bold, boolean italic, boolean underline) {
        toggleButton(boldButton, bold); toggleButton(italicButton, italic); toggleButton(underlineButton, underline);
    }

    private void toggleButton(Button button, boolean state) {
        if(state)
            button.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.lightGreen));
        else
            button.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.white));
    }

}
