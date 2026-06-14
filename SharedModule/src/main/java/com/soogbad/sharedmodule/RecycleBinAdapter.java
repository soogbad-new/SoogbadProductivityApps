package com.soogbad.sharedmodule;

import android.content.Context;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Map;

public class RecycleBinAdapter extends RecyclerView.Adapter<RecycleBinAdapter.ViewHolder> {

    private final ArrayList<? extends Item<?>> items;

    public RecycleBinAdapter(ArrayList<? extends Item<?>> items) {
        this.items = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView, deletedAtTextView;
        public ViewHolder(View item) {
            super(item);
            titleTextView = item.findViewById(R.id.recycleBinItemTitleTextView); deletedAtTextView = item.findViewById(R.id.recycleBinItemDeletedAtTextView);
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
        holder.deletedAtTextView.setText(MessageFormat.format("Deleted {0} days ago", (System.currentTimeMillis() - item.DeletedAt) / (1000 * 60 * 60 * 24)));
        holder.itemView.setTag(item.UUID);
        holder.itemView.setOnCreateContextMenuListener((menu, view, menuInfo) -> showContextMenu(menu, holder, item));
    }

    private void showContextMenu(ContextMenu menu, ViewHolder itemHolder, Item<?> item) {
        Context context = itemHolder.itemView.getContext();
        ItemsManager<?, ?> itemsManager = Utility.getItemsManager(context);
        new ItemMenuHandler(context, Map.ofEntries(
            Map.entry(R.id.action_restore, () -> {
                itemsManager.restoreRecycleBinItem(item.UUID);
                Toast.makeText(context, "Item restored", Toast.LENGTH_SHORT).show();
                notifyItemRemoved(itemHolder);
            }),
            Map.entry(R.id.action_delete_permanently, () ->
                new AlertDialog.Builder(context).setTitle("Delete Permanently").setMessage("Are you sure you want to permanently delete this item?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            itemsManager.permanentlyDeleteRecycleBinItem(item.UUID);
                            notifyItemRemoved(itemHolder);
                        }).setNegativeButton("Cancel", null).show()
            )
        )).showContextMenu(menu, R.menu.recycle_bin_item_context_menu);
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
