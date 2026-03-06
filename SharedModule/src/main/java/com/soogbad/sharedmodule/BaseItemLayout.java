package com.soogbad.sharedmodule;

import android.content.Context;
import android.graphics.Typeface;
import android.text.style.StyleSpan;
import android.util.AttributeSet;

import androidx.constraintlayout.widget.ConstraintLayout;

public abstract class BaseItemLayout extends ConstraintLayout {

    protected RichEditText editText;
    public void setEditText(RichEditText editText) { this.editText = editText; }

    protected BaseItemLayout(Context context, AttributeSet attrs) { super(context, attrs); }

    public void onBoldButtonClick() {
        editText.setSpan(new StyleSpan(Typeface.BOLD));
    }
    public void onItalicButtonClick() {

    }

}
