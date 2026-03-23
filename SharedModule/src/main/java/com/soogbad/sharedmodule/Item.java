package com.soogbad.sharedmodule;

import android.text.SpannedString;

public abstract class Item {

    public String UUID;
    public String Title;
    public SpannedString Content;

    public void loadContent() {
        Content = StorageManager.getItemContent(UUID);
    }

    public void save() {
        StorageManager.saveItem(this);
    }

    public void delete() {
        StorageManager.deleteItem(this);
    }

    @FunctionalInterface
    public interface Creator<T extends Item> {
        T create(String uuid, String title);
    }

}
