package com.soogbad.sharedmodule.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.soogbad.sharedmodule.R;
import com.soogbad.sharedmodule.core.Item;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

@SuppressWarnings({"unused", "SameParameterValue"})
public abstract class ItemOptionsDialog<O extends Item.Options> {

    protected final Context context;
    protected final O initialOptions;
    protected final Consumer<O> callback;

    private View dialogView;
    private LinearLayout toolbar;

    protected ItemOptionsDialog(Context context, O initialOptions, Consumer<O> callback) {
        this.context = context; this.initialOptions = initialOptions; this.callback = callback;
    }

    public abstract void show();
    protected abstract void onConfirm();

    protected View inflateDialogView(@LayoutRes int contentLayout) {
        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        inflater.inflate(R.layout.options_dialog_toolbar, layout, true);
        toolbar = layout.findViewById(R.id.optionsDialogToolbar);
        inflater.inflate(contentLayout, layout, true);
        dialogView = layout;
        return dialogView;
    }

    protected void showDialog() {
        new MaterialAlertDialogBuilder(context, R.style.AlertDialogTheme).setTitle("Edit Options").setView(dialogView)
                .setPositiveButton("OK", (dialog, which) -> onConfirm()).setNegativeButton("Cancel", null).show();
    }

    protected void addActionButton(@DrawableRes int icon, String tooltip, Runnable onClick) {
        MaterialButton button = addToolbarButton(icon, tooltip);
        button.setOnClickListener(view -> onClick.run());
    }
    protected void addToggleButton(@DrawableRes int icon, String tooltip, boolean initialState, Consumer<Boolean> onCheckedChange) {
        MaterialButton button = addToolbarButton(icon, tooltip);
        button.setCheckable(true); button.setChecked(initialState);
        button.setIconTint(ContextCompat.getColorStateList(context, initialState ? R.color.selectedGreen : R.color.white));
        button.addOnCheckedChangeListener((view, isChecked) -> { button.setIconTint(ContextCompat.getColorStateList(context, isChecked ? R.color.selectedGreen : R.color.white)); onCheckedChange.accept(isChecked); });
    }
    protected void addTextSelectionButton(@DrawableRes int icon, String tooltip, String[] labels, int initialIndex, IntConsumer onSelect) {
        MaterialButton button = addToolbarButton(icon, tooltip);
        AtomicInteger selectedIndex = new AtomicInteger(initialIndex);
        SelectionPopup popup = SelectionPopup.withTextOptions(button, false, labels, i -> { selectedIndex.set(i); onSelect.accept(i); });
        button.setOnClickListener(view -> popup.show(selectedIndex.get()));
    }
    protected void addColorSelectionButton(@DrawableRes int icon, String tooltip, int[] colors, int initialIndex, IntConsumer onSelect) {
        MaterialButton button = addToolbarButton(icon, tooltip);
        AtomicInteger selectedIndex = new AtomicInteger(initialIndex);
        button.setIconTint(ColorStateList.valueOf(colors[initialIndex]));
        SelectionPopup popup = SelectionPopup.withColorOptions(button, false, colors, i -> { selectedIndex.set(i); button.setIconTint(ColorStateList.valueOf(colors[i])); onSelect.accept(i); });
        button.setOnClickListener(view -> popup.show(selectedIndex.get()));
    }
    private MaterialButton addToolbarButton(@DrawableRes int icon, String tooltip) {
        MaterialButton button = (MaterialButton)LayoutInflater.from(context).inflate(R.layout.options_dialog_toolbar_button, toolbar, false);
        button.setIconResource(icon); button.setTooltipText(tooltip); button.setContentDescription(tooltip);
        toolbar.addView(button);
        return button;
    }

}
