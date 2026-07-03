package com.soogbad.soogbadtodo;

import android.os.Bundle;
import android.view.View;

import androidx.core.view.ViewCompat;

import com.soogbad.sharedmodule.ui.ItemListActivity;
import com.soogbad.sharedmodule.core.ItemsManager;
import com.soogbad.sharedmodule.core.Utility;

public class TodoListListActivity extends ItemListActivity {

    private ItemsManager<TodoList, TodoList.TodoListOptions> typedItemsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.setWindowProperties(this, R.layout.todo_list_list_activity, R.id.toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), this::onApplyWindowInsetsListener);
        typedItemsManager = ((SoogbadTodoApplication)getApplication()).getItemsManager();
        typedItemsManager.getItems().sort((a, b) -> Long.compare(b.Options.Time.getTime(), a.Options.Time.getTime()));
    }

    public void onAddButtonClick(View view) {
        TodoList.TodoListOptions options = (TodoList.TodoListOptions)Utility.getAppUtility(this).launchCreateItemOptionsDialog();
        String uuid = typedItemsManager.createItem(options);
        super.onAddButtonClick(uuid);
    }

    @Override protected View getToolbar() { return findViewById(R.id.toolbar); }

}
