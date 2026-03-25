package com.soogbad.sharedmodule;

import android.text.SpannedString;

public abstract class Item<O extends Item.ItemOptions> {

    public String UUID;
    public String Title;
    public SpannedString Content;
    public O Options;

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
    public interface Creator<T extends Item<O>, O extends ItemOptions> {
        T create(String uuid, String title, O options);
    }

    public static class ItemOptions {
        
    }

}
