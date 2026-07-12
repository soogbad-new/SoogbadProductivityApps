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
            itemsManager.saveItemOptions(todoList.UUID, todoList.Options, null);
        }
        else {
            itemsManager.saveItemContent(todoList.UUID, todoList.Options.DefaultContent);
            context.startActivity(new Intent(context, TodoListActivity.class).putExtra("item_uuid", todoList.UUID).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }        
    }

    @Override protected TodoList getItem(Context context, String uuid) { return ((SoogbadTodoApplication)context.getApplicationContext()).getItemsManager().getItem(uuid); }

}
