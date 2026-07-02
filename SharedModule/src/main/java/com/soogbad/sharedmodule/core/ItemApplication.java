package com.soogbad.sharedmodule.core;

import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

import com.soogbad.sharedmodule.ui.ItemActivity;

public abstract class ItemApplication<T extends Item<O>, O extends Item.ItemOptions> extends Application {

    protected ItemsManager<T, O> itemsManager;
    public ItemsManager<T, O> getItemsManager() { return itemsManager; }

    public abstract AppUtility getAppUtility();

    public interface AppUtility {

        String getAppName();

        String getItemName();

        Class<? extends ItemActivity> getItemActivityClass();

        boolean hasConfigurableOptions();
        
        default void copyItemUuid(Context context, Item<?> item) {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(ClipData.newPlainText("UUID", getItemName().toUpperCase() + "-" + item.UUID));
            Toast.makeText(context, "Item UUID copied to clipboard", Toast.LENGTH_SHORT).show();
        }

    }

}
