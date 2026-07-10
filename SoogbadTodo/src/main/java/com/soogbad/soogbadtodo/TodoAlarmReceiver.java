package com.soogbad.soogbadtodo;

import android.content.Context;
import android.content.Intent;

import com.soogbad.sharedmodule.scheduling.ItemAlarmReceiver;
import com.soogbad.sharedmodule.core.ItemsManager;

public class TodoAlarmReceiver extends ItemAlarmReceiver<TodoList> {

    @Override
    protected void onAlarm(Context context, TodoList todoList) {
        ItemsManager<TodoList, TodoList.Options> itemsManager = ((SoogbadTodoApplication)context.getApplicationContext()).getItemsManager();
        if(todoList.Options.SkipNextRun) {
            todoList.Options.SkipNextRun = false;
            itemsManager.saveItemMetadata(todoList.UUID, todoList.Title, todoList.Options);
        }
        else {
            itemsManager.saveItemContent(todoList.UUID, todoList.Options.DefaultText);
            context.startActivity(new Intent(context, TodoListActivity.class).putExtra("item_uuid", todoList.UUID).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }        
    }

    @Override protected TodoList getItem(Context context, String uuid) { return ((SoogbadTodoApplication)context.getApplicationContext()).getItemsManager().getItem(uuid); }

}
