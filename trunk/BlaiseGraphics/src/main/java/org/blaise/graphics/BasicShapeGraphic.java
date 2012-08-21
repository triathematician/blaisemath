/*
 * BasicShapeGraphic.java
 * Created Jan 2011 (adapted much from earlier blaise code)
 */

package org.blaise.graphics;

import java.awt.Rectangle;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import org.blaise.style.PathStyle;
import org.blaise.style.ShapeStyle;

/**
 * A shape or path with an associated style.
 * If the style is null, then the shape will be drawn as either a path or solid shape,
 * depending on the provided {@code strokeOnly} parameter.
 *
 * @see ShapeStyle
 * @see PathStyle
 *
 * @author Elisha Peterson
 */
public class BasicShapeGraphic extends GraphicSupport {

    /** The object that will be drawn. */
    private Shape primitive;
    /** Whether to use stroke or fill style (if not specified) */
    private boolean strokeOnly;
    /** The associated style (may be null). */
    private ShapeStyle style;
    

    //
    // CONSTRUCTORS
    //

    /**
     * Construct with no style (will use the default)
     * @param primitive the shape to draw
     * @param strokeOnly determines whether to use the solid style or the path/edge style
     */
    public BasicShapeGraphic(Shape primitive, boolean strokeOnly) {
        this.primitive = primitive;
        this.style = null;
        this.strokeOnly = strokeOnly;
    }

    /**
     * Construct with given primitive and style.
     * @param primitive the shape to draw
     * @param style style used to draw
     */
    public BasicShapeGraphic(Shape primitive, ShapeStyle style) {
        this.primitive = primitive;
        this.style = style;
        this.strokeOnly = style instanceof PathStyle;
    }

    @Override
    public String toString() {
        return "Shape";
    }
    

    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    //
    // PROPERTIES
    //

    /**
     * Return the shape for the graphic.
     * @return shape
     */
    public Shape getPrimitive() {
        return primitive;
    }

    /**
     * Set the shape for the graphic.
     * @param primitive shape
     */
    public void setPrimitive(Shape primitive) {
        this.primitive = primitive;
        fireGraphicChanged();
    }

    /**
     * Return the style for the graphic.
     * @return style
     */
    public ShapeStyle getStyle() {
        return style;
    }

    /**
     * Sets the style for the graphic
     * @param style the style
     */
    public void setStyle(ShapeStyle style) {
        if (this.style != style) {
            this.style = style;
            fireGraphicChanged();
        }
    }
    
    //</editor-fold>
    
    
    //
    // GRAPHIC METHODS
    //

    /** Used to test contains */
    public static final BasicStroke CONTAINS_STROKE = new BasicStroke(5f);

    public boolean contains(Point point) {
        return asStroke() ? CONTAINS_STROKE.createStrokedShape(primitive).contains(point)
                : primitive.contains(point);
    }

    public boolean intersects(Rectangle box) {
        return asStroke() ? CONTAINS_STROKE.createStrokedShape(primitive).intersects(box)
                : primitive.intersects(box);
    }
    

    //
    // DRAW METHODS
    //

    /** Return true if painting as a stroke. */
    private boolean asStroke() { return (style == null && strokeOnly) || style instanceof PathStyle; }

    /** Return the actual style used for drawing */
    private ShapeStyle drawStyle() {
        return style == null ?
                (asStroke() ? parent.getStyleProvider().getPathStyle(this) : parent.getStyleProvider().getShapeStyle(this))
                : style;
    }

    public void draw(Graphics2D canvas) {
        drawStyle().draw(primitive, canvas, visibility);
    }
    
}