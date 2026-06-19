package com.soogbad.sharedmodule.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soogbad.sharedmodule.core.Item;
import com.soogbad.sharedmodule.core.ItemsManager;
import com.soogbad.sharedmodule.R;
import com.soogbad.sharedmodule.core.Utility;

import java.util.ArrayList;
import java.util.Map;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> {

    private final ArrayList<? extends Item<?>> items;
    private final int item_resource, item_title_id;

    public ItemListAdapter(ArrayList<? extends Item<?>> items, @LayoutRes int item_resource, @IdRes int item_title_id) {
        this.items = items; this.item_resource = item_resource; this.item_title_id = item_title_id;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        public ViewHolder(View item, @IdRes int item_title_id) {
            super(item);
            titleTextView = item.findViewById(item_title_id);
        }
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(item_resource, parent, false);
        return new ViewHolder(item, item_title_id);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item<?> item = items.get(position);
        holder.titleTextView.setText(item.Title);
        holder.itemView.setTag(item.UUID);
        holder.itemView.setOnCreateContextMenuListener((menu, view, menuInfo) -> showContextMenu(menu, holder, item));
    }

    private void showContextMenu(ContextMenu menu, ViewHolder itemHolder, Item<?> item) {
        Context context = itemHolder.itemView.getContext();
        ItemsManager<?, ?> itemsManager = Utility.getItemsManager(context);
        new ItemMenuHandler(context, Map.ofEntries(
            Map.entry(R.id.action_delete, () -> {
                itemsManager.moveItemToRecycleBin(item.UUID);
                notifyItemRemoved(itemHolder);
            }),
            Map.entry(R.id.action_copy_uuid, () ->
                Utility.getAppUtility(context).copyItemUuid(context, item)
            )
        )).showContextMenu(menu, R.menu.item_menu);
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
