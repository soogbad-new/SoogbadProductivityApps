package com.soogbad.sharedmodule.core;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.soogbad.sharedmodule.ui.ItemActivity;
import com.soogbad.sharedmodule.scheduling.ItemScheduler;

import java.util.function.Consumer;

public abstract class ItemApplication<T extends Item<O>, O extends Item.Options> extends Application {

    protected ItemsManager<T, O> itemsManager;
    public ItemsManager<T, O> getItemsManager() { return itemsManager; }

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override public void onActivityPreCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                if(!itemsManager.isListLoaded()) {
                    itemsManager.loadItems();
                    onFirstActivityOpened();
                }
            }
            @Override public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) { }
            @Override public void onActivityStarted(@NonNull Activity activity) { }
            @Override public void onActivityResumed(@NonNull Activity activity) { }
            @Override public void onActivityPaused(@NonNull Activity activity) { }
            @Override public void onActivityStopped(@NonNull Activity activity) { }
            @Override public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) { }
            @Override public void onActivityDestroyed(@NonNull Activity activity) { }
        });
    }

    protected abstract void onFirstActivityOpened();

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
