package com.soogbad.sharedmodule;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannedString;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.widget.EditText;

import androidx.constraintlayout.widget.ConstraintLayout;

public class ItemLayout extends ConstraintLayout {

    private ItemsManager<?, ?> itemsManager;
    private Item<?> item;
    private RichEditText contentEditText;
    private EditText titleEditText;
    private boolean itemDeleted = false;

    public ItemLayout(Context context, AttributeSet attrs) { super(context, attrs); }

    public void init(ItemsManager<?, ?> itemsManager, Item<?> item, RichEditText contentEditText, EditText titleEditText) {
        this.itemsManager = itemsManager; this.item = item; this.contentEditText = contentEditText; this.titleEditText = titleEditText;
        titleEditText.setText(item.Title);
        itemsManager.loadItemContent(item);
        contentEditText.setText(item.Content);
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

}
