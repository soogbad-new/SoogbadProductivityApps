package com.soogbad.sharedmodule.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.soogbad.sharedmodule.core.Item;
import com.soogbad.sharedmodule.core.ItemsManager;
import com.soogbad.sharedmodule.R;
import com.soogbad.sharedmodule.core.Utility;

public class ItemActionBar extends ConstraintLayout {

    private final EditText titleEditText;
    public EditText getTitleEditText() { return titleEditText; }

    private ItemLayout itemLayout;
    private Item<?> item;

    public ItemActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.item_action_bar_content, this, true);
        titleEditText = findViewById(R.id.titleEditText);
    }

    public void init(ItemLayout itemLayout, Item<?> item, boolean readOnly) {
        this.itemLayout = itemLayout; this.item = item;
        titleEditText.setText(item.Title);
        if(readOnly) {
            titleEditText.setFocusable(false); titleEditText.setFocusableInTouchMode(false); titleEditText.setCursorVisible(false);
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        Context context = getContext();
        ItemsManager<?, ?> itemsManager = Utility.getItemsManager(context);
        if(menuItem.getItemId() == R.id.action_edit_options) {
            Utility.getAppUtility(context).createItemOptionsDialog(context, item.Options, (options) -> {
                item.Options = options;
                itemsManager.saveItemOptions(item.UUID, item.Options, Utility.getAppUtility(context)::onItemOptionsChanged);
            });
            return true;
        }
        else if(menuItem.getItemId() == R.id.action_copy_uuid) {
            Utility.copyItemUuid(getContext(), item);
            return true;
        }
        else if(menuItem.getItemId() == R.id.action_delete) {
            itemLayout.delete();
            Utility.getActivity(getContext()).finish();
            return true;
        }
        else if(menuItem.getItemId() == R.id.action_restore) {
            itemsManager.restoreRecycleBinItem(item.UUID);
            Toast.makeText(context, "Item restored", Toast.LENGTH_SHORT).show();
            titleEditText.setFocusable(true); titleEditText.setFocusableInTouchMode(true); titleEditText.setCursorVisible(true);
            itemLayout.disableReadOnly(); ((ItemActivity)Utility.getActivity(context)).disablePreviewMode();
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
