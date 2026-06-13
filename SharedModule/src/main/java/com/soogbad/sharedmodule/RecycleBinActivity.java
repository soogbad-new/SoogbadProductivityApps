package com.soogbad.sharedmodule;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

public class RecycleBinActivity extends AppCompatActivity {

    private RecyclerView recycleBinList;
    private ItemsManager<?, ?> itemsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.setWindowProperties(this, R.layout.activity_recycle_bin, R.id.toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), this::onApplyWindowInsetsListener);
        recycleBinList = findViewById(R.id.recycleBinList);
        ItemApplication<?, ?> app = (ItemApplication<?, ?>)getApplication();
        ((MaterialToolbar)findViewById(R.id.toolbar)).setTitle(app.getAppUtility().getAppName());
        itemsManager = app.getItemsManager();
        itemsManager.loadRecycleBinItems();
        recycleBinList.setLayoutManager(new LinearLayoutManager(this));
        recycleBinList.setAdapter(new RecycleBinAdapter(itemsManager.getRecycleBinItems()));
    }

    public void onRecycleBinItemClick(View view) {
        if(view.getTag() == null) return;
        Intent intent = new Intent(this, Utility.getAppUtility(this).getItemActivityClass());
        startActivity(intent.putExtra("item_uuid", view.getTag().toString()).putExtra("preview_mode", true));
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onEmptyBinButtonClick(View view) {
        if(itemsManager.getRecycleBinItems().isEmpty()) return;
        new AlertDialog.Builder(this).setTitle("Empty Recycle Bin").setMessage("Are you sure you want to empty the recycle bin?")
                .setPositiveButton("Empty", (dialog, which) -> {
                    itemsManager.emptyRecycleBin();
                    if(recycleBinList.getAdapter() != null) recycleBinList.getAdapter().notifyDataSetChanged();
                    Toast.makeText(this, "Recycle bin emptied", Toast.LENGTH_SHORT).show();
                }).setNegativeButton("Cancel", null).show();
    }

    public WindowInsetsCompat onApplyWindowInsetsListener(View view, WindowInsetsCompat insets) {
        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        view.getRootView().findViewById(R.id.toolbar).setPadding(0, systemBars.top, 0, 0);
        view.setPadding(0, 0, 0, systemBars.bottom);
        return insets;
    }

}
