package com.soogbad.commonmodule;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

public class BaseNote extends AppCompatEditText {


    public BaseNote(Context context, AttributeSet attrs) {
        super(context, attrs);

        SpannableString text = new SpannableString("Hello World");
        text.setSpan(new StyleSpan(Typeface.BOLD), 0, 5, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(text);
    }

}
