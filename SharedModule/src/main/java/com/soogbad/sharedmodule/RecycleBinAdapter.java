package com.soogbad.sharedmodule;

import android.content.Context;
import android.content.Intent;
import android.annotation.SuppressLint;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.app.AlertDialog;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecycleBinAdapter extends RecyclerView.Adapter<RecycleBinAdapter.ViewHolder> {

    private final ArrayList<? extends Item<?>> items;

    public RecycleBinAdapter(ArrayList<? extends Item<?>> items) {
        this.items = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView, deletedAtTextView;
        public ViewHolder(View item) {
            super(item);
            titleTextView = item.findViewById(R.id.itemTitleTextView); deletedAtTextView = item.findViewById(R.id.deletedAtTextView);
        }
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_bin_list_item, parent, false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item<?> item = items.get(position);
        holder.titleTextView.setText(item.Title);
        holder.deletedAtTextView.setText("Deleted " + (System.currentTimeMillis() - item.DeletedAt) / (1000 * 60 * 60 * 24) + " days ago");
        holder.itemView.setTag(item.UUID);
        holder.itemView.setOnCreateContextMenuListener((menu, view, menuInfo) -> showContextMenu(menu, holder, item));
    }

    private void showContextMenu(ContextMenu menu, ViewHolder itemHolder, Item<?> item) {
        new MenuInflater(itemHolder.itemView.getContext()).inflate(R.menu.recycle_bin_item_context_menu, menu);
        for(int i = 0; i < menu.size(); i++)
            menu.getItem(i).setOnMenuItemClickListener(menuItem -> onContextMenuItemClick(menuItem, item, itemHolder));
    }

    private boolean onContextMenuItemClick(MenuItem menuItem, Item<?> item, ViewHolder itemHolder) {
        Context context = itemHolder.itemView.getContext();
        ItemsManager<?, ?> itemsManager = ((ItemApplication<?, ?>)context.getApplicationContext()).getItemsManager();
        if(menuItem.getItemId() == R.id.action_restore) {
            restoreItem(item, context, itemsManager);
            notifyItemRemoved(itemHolder);
            return true;
        }
        else if(menuItem.getItemId() == R.id.action_delete_permanently) {
            permanentlyDeleteItem(item, context, itemsManager);
            notifyItemRemoved(itemHolder);
            return true;
        }
        return false;
    }
    private void restoreItem(Item<?> item, Context context, ItemsManager<?, ?> itemsManager) {
        itemsManager.restoreRecycleBinItem(item.UUID);
        Toast.makeText(context, "Item restored", Toast.LENGTH_SHORT).show();
    }
    private void permanentlyDeleteItem(Item<?> item, Context context, ItemsManager<?, ?> itemsManager) {
        new AlertDialog.Builder(context).setTitle("Delete Permanently").setMessage("Are you sure you want to permanently delete this item?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    itemsManager.permanentlyDeleteRecycleBinItem(item.UUID);
                }).setNegativeButton("Cancel", null).show();
    }
    @SuppressLint("NotifyDataSetChanged")
    private void notifyItemRemoved(ViewHolder itemHolder) {
        int position = itemHolder.getBindingAdapterPosition();
        if(position != RecyclerView.NO_POSITION)
            notifyItemRemoved(position);
        else
            notifyDataSetChanged();
    }

    @Override
    public int getItemCount() { return items.size(); }

}
