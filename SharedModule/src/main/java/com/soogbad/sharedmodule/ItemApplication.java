package com.soogbad.sharedmodule;

import android.app.Application;

public abstract class ItemApplication<T extends Item<O>, O extends Item.ItemOptions> extends Application {

    protected ItemsManager<T, O> itemsManager;
    public ItemsManager<T, O> getItemsManager() { return itemsManager; }

}
