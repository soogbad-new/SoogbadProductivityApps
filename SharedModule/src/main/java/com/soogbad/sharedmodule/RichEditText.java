package com.soogbad.sharedmodule;

import android.content.Context;
import android.text.Editable;
import android.text.style.StyleSpan;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

public class RichEditText extends AppCompatEditText {

    public RichEditText(Context context, AttributeSet attrs) { super(context, attrs);
        setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur et purus tempor, mollis ex vitae, pretium magna. Vestibulum hendrerit velit id orci blandit eleifend. Nam quis dolor a dui tristique consequat a et nibh. Phasellus convallis lacus id feugiat viverra. Nam tempor laoreet lorem, id luctus metus elementum vitae. Phasellus viverra justo ac eros porta tristique. Vivamus consequat ligula purus, vitae pellentesque justo rhoncus sit amet. Aenean imperdiet, neque nec scelerisque porta, sapien felis malesuada justo, in venenatis leo mi in lorem. Nunc iaculis condimentum sapien, vitae cursus velit mattis non. Integer finibus arcu lacus, quis dictum ipsum aliquam nec. Nunc in mollis sapien, et pharetra ligula. Mauris ipsum magna, posuere eget vulputate a, finibus ac metus. Aenean lectus metus, elementum ut risus sit amet, egestas lobortis mauris. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus.");
    }

    public void toggleStyle(StyleSpan style) {
        Editable editable = getText();
        int selectionStart = getSelectionStart(); int selectionEnd = getSelectionEnd();
        assert editable != null;
        StyleSpan[] spans = editable.getSpans(selectionStart, selectionEnd, StyleSpan.class);
        if(selectionStart != selectionEnd) {
            int min = selectionEnd, max = selectionStart;
            for(StyleSpan span : spans) {
                if(span.getStyle() == style.getStyle()) {
                    int spanStart = editable.getSpanStart(span); int spanEnd = editable.getSpanEnd(span);
                    editable.removeSpan(span);
                    if(spanStart < selectionStart)
                        editable.setSpan(style, spanStart, selectionStart, Editable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if(spanEnd > selectionEnd)
                        editable.setSpan(style, selectionEnd, spanEnd, Editable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if(spanStart < min)
                        min = spanStart;
                    if(spanEnd > max)
                        max = spanEnd;
                }
            }
            if(!(min <= selectionStart && max >= selectionEnd))
                editable.setSpan(style, selectionStart, selectionEnd, Editable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

}
