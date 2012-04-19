/*
 * LabeledPointStyle.java
 * Created Oct 5, 2011
 */
package org.bm.blaise.style;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import org.bm.util.PointFormatters;

/**
 * Adds a label to a point style. The points' style and labels' style can
 * be manipulated separately. Label visibility may be turned on and off.
 * 
 * @author elisha
 */
public class LabeledPointStyle implements PointStyle {

    /** Base style */
    private PointStyle base;
    /** Label style */
    private StringStyle labelStyle = new BasicStringStyle();
    /** Whether labels are visible */
    private boolean showLabel = true;

    /**
     * Construct with default base style
     */
    public LabeledPointStyle() {
        this.base = new BasicPointStyle();
    }

    /** 
     * Construct with specified base style
     * @param base the base style
     */
    public LabeledPointStyle(PointStyle base) {
        this.base = base;
    }
    

    //
    // PROPERTIES
    //
    
    public PointStyle getBaseStyle() {
        return base;
    }

    public void setBaseStyle(PointStyle base) {
        this.base = base;
    }

    public StringStyle getLabelStyle() {
        return labelStyle;
    }

    public void setLabelStyle(StringStyle labelStyle) {
        this.labelStyle = labelStyle;
    }

    public boolean isShowLabel() {
        return showLabel;
    }

    public void setShowLabel(boolean showLabel) {
        this.showLabel = showLabel;
    }

    //
    // PointStyle METHODS
    //
    
    public Shape shape(Point2D point) {
        return base.shape(point);
    }

    public Shape shape(Point2D p, double angle) {
        return base.shape(p, angle);
    }

    public float getRadius() {
        return base.getRadius();
    }

    /**
     * Draws specified point on the graphics canvas with visibility options
     * @param p the point to draw
     * @param label the point's label
     * @param canvas graphics element to draw on
     * @param visibility visibility & highlight settings
     */
    public void draw(Point2D p, String label, Graphics2D canvas, VisibilityKey visibility) {
        base.draw(p, canvas, visibility);
        if (showLabel) {
            ((BasicStringStyle)labelStyle).offset(base.getRadius());
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
    public void draw(Point2D p, double angle, String label, Graphics2D canvas, VisibilityKey visibility) {
        base.draw(p, angle, canvas, visibility);
        if (showLabel)
            labelStyle.draw(p, label, canvas, visibility);
    }

    public void draw(Point2D p, Graphics2D canvas, VisibilityKey visibility) {
        draw(p, PointFormatters.formatPoint(p, 2), canvas, visibility);
    }

    public void draw(Point2D p, double angle, Graphics2D canvas, VisibilityKey visibility) {
        draw(p, angle, PointFormatters.formatPoint(p, 2), canvas, visibility);
    }

    public void drawAll(Iterable<Point2D> pts, Graphics2D canvas, VisibilityKey visibility) {
        for (Point2D p : pts) {
            draw(p, canvas, visibility);
        }
    }

    public void drawAll(Iterable<Point2D> pts, double angle, Graphics2D canvas, VisibilityKey visibility) {
        for (Point2D p : pts) {
            draw(p, angle, canvas, visibility);
        }
    }
}
