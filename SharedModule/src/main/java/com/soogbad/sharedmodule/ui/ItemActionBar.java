package com.soogbad.sharedmodule.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.soogbad.sharedmodule.core.Item;
import com.soogbad.sharedmodule.core.ItemsManager;
import com.soogbad.sharedmodule.R;
import com.soogbad.sharedmodule.core.Utility;

import java.util.Map;

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
        overflowMenuButton.setOnClickListener(readOnly ? this::showRecycleBinOverflowMenu : this::showOverflowMenu);
    }

    public void init(ItemLayout itemLayout, Item<?> item, boolean readOnly) {
        this.itemLayout = itemLayout; this.item = item; this.readOnly = readOnly;
        titleEditText.setText(item.Title);
        if(readOnly) {
            titleEditText.setFocusable(false); titleEditText.setFocusableInTouchMode(false); titleEditText.setCursorVisible(false);
        }
    }

    private void showOverflowMenu(View view) {
        new ItemMenuHandler(getContext(), Map.ofEntries(
            Map.entry(R.id.action_delete, () -> {
                itemLayout.delete();
                Utility.getActivity(getContext()).finish();
            }),
            Map.entry(R.id.action_copy_uuid, () ->
                Utility.getAppUtility(getContext()).copyItemUuid(getContext(), item)
            )
        )).showOverflowMenu(view, R.menu.item_context_menu);
    }

    private void showRecycleBinOverflowMenu(View view) {
        Context context = getContext();
        ItemsManager<?, ?> itemsManager = Utility.getItemsManager(context);
        new ItemMenuHandler(context, Map.ofEntries(
            Map.entry(R.id.action_restore, () -> {
                itemsManager.restoreRecycleBinItem(item.UUID);
                Toast.makeText(context, "Item restored", Toast.LENGTH_SHORT).show();
                readOnly = false;
                titleEditText.setFocusable(true); titleEditText.setFocusableInTouchMode(true); titleEditText.setCursorVisible(true);
                itemLayout.disableReadOnly();
            }),
            Map.entry(R.id.action_delete_permanently, () ->
                new AlertDialog.Builder(context).setTitle("Delete Permanently").setMessage("Are you sure you want to permanently delete this item?")
                        .setPositiveButton("Delete", (dialog, which) -> { itemsManager.permanentlyDeleteRecycleBinItem(item.UUID); Utility.getActivity(context).finish(); }).setNegativeButton("Cancel", null).show())
        )).showOverflowMenu(view, R.menu.recycle_bin_item_context_menu);
    }

}
