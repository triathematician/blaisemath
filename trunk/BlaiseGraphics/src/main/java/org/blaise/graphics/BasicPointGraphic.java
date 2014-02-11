/**
 * BasicPointGraphic.java
 * Created Jan 22, 2011
 */
package org.blaise.graphics;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.blaise.style.PointStyle;

/**
 * A point with position, orientation, and an associated style.
 * Implements {@code PointBean}, allowing the point to be dragged around.
 * 
 * @see PointStyle
 * 
 * @author Elisha Peterson
 */
public class BasicPointGraphic extends AbstractPointGraphic {

    /** Angle specifying point orientation */
    protected double angle = 0;
    /** The associated style (may be null). */
    protected @Nullable PointStyle style;

    //
    // CONSTRUCTORS
    //
    
    /**
     * Construct with no point (defaults to origin)
     */
    public BasicPointGraphic() {
        this(new Point2D.Double(), null);
    }

    /** 
     * Construct with no style (will use the default) 
     * @param p initial point
     */
    public BasicPointGraphic(Point2D p) { 
        this(p, null); 
    }

    /** 
     * Construct with given primitive and style.
     * @param p initial point
     * @param style the style
     */
    public BasicPointGraphic(Point2D p, @Nullable PointStyle style) {
        super(p);
        setStyle(style);
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //

    /**
     * Return orientation/angle of the point
     * @return angle
     */
    public double getAngle() {
        return angle; 
    }
    
    /**
     * Set orientation/angle of the point
     * @param d angle
     */
    public void setAngle(double d) {
        if (angle != d) {
            angle = d;
            fireGraphicChanged();
        }
    }

    /**
     * Return the style for the point
     * @return style, or null if there is none
     */
    public @Nullable PointStyle getStyle() { 
        return style; 
    }
    
    /**
     * Set the style for the point
     * @param style the style; may be null
     */
    public final void setStyle(@Nullable PointStyle style) {
        if (this.style != style) {
            this.style = style;
            fireGraphicChanged();
        }
    }
    
    //</editor-fold>
    
        
    /** Return the actual style used for drawing */
    public @Nonnull PointStyle drawStyle() {
        return style == null ? parent.getStyleContext().getPointStyle(this) : style;
    }

    public void draw(Graphics2D canvas) {
        drawStyle().draw(point, angle, canvas, visibility);
    }
    
}
