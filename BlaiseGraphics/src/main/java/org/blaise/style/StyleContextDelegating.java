/*
 * StyleContext.java
 * Created Jan 22, 2011
 */
package org.blaise.style;

/**
 * Delegates all styles to a parent style. This class is designed to be overridden
 * so that subclasses only need to implement overrides for a few styles.
 * 
 * @param <S> type of object to be styled
 * 
 * @author elisha
 */
public abstract class StyleContextDelegating<S> implements StyleContext<S> {
    
    protected final StyleContext<S> parent;

    public StyleContextDelegating() {
        this(StyleContextDefault.getInstance());
    }

    public StyleContextDelegating(StyleContext parent) {
        this.parent = parent;
    }

    /**
     * Return the parent factory used for delegation
     * @return parent
     */
    public StyleContext getParentContext() {
        return parent;
    }

    public ShapeStyle getShapeStyle(S src) {
        return parent == null ? null : parent.getShapeStyle(src);
    }

    public PathStyle getPathStyle(S src) {
        return parent == null ? null : parent.getPathStyle(src);
    }

    public PointStyle getPointStyle(S src) {
        return parent == null ? null : parent.getPointStyle(src);
    }

    public TextStyle getStringStyle(S src) {
        return parent == null ? null : parent.getStringStyle(src);
    }
    
}
