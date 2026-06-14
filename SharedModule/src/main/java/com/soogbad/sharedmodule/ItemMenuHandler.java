package com.soogbad.sharedmodule;

import android.content.Context;
import java.util.Map;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.LayoutRes;

public class ItemMenuHandler {

    private final Map<Integer, Runnable> actions;
    private final Context context;

    public ItemMenuHandler(Context context, Map<Integer, Runnable> actions) {
        this.context = context; this.actions = actions;
    }

    public void showContextMenu(ContextMenu menu, @LayoutRes int menuRes) {
        new MenuInflater(context).inflate(menuRes, menu);
        for(int i = 0; i < menu.size(); i++)
            menu.getItem(i).setOnMenuItemClickListener(this::handleMenuItem);
    }

    public void showOverflowMenu(View anchor, @LayoutRes int menuRes) {
        PopupMenu popup = new PopupMenu(context, anchor);
        popup.getMenuInflater().inflate(menuRes, popup.getMenu());
        popup.setOnMenuItemClickListener(this::handleMenuItem);
        popup.show();
    }

    private boolean handleMenuItem(MenuItem menuItem) {
        Runnable action = actions.get(menuItem.getItemId());
        if(action != null) {
            action.run();
            return true;
        }
        return false;
    }

}
