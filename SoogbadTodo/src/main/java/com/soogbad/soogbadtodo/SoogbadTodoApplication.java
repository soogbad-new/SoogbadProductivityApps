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

import java.util.function.Consumer;

public class SoogbadTodoApplication extends ItemApplication<TodoList, TodoList.Options> {

    private ItemScheduler itemScheduler;

    @Override
    public void onCreate() {
        super.onCreate();
        itemsManager = new ItemsManager<>(new StorageManager(getFilesDir().toPath()), TodoList::create, TodoList::parseOptionsFromJson);
        itemScheduler = new ItemScheduler(this, TodoAlarmReceiver.class);
        itemsManager.setItemScheduler(itemScheduler);
    }

    @Override
    protected void onFirstActivityOpened() {
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
            @Override public String getItemName() { return "Todo"; }
            @Override public Class<? extends ItemActivity> getItemActivityClass() { return TodoListActivity.class; }
            @Override public boolean hasConfigurableOptions() { return true; }
            @Override public void createItemOptionsDialog(Context context, Item.Options initialOptions, Consumer<Item.Options> callback) {
                new TodoListOptionsDialog(context, (TodoList.Options)initialOptions, callback::accept).show();
            }
            @Override public void onItemOptionsChanged(Item<?> item) {
                TodoList todoList = (TodoList)item;
                if(todoList.Options.ResetRequested) {
                    todoList.Options.ResetRequested = false;
                    itemsManager.saveItemContent(todoList.UUID, todoList.Options.DefaultContent);
                }
                getItemScheduler().scheduleItem(todoList);
            }
            @Override public ItemScheduler getItemScheduler() { return itemScheduler; }
        };
    }

}
