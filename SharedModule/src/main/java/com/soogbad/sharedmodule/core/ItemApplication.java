package com.soogbad.sharedmodule.core;

import android.app.Application;
import android.content.Context;

import com.soogbad.sharedmodule.ui.ItemActivity;
import com.soogbad.sharedmodule.scheduling.ItemScheduler;

import java.util.function.Consumer;

public abstract class ItemApplication<T extends Item<O>, O extends Item.Options> extends Application {

    protected ItemsManager<T, O> itemsManager;
    public ItemsManager<T, O> getItemsManager() { return itemsManager; }

    public abstract AppUtility getAppUtility();

    public interface AppUtility {
        String getAppName();
        String getItemName();
        Class<? extends ItemActivity> getItemActivityClass();
        @SuppressWarnings("BooleanMethodIsAlwaysInverted") boolean hasConfigurableOptions();
        void createItemOptionsDialog(Context context, Item.Options initialOptions, Consumer<Item.Options> callback);
        void onItemOptionsChanged(Item<?> item);
        ItemScheduler getItemScheduler();
    }

}
