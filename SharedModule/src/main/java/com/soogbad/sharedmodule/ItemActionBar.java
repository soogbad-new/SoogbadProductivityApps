package com.soogbad.sharedmodule;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.LayoutInflater;
import android.view.MenuInflater;
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
        overflowMenuButton.setOnClickListener(v -> showOverflowMenu(v));
    }

    public void init(ItemLayout itemLayout, Item<?> item) {
        this.itemLayout = itemLayout; this.item = item;
        titleEditText.setText(item.Title);
    }

    private void showOverflowMenu(View view) {
        PopupMenu popup = new PopupMenu(getContext(), view);
        popup.getMenuInflater().inflate(R.menu.menu_item_action_bar, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.action_delete) {
                onDeleteButtonClick();
                return true;
            } else if(item.getItemId() == R.id.action_copy_uuid) {
                onCopyUuidButtonClick();
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void onDeleteButtonClick() {
        itemLayout.delete();
        ((Activity)getContext()).finish();
    }

    private void onCopyUuidButtonClick() {
        ClipboardManager clipboard = (ClipboardManager)getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText("UUID", item.UUID));
        Toast.makeText(getContext(), "Item UUID copied to clipboard", Toast.LENGTH_SHORT).show();
    }

}
