package com.soogbad.sharedmodule;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannedString;
import android.text.style.StyleSpan;
import android.util.AttributeSet;

import androidx.constraintlayout.widget.ConstraintLayout;

public class ItemLayout extends ConstraintLayout {

    private RichEditText editText;
    private Item<?> item;

    private boolean itemDeleted = false;

    public ItemLayout(Context context, AttributeSet attrs) { super(context, attrs); }

    public void init(RichEditText editText, String itemUuid) {
        this.editText = editText;
        for(Item<?> item : ItemsManager.getInstance().getItems())
            if(item.UUID.equals(itemUuid))
                this.item = item;
        ItemsManager.getInstance().loadItemContent(item);
        editText.setText(item.Content);
    }

    public void saveItem() {
        if(itemDeleted)
            return;
        ItemsManager.getInstance().saveItemContent(item, new SpannedString(editText.getText()));
    }

    public void deleteItem() {
        itemDeleted = true;
        ItemsManager.getInstance().deleteItem(item);
    }

    public void onBoldButtonClick() { editText.toggleStyle(StyleSpan.class, Typeface.BOLD); }
    public void onItalicButtonClick() { editText.toggleStyle(StyleSpan.class, Typeface.ITALIC); }

}
