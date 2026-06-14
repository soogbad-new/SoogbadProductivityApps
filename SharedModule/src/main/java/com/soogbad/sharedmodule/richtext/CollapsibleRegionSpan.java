package com.soogbad.sharedmodule.richtext;

import android.text.SpannableStringBuilder;

public class CollapsibleRegionSpan {

    private boolean collapsed = false;
    public boolean isCollapsed() { return collapsed; }
    public void setCollapsed(boolean collapsed) { this.collapsed = collapsed; }

    private SpannableStringBuilder hiddenContent = null;
    public SpannableStringBuilder getHiddenContent() { return hiddenContent; }
    public void setHiddenContent(SpannableStringBuilder hiddenContent) { this.hiddenContent = hiddenContent; }

}
