package com.soogbad.sharedmodule;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannedString;
import android.text.style.StyleSpan;
import android.util.AttributeSet;

import androidx.constraintlayout.widget.ConstraintLayout;

public class ItemLayout extends ConstraintLayout {

    private RichEditText editText;
    private ItemsManager<?, ?> itemsManager;
    private Item<?> item;
    private boolean itemDeleted = false;

    public ItemLayout(Context context, AttributeSet attrs) { super(context, attrs); }

    public void init(RichEditText editText, ItemsManager<?, ?> itemsManager,Item<?> item) {
        this.editText = editText; this.itemsManager = itemsManager; this.item = item;
        itemsManager.loadItemContent(item);
        editText.setText(item.Content);
    }

    public void save() {
        if(itemDeleted)
            return;
        itemsManager.saveItemContent(item, new SpannedString(editText.getText()));
    }

    public void delete() {
        itemDeleted = true;
        itemsManager.deleteItem(item);
    }

    public void onBoldButtonClick() { editText.toggleStyle(StyleSpan.class, Typeface.BOLD); }
    public void onItalicButtonClick() { editText.toggleStyle(StyleSpan.class, Typeface.ITALIC); }

}
