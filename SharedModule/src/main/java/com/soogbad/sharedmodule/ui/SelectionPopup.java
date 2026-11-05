package com.soogbad.sharedmodule.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;

import com.soogbad.sharedmodule.R;
import com.soogbad.sharedmodule.core.Utility;

import java.util.function.IntConsumer;

public class SelectionPopup {

    private interface OptionBinder { void bind(TextView textView, int index, boolean selected); }

    private final View anchor;
    private final @LayoutRes int optionLayout;
    private final OptionBinder optionBinder;
    private final IntConsumer onSelect;
    private Runnable onShowListener = null;
    public void setOnShowListener(Runnable onShowListener) { this.onShowListener = onShowListener; }

    private final boolean showAboveAnchor;
    private final int optionCount;
    private boolean suppressNextPopup = false;

    private SelectionPopup(View anchor, boolean showAboveAnchor, @LayoutRes int optionLayout, int optionCount, OptionBinder optionBinder, IntConsumer onSelect) {
        this.anchor = anchor; this.showAboveAnchor = showAboveAnchor; this.optionLayout = optionLayout; this.optionCount = optionCount; this.optionBinder = optionBinder; this.onSelect = onSelect;
    }

    public static SelectionPopup withTextOptions(View anchor, boolean showAboveAnchor, String[] labels, IntConsumer onSelect) {
        return new SelectionPopup(anchor, showAboveAnchor, R.layout.selection_popup_option, labels.length, (textView, i, selected) -> {
            textView.setText(labels[i]);
            if(selected)
                textView.setBackgroundResource(R.drawable.selected_popup_option_border);
        }, onSelect);
    }
    public static SelectionPopup withColorOptions(View anchor, boolean showAboveAnchor, int[] colors, IntConsumer onSelect) {
        return new SelectionPopup(anchor, showAboveAnchor, R.layout.selection_popup_option_color, colors.length, (textView, i, selected) -> {
            textView.setBackgroundColor(colors[i]);
            if(selected) {
                Drawable borderDrawable = ContextCompat.getDrawable(anchor.getContext(), R.drawable.selected_popup_option_border);
                textView.setBackground(new LayerDrawable(new Drawable[] { new ColorDrawable(colors[i]), borderDrawable }));
            }
        }, onSelect);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void show(int selectedIndex) {
        if(suppressNextPopup) {
            suppressNextPopup = false;
            return;
        }
        if(onShowListener != null)
            onShowListener.run();
        Context themedContext = new ContextThemeWrapper(anchor.getContext(), R.style.AppTheme_PopupOverlay);
        LayoutInflater inflater = LayoutInflater.from(themedContext);
        LinearLayout popupLayout = (LinearLayout)inflater.inflate(R.layout.selection_popup, (ViewGroup)anchor.getParent(), false);
        PopupWindow popup = new PopupWindow(popupLayout, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);
        popup.setWindowLayoutType(WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL);
        popup.setElevation(8);
        popup.setOutsideTouchable(true);
        TypedValue typedValue = new TypedValue();
        themedContext.getTheme().resolveAttribute(android.R.attr.colorBackground, typedValue, true);
        popup.setBackgroundDrawable(new ColorDrawable(typedValue.data));
        // Tapping the anchor while open dismisses the popup through the outside touch, so the click that follows must not reopen it
        popup.setTouchInterceptor((view, motionEvent) -> {
            if(motionEvent.getAction() == MotionEvent.ACTION_OUTSIDE && Utility.isMotionEventInsideView(motionEvent, anchor))
                suppressNextPopup = true;
            return false;
        });
        for(int i = 0; i < optionCount; i++) {
            TextView textView = (TextView)inflater.inflate(optionLayout, popupLayout, false);
            optionBinder.bind(textView, i, i == selectedIndex);
            final int index = i;
            textView.setOnClickListener(view -> { popup.dismiss(); onSelect.accept(index); });
            popupLayout.addView(textView);
        }
        if(showAboveAnchor) {
            popupLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            popup.showAsDropDown(anchor, 0, -(anchor.getHeight() + popupLayout.getMeasuredHeight()));
        }
        else
            popup.showAsDropDown(anchor);
    }

}
