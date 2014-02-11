/*
 * PointStyleLabeled.java
 * Created Oct 5, 2011
 */
package org.blaise.style;

import static com.google.common.base.Preconditions.*;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.Set;
import javax.annotation.Nullable;
import org.blaise.util.PointFormatters;

/**
 * Adds a label to a point style. The points' style and labels' style can
 * be manipulated separately. Label visibility may be turned on and off.
 *
 * @author elisha
 */
public class PointStyleLabeled implements PointStyle {

    /** Base style */
    protected @Nullable PointStyle base = new PointStyleBasic();
    /** Label style */
    protected TextStyle labelStyle = new TextStyleBasic();
    /** Whether labels are visible */
    protected boolean showLabel = true;

    /**
     * Construct with default styles
     */
    public PointStyleLabeled() {
    }
    
    /**
     * Construct with base style.
     * @param baseStyle base style to use for point
     */
    public PointStyleLabeled(PointStyle baseStyle) {
        setBaseStyle(baseStyle);
    }

    @Override
    public String toString() {
        return String.format("LabeledPointStyle[baseStyle=%s, labelStyle=%s, showLabel=%s]", 
                base, labelStyle, showLabel);
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="BUILDER PATTERNS">

    /** Sets base style & returns point to this object */
    public PointStyleLabeled baseStyle(PointStyle base) {
        setBaseStyle(base);
        return this;
    }

    /** Sets label style & returns pointer to object */
    public PointStyleLabeled labelStyle(TextStyle labelStyle) {
        setLabelStyle(labelStyle);
        return this;
    }

    /** Sets whether label is shown & returns pointer to object */
    public PointStyleLabeled showLabel(boolean showLabel) {
        setShowLabel(showLabel);
        return this;
    }

    // </editor-fold>


    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //

    public PointStyle getBaseStyle() {
        return base;
    }

    public final void setBaseStyle(PointStyle base) {
        this.base = checkNotNull(base);
    }

    public TextStyle getLabelStyle() {
        return labelStyle;
    }

    public void setLabelStyle(TextStyle labelStyle) {
        this.labelStyle = checkNotNull(labelStyle);
    }

    public boolean isShowLabel() {
        return showLabel;
    }

    public void setShowLabel(boolean showLabel) {
        this.showLabel = showLabel;
    }
    
    //</editor-fold>

    
    //
    // PointStyle METHODS
    //

    public float getMarkerRadius() {
        return base.getMarkerRadius();
    }

    public Shape markerShape(Point2D point) {
        return base.markerShape(point);
    }

    public Shape markerShape(Point2D p, double angle) {
        return base.markerShape(p, angle);
    }

    /**
     * Draws specified point on the graphics canvas with visibility options
     * @param p the point to draw
     * @param label the point's label
     * @param canvas graphics element to draw on
     * @param visibility visibility & highlight settings
     */
    public void draw(Point2D p, String label, Graphics2D canvas, VisibilityHintSet visibility) {
        base.draw(p, canvas, visibility);
        if (showLabel) {
            ((TextStyleBasic)labelStyle).offset(base.getMarkerRadius());
            labelStyle.draw(p, label, canvas, visibility);
        }
    }

    /**
     * Draws specified point on the graphics canvas with visibility options
     * @param p the point to draw
     * @param angle orientation of the point
     * @param label the point's label
     * @param canvas graphics element to draw on
     * @param visibility visibility & highlight settings
     */
    public void draw(Point2D p, double angle, String label, Graphics2D canvas, VisibilityHintSet visibility) {
        base.draw(p, angle, canvas, visibility);
        if (showLabel) {
            labelStyle.draw(p, label, canvas, visibility);
        }
    }

    public void draw(Point2D p, Graphics2D canvas, VisibilityHintSet visibility) {
        draw(p, PointFormatters.formatPoint(p, 2), canvas, visibility);
    }

    public void draw(Point2D p, double angle, Graphics2D canvas, VisibilityHintSet visibility) {
        draw(p, angle, PointFormatters.formatPoint(p, 2), canvas, visibility);
    }
}
