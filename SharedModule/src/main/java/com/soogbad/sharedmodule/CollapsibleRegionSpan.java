package com.soogbad.sharedmodule;

import android.text.SpannableStringBuilder;

public class CollapsibleRegionSpan {

    private boolean collapsed;
    private SpannableStringBuilder hiddenContent;

    public CollapsibleRegionSpan() {
        collapsed = false;
    }

    public boolean isCollapsed() { return collapsed; }
    public void setCollapsed(boolean collapsed) { this.collapsed = collapsed; }
    public SpannableStringBuilder getHiddenContent() { return hiddenContent; }
    public void setHiddenContent(SpannableStringBuilder hiddenContent) { this.hiddenContent = hiddenContent; }

}
