package com.soogbad.soogbadtodo;

import android.content.Context;

import com.soogbad.sharedmodule.core.Item;
import com.soogbad.sharedmodule.core.ItemApplication;
import com.soogbad.sharedmodule.core.ItemsManager;
import com.soogbad.sharedmodule.core.StorageManager;
import com.soogbad.sharedmodule.ui.ItemActivity;

import java.util.Comparator;
import java.util.function.Consumer;

public class SoogbadTodoApplication extends ItemApplication<TodoList, TodoList.Options> {

    @Override
    public void onCreate() {
        super.onCreate();
        itemsManager = new ItemsManager<>(new StorageManager(getFilesDir().toPath()), TodoList::create, TodoList.Options::fromJson);
        itemsManager.loadItems();
        itemsManager.getItems().sort(Comparator.comparing((TodoList item) -> item.Options.Day).thenComparingInt(item -> item.Options.Hour).thenComparingInt(item -> item.Options.Minute));
    }

    @Override
    public AppUtility getAppUtility() {
        return new AppUtility() {
            @Override public String getAppName() { return "SoogbadTodo"; }
            @Override public String getItemName() { return "TodoList"; }
            @Override public Class<? extends ItemActivity> getItemActivityClass() { return TodoListActivity.class; }
            @Override public boolean hasConfigurableOptions() { return true; }
            @Override public void launchEditItemOptionsDialog(Context context, Item<?> item) { TodoListOptionsDialog.launchEditItemOptionsDialog(context, item); }
            @Override public void launchCreateItemOptionsDialog(Context context, Consumer<Item.Options> callback) { TodoListOptionsDialog.launchCreateItemOptionsDialog(context, callback); }

        };
    }

}
