package com.soogbad.sharedmodule;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.constraintlayout.widget.ConstraintLayout;

public abstract class ItemActionBar extends ConstraintLayout {


    private final EditText titleEditText;
    public EditText getTitleEditText() { return titleEditText; }
    protected final ImageButton overflowMenuButton;

    private ItemLayout itemLayout;
    private Item<?> item;

    public ItemActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.item_action_bar_content, this, true);
        titleEditText = findViewById(R.id.titleEditText); overflowMenuButton = findViewById(R.id.overflowMenuButton);
        overflowMenuButton.setOnClickListener(this::showOverflowMenu);
    }

    public void init(ItemLayout itemLayout, Item<?> item) {
        this.itemLayout = itemLayout; this.item = item;
        titleEditText.setText(item.Title);
    }

    private void showOverflowMenu(View view) {
        PopupMenu popup = new PopupMenu(getContext(), view);
        popup.getMenuInflater().inflate(R.menu.item_overflow_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(this::onOverflowMenuItemClick);
        popup.show();
    }

    private boolean onOverflowMenuItemClick(MenuItem menuItem) {
        if(menuItem.getItemId() == R.id.action_delete) {
            itemLayout.delete();
            ((Activity)getContext()).finish();
            return true;
        }
        else if(menuItem.getItemId() == R.id.action_copy_uuid) {
            Utility.getAppUtility(getContext()).copyItemUuid(getContext(), item);
            return true;
        }
        return false;
    }

}
