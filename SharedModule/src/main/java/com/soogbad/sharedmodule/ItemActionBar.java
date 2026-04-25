package com.soogbad.sharedmodule;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class ItemActionBar extends LinearLayout {


    private final EditText titleEditText;
    public EditText getTitleEditText() { return titleEditText; }
    private final ImageButton deleteButton;

    private ItemLayout itemLayout;

    public ItemActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.item_action_bar_content, this, true);
        titleEditText = findViewById(R.id.titleEditText); deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(v -> onDeleteButtonClick());
    }

    public void init(ItemLayout itemLayout, Item<?> item) {
        this.itemLayout = itemLayout;
        titleEditText.setText(item.Title);
    }

    private void onDeleteButtonClick() {
        itemLayout.delete();
        ((Activity)getContext()).finish();
    }

}
