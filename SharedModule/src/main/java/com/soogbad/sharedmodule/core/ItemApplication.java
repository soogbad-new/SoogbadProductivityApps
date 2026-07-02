package com.soogbad.sharedmodule.core;

import android.app.Application;

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

    }

}
