package com.soogbad.soogbadtodo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import androidx.core.view.ViewCompat;

import com.soogbad.sharedmodule.ui.ItemListActivity;
import com.soogbad.sharedmodule.core.ItemsManager;
import com.soogbad.sharedmodule.core.Utility;

import java.util.Comparator;

public class TodoListListActivity extends ItemListActivity {

    private ItemsManager<TodoList, TodoList.Options> itemsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.setWindowProperties(this, R.layout.todo_list_list_activity, R.id.toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), this::onApplyWindowInsetsListener);
        itemsManager = ((SoogbadTodoApplication)getApplication()).getItemsManager();
        if(!Settings.canDrawOverlays(this))
            startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())));
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();
        itemsManager.getItems().sort(Comparator.comparing((TodoList item) -> item.Options.Day).thenComparingInt(item -> item.Options.Hour).thenComparingInt(item -> item.Options.Minute));
        if(itemList.getAdapter() != null)
            itemList.getAdapter().notifyDataSetChanged();
    }

    public void onAddButtonClick(View view) {
        Utility.getAppUtility(this).launchCreateItemOptionsDialog(this, options -> {
            String uuid = itemsManager.createItem((TodoList.Options)options);
            createItem(uuid);
            return uuid;
        });
    }

    @Override protected View getToolbar() { return findViewById(R.id.toolbar); }

}
