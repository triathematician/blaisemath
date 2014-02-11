/*
 * Styles.java
 * Created May 9, 2013
 */
package org.blaise.style;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import javax.annotation.Nullable;

/**
 * Factory class providing convenience methods for easily creating styles.
 * 
 * @author Elisha
 */
public final class Styles {
    
    /** Default stroke of 1 unit width. */
    public static final BasicStroke DEFAULT_STROKE = new BasicStroke(1.0f);
    /** Default composite */
    public static final Composite DEFAULT_COMPOSITE = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);
    
   
    /**
     * Create a basic shape style with given fill & stroke
     * @param fill fill color
     * @param stroke stroke color
     * @return shape style
     */
    public static ShapeStyleBasic fillStroke(@Nullable Color fill, @Nullable Color stroke) {
        return new ShapeStyleBasic().fill(fill).stroke(stroke);
    }
    
    /**
     * Create a path style with a stroke color and width
     * @param stroke stroke color
     * @param width stroke width
     * @return path style
     */
    public static PathStyleBasic strokeWidth(Color stroke, float width) {
        return new PathStyleBasic().stroke(stroke).strokeWidth(width);
    }
    
    
}
