package com.soogbad.sharedmodule.core;

import android.app.Application;
import android.content.Context;

import com.soogbad.sharedmodule.ui.ItemActivity;

import java.util.function.Consumer;

public abstract class ItemApplication<T extends Item<O>, O extends Item.Options> extends Application {

    protected ItemsManager<T, O> itemsManager;
    public ItemsManager<T, O> getItemsManager() { return itemsManager; }

    public abstract AppUtility getAppUtility();

    public interface AppUtility {
        String getAppName();
        String getItemName();
        Class<? extends ItemActivity> getItemActivityClass();
        boolean hasConfigurableOptions();
        void launchEditItemOptionsDialog(Context context, Item<?> item, Consumer<Item.Options> callback);
        void launchCreateItemOptionsDialog(Context context, Consumer<Item.Options> callback);
    }

}
