/*
 * StyleProvider.java
 * Created Jan 22, 2011
 */
package org.bm.blaise.style;

import org.bm.blaise.style.StyleUtils.DefaultStyleProvider;

/**
 * Delegates all styles to a parent style. This class is designed to be overridden
 * so that subclasses only need to implement overrides for a few styles.
 * 
 * @author elisha
 */
public abstract class StyleDelegator implements StyleProvider {
    private final StyleProvider parent;

    public StyleDelegator() {
        this(new DefaultStyleProvider());
    }

    public StyleDelegator(StyleProvider parent) {
        this.parent = parent;
    }

    /**
     * Return the parent factory used for delegation
     * @return parent
     */
    public StyleProvider getParentFactory() {
        return parent;
    }

    public ShapeStyle getShapeStyle() {
        return parent == null ? null : parent.getShapeStyle();
    }

    public PathStyle getPathStyle() {
        return parent == null ? null : parent.getPathStyle();
    }

    public PointStyle getPointStyle() {
        return parent == null ? null : parent.getPointStyle();
    }

    public StringStyle getStringStyle() {
        return parent == null ? null : parent.getStringStyle();
    }
    
}
