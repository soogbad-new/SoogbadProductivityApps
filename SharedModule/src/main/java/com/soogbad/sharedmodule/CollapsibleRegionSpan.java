package com.soogbad.sharedmodule;

public class CollapsibleRegionSpan {

    private boolean collapsed;

    public CollapsibleRegionSpan() {
        collapsed = false;
    }

    public boolean isCollapsed() { return collapsed; }
    public void setCollapsed(boolean collapsed) { this.collapsed = collapsed; }

}
