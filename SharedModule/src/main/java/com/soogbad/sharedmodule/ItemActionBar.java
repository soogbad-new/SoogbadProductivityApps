package com.soogbad.sharedmodule;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

public class ItemActionBar extends LinearLayout {


    private final EditText titleEditText;
    public EditText getTitleEditText() { return titleEditText; }

    private ItemLayout itemLayout;
    private Item<?> item;

    public ItemActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.item_action_bar_content, this, true);
        titleEditText = findViewById(R.id.titleEditText); ImageButton overflowMenuButton = findViewById(R.id.overflowMenuButton);
        overflowMenuButton.setOnClickListener(this::showOverflowMenu);
    }

    public void init(ItemLayout itemLayout, Item<?> item) {
        this.itemLayout = itemLayout; this.item = item;
        titleEditText.setText(item.Title);
    }

    private void showOverflowMenu(View view) {
        PopupMenu popup = new PopupMenu(getContext(), view);
        popup.getMenuInflater().inflate(R.menu.action_bar_overflow_menu_item, popup.getMenu());
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
            ClipboardManager clipboard = (ClipboardManager)getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            String prefix = "";
            if(getContext().getPackageName().equals("com.soogbad.soogbadcalendar"))
                prefix = "EVENT-";
            else if(getContext().getPackageName().equals("com.soogbad.soogbadnotes"))
                prefix = "NOTE-";
            else if(getContext().getPackageName().equals("com.soogbad.soogbadreminders"))
                prefix = "REMINDER-";
            clipboard.setPrimaryClip(ClipData.newPlainText("UUID", prefix + item.UUID));
            Toast.makeText(getContext(), "Item UUID copied to clipboard", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

}
