package com.soogbad.sharedmodule.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.soogbad.sharedmodule.core.ItemsManager;
import com.soogbad.sharedmodule.R;
import com.soogbad.sharedmodule.core.Utility;

public abstract class ItemListActivity extends AppCompatActivity {

    protected RecyclerView itemList;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        itemList = findViewById(R.id.itemList);
        ItemsManager<?, ?> itemsManager = Utility.getItemsManager(this);
        itemList.setLayoutManager(new LinearLayoutManager(this));
        itemList.setAdapter(new ItemListAdapter(itemsManager.getItems(), R.layout.item_list_item, R.id.itemListItemTitleTextView));
    }

    protected void onAddButtonClick(String uuid) {
        if(itemList.getAdapter() != null)
            itemList.getAdapter().notifyItemInserted(0);
        launchItem(uuid);
    }
    public void onItemListItemClick(View view) {
        launchItem(view.getTag().toString());
    }
    protected void launchItem(String uuid) {
        startActivity(new Intent(this, Utility.getAppUtility(this).getItemActivityClass()).putExtra("item_uuid", uuid));
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();
        if(itemList != null && itemList.getAdapter() != null)
            itemList.getAdapter().notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_list_activity_toolbar_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_recycle_bin) {
            startActivity(new Intent(this, RecycleBinActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public WindowInsetsCompat onApplyWindowInsetsListener(View view, WindowInsetsCompat insets) {
        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        getToolbar().setPadding(0, systemBars.top, 0, 0);
        view.setPadding(0, 0, 0, systemBars.bottom);
        return insets;
    }
    protected abstract View getToolbar();

}
