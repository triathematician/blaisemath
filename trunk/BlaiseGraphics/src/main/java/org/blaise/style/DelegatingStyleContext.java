/*
 * StyleContext.java
 * Created Jan 22, 2011
 */
package org.blaise.style;

/**
 * Delegates all styles to a parent style. This class is designed to be overridden
 * so that subclasses only need to implement overrides for a few styles.
 * 
 * @param <Src> type of object to be styled
 * 
 * @author elisha
 */
public abstract class DelegatingStyleContext<Src> implements StyleContext<Src> {
    
    private final StyleContext<Src> parent;

    public DelegatingStyleContext() {
        this(DefaultStyleContext.getInstance());
    }

    public DelegatingStyleContext(StyleContext parent) {
        this.parent = parent;
    }

    /**
     * Return the parent factory used for delegation
     * @return parent
     */
    public StyleContext getParentContext() {
        return parent;
    }

    public ShapeStyle getShapeStyle(Src src) {
        return parent == null ? null : parent.getShapeStyle(src);
    }

    public PathStyle getPathStyle(Src src) {
        return parent == null ? null : parent.getPathStyle(src);
    }

    public PointStyle getPointStyle(Src src) {
        return parent == null ? null : parent.getPointStyle(src);
    }

    public StringStyle getStringStyle(Src src) {
        return parent == null ? null : parent.getStringStyle(src);
    }
    
}
