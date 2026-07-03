package com.soogbad.soogbadtodo;

import com.soogbad.sharedmodule.ui.ItemActivity;
import com.soogbad.sharedmodule.core.ItemApplication;
import com.soogbad.sharedmodule.core.ItemsManager;
import com.soogbad.sharedmodule.core.StorageManager;

public class SoogbadTodoApplication extends ItemApplication<TodoList, TodoList.TodoListOptions> {

    @Override
    public void onCreate() {
        super.onCreate();
        itemsManager = new ItemsManager<>(new StorageManager(getFilesDir().toPath()), TodoList::create, TodoList.TodoListOptions::fromJson);
        itemsManager.loadItems();
        itemsManager.getItems().sort((a, b) -> Long.compare(b.Options.Time.getTime(), a.Options.Time.getTime()));
    }

    @Override
    public AppUtility getAppUtility() {
        return new AppUtility() {
            @Override public String getAppName() { return "SoogbadTodo"; }
            @Override public String getItemName() { return "TodoList"; }
            @Override public Class<? extends ItemActivity> getItemActivityClass() { return TodoListActivity.class; }
            @Override public boolean hasConfigurableOptions() { return true; }
            @Override public void launchEditItemOptionsDialog() {

            }
            @Override public TodoList.TodoListOptions launchCreateItemOptionsDialog() {
                return null;
            }
        };
    }

}
