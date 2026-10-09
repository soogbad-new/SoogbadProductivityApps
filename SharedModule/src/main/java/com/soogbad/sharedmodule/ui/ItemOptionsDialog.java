package com.soogbad.sharedmodule.ui;

import android.content.Context;
import android.view.View;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.soogbad.sharedmodule.R;
import com.soogbad.sharedmodule.core.Item;

import java.util.function.Consumer;

@SuppressWarnings("unused")
public abstract class ItemOptionsDialog<O extends Item.Options> {

    protected final Context context;
    protected final O initialOptions;
    protected final Consumer<O> callback;

    protected ItemOptionsDialog(Context context, O initialOptions, Consumer<O> callback) {
        this.context = context; this.initialOptions = initialOptions; this.callback = callback;
    }

    public abstract void show();
    protected abstract void onConfirm();

    protected void showDialog(View view) {
        new MaterialAlertDialogBuilder(context, R.style.AlertDialogTheme).setTitle("Edit Options").setView(view)
                .setPositiveButton("OK", (dialog, which) -> onConfirm()).setNegativeButton("Cancel", null).show();
    }

}
