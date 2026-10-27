package com.soogbad.soogbadcalendar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.soogbad.sharedmodule.core.ItemsManager;
import com.soogbad.sharedmodule.core.Utility;
import com.soogbad.sharedmodule.ui.RecycleBinActivity;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class MonthGridActivity extends AppCompatActivity {

    private MonthGrid monthGrid;
    private TextView monthTitleTextView;

    private ItemsManager<Event, Event.Options> itemsManager;
    private YearMonth selectedMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.setWindowProperties(this, R.layout.month_grid_activity, R.id.toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), this::onApplyWindowInsetsListener);
        monthGrid = findViewById(R.id.monthGrid); monthTitleTextView = findViewById(R.id.monthTitleTextView);
        itemsManager = ((SoogbadCalendarApplication)getApplication()).getItemsManager();
        selectedMonth = YearMonth.now();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 0);
    }

    public void onPreviousMonthButtonClick(View view) { showMonth(selectedMonth.minusMonths(1)); }
    public void onNextMonthButtonClick(View view) { showMonth(selectedMonth.plusMonths(1)); }    

    @Override
    protected void onResume() {
        super.onResume();
        showMonth(selectedMonth);
    }

    private void showMonth(YearMonth month) {
        selectedMonth = month;
        monthTitleTextView.setText(month.format(DateTimeFormatter.ofPattern("LLLL yyyy")));
        monthGrid.setMonth(month, itemsManager.getItems());
    }

    public void onDayCellClick(View view) {
        startActivity(new Intent(this, DayListActivity.class).putExtra("date", view.getTag().toString()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(com.soogbad.sharedmodule.R.menu.item_list_activity_toolbar_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == com.soogbad.sharedmodule.R.id.action_recycle_bin) {
            startActivity(new Intent(this, RecycleBinActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private WindowInsetsCompat onApplyWindowInsetsListener(View view, WindowInsetsCompat insets) {
        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        findViewById(R.id.toolbar).setPadding(0, systemBars.top, 0, 0);
        view.setPadding(0, 0, 0, systemBars.bottom);
        return insets;
    }

}
