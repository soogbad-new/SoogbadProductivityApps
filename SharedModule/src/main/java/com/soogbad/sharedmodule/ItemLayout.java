package com.soogbad.sharedmodule;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannedString;
import android.text.style.StyleSpan;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class ItemLayout extends ConstraintLayout {

    private RichEditText editText;
    private Item<?> item;

    private boolean itemDeleted = false;

    public ItemLayout(Context context, AttributeSet attrs) { super(context, attrs); }

    public void init(RichEditText editText, @Nullable Item<?> item) {
        this.editText = editText; this.item = item;
        if(item == null)
            return;
        item.loadContent();
        editText.setText(item.Content);
    }

    public void saveItem() {
        if(itemDeleted)
            return;
        item.Content = new SpannedString(editText.getText());
        item.save();
    }

    public void deleteItem() {
        itemDeleted = true;
        item.delete();
    }

    public void onBoldButtonClick() { editText.toggleStyle(StyleSpan.class, Typeface.BOLD); }
    public void onItalicButtonClick() { editText.toggleStyle(StyleSpan.class, Typeface.ITALIC); }

}
