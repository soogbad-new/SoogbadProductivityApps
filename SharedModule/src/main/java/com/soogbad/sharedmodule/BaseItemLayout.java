package com.soogbad.sharedmodule;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import androidx.constraintlayout.widget.ConstraintLayout;

public abstract class BaseItemLayout extends ConstraintLayout {

    protected EditText editText;
    public void setEditText(EditText editText) { this.editText = editText; }

    protected BaseItemLayout(Context context, AttributeSet attrs) { super(context, attrs); }

    public void onBoldButtonClick() {

    }
    public void onItalicButtonClick() {

    }

}
