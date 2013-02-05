/*
 * DecoratorPointStyle.java
 * Created Aug 27, 2011
 */
package org.blaise.style;

import java.awt.Color;

/**
 * A style that generally defers to a base style, except for radius and color.
 * 
 * @author elisha
 */
public class DecoratorPointStyle extends PointStyleSupport {
    
    private final PointStyleSupport base;
    private final Float rad;
    private final Color col;
    
    /**
     * Defer to base style, except for radius.
     * @param base
     * @param rad 
     */
    public DecoratorPointStyle(PointStyleSupport base, float rad) {
        this.base = base;
        this.rad = rad;
        col = null;
    }
    
    /**
     * Defer to base style, except for color.
     * @param base
     * @param c 
     */
    public DecoratorPointStyle(PointStyleSupport base, Color c) {
        this.base = base;
        this.col = c;
        this.rad = 1f;
    }

    /**
     * Defer to base style, except for radius and color.
     * @param base
     * @param rad
     * @param color 
     */
    public DecoratorPointStyle(PointStyleSupport base, float rad, Color color) {
        this.base = base;
        this.rad = rad;
        this.col = color;
    }
    
    @Override 
    public ShapeFactory getShape() { 
        return base.getShape();
    }
        
    public float getRadius() { 
        return rad * base.getRadius();
    }
    
    @Override 
    protected ShapeStyle getShapeStyle() {
        if (col == null)
            return base.getShapeStyle();
        BasicShapeStyle r = (BasicShapeStyle) base.getShapeStyle();
        return new BasicShapeStyle(col, r.getStroke(), r.getThickness());
    }
    
}
