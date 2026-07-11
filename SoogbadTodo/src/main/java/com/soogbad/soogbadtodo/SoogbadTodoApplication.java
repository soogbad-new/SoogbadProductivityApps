package com.soogbad.soogbadtodo;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import com.soogbad.sharedmodule.core.Item;
import com.soogbad.sharedmodule.core.ItemApplication;
import com.soogbad.sharedmodule.scheduling.ItemScheduler;
import com.soogbad.sharedmodule.core.ItemsManager;
import com.soogbad.sharedmodule.core.StorageManager;
import com.soogbad.sharedmodule.ui.ItemActivity;

import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Function;

public class SoogbadTodoApplication extends ItemApplication<TodoList, TodoList.Options> {

    private ItemScheduler itemScheduler;

    @Override
    public void onCreate() {
        super.onCreate();
        itemsManager = new ItemsManager<>(new StorageManager(getFilesDir().toPath()), TodoList::create, TodoList::parseOptionsFromJson);
        itemsManager.loadItems();
        itemsManager.getItems().sort(Comparator.comparing((TodoList item) -> item.Options.Day).thenComparingInt(item -> item.Options.Hour).thenComparingInt(item -> item.Options.Minute));
        itemScheduler = new ItemScheduler(this, TodoAlarmReceiver.class);
        itemsManager.setItemScheduler(itemScheduler);
        if(!getSystemService(AlarmManager.class).canScheduleExactAlarms())
            startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:" + getPackageName())).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        if(!Settings.canDrawOverlays(this))
            startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        itemScheduler.scheduleAllItems();
    }

    @Override
    public AppUtility getAppUtility() {
        return new AppUtility() {
            @Override public String getAppName() { return "SoogbadTodo"; }
            @Override public String getItemName() { return "TodoList"; }
            @Override public Class<? extends ItemActivity> getItemActivityClass() { return TodoListActivity.class; }
            @Override public boolean hasConfigurableOptions() { return true; }
            @Override public void launchEditItemOptionsDialog(Context context, Item<?> item, Consumer<Item.Options> callback) { TodoListOptionsDialog.launchEditItemOptionsDialog(context, item, callback); }
            @Override public void launchCreateItemOptionsDialog(Context context, Function<Item.Options, String> callback) { TodoListOptionsDialog.launchCreateItemOptionsDialog(context, callback); }
            @Override public ItemScheduler getItemScheduler() { return itemScheduler; }
        };
    }

}
