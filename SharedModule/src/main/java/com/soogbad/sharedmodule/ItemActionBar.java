package com.soogbad.sharedmodule;

import android.app.AlertDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

public class ItemActionBar extends ConstraintLayout {

    private final EditText titleEditText;
    public EditText getTitleEditText() { return titleEditText; }
    protected final ImageButton overflowMenuButton;

    private ItemLayout itemLayout;
    private Item<?> item;
    private boolean readOnly;

    public ItemActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.item_action_bar_content, this, true);
        titleEditText = findViewById(R.id.titleEditText); overflowMenuButton = findViewById(R.id.overflowMenuButton);
        overflowMenuButton.setOnClickListener(this::showOverflowMenu);
    }

    public void init(ItemLayout itemLayout, Item<?> item, boolean readOnly) {
        this.itemLayout = itemLayout; this.item = item; this.readOnly = readOnly;
        titleEditText.setText(item.Title);
        if(readOnly) {
            titleEditText.setFocusable(false); titleEditText.setFocusableInTouchMode(false); titleEditText.setCursorVisible(false);
        }
    }

    private void showOverflowMenu(View view) {
        PopupMenu popup = new PopupMenu(getContext(), view);
        popup.getMenuInflater().inflate(readOnly ? R.menu.recycle_bin_item_context_menu : R.menu.item_context_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(readOnly ? this::onContextMenuItemClick : this::onOverflowMenuItemClick);
        popup.show();
    }

    private boolean onOverflowMenuItemClick(MenuItem menuItem) {
        if(menuItem.getItemId() == R.id.action_delete) {
            itemLayout.delete();
            Utility.getActivity(getContext()).finish();
            return true;
        }
        else if(menuItem.getItemId() == R.id.action_copy_uuid) {
            Utility.getAppUtility(getContext()).copyItemUuid(getContext(), item);
            return true;
        }
        return false;
    }

    private boolean onContextMenuItemClick(MenuItem menuItem) {
        Context context = getContext();
        ItemsManager<?, ?> itemsManager = ((ItemApplication<?, ?>)context.getApplicationContext()).getItemsManager();
        if(menuItem.getItemId() == R.id.action_restore) {
            itemsManager.restoreRecycleBinItem(item.UUID);
            Toast.makeText(context, "Item restored", Toast.LENGTH_SHORT).show();
            readOnly = false;
            titleEditText.setFocusable(true); titleEditText.setFocusableInTouchMode(true); titleEditText.setCursorVisible(true);
            itemLayout.disableReadOnly();
            return true;
        }
        else if(menuItem.getItemId() == R.id.action_delete_permanently) {
            new AlertDialog.Builder(context).setTitle("Delete Permanently").setMessage("Are you sure you want to permanently delete this item?")
                    .setPositiveButton("Delete", (dialog, which) -> { itemsManager.permanentlyDeleteRecycleBinItem(item.UUID); Utility.getActivity(context).finish(); }).setNegativeButton("Cancel", null).show();
            return true;
        }
        return false;
    }

}
