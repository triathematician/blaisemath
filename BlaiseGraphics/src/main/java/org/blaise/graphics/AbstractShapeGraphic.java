/*
 * BasicShapeGraphic.java
 * Created Jan 2011 (adapted much from earlier blaise code)
 */

package org.blaise.graphics;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import org.blaise.style.BasicShapeStyle;
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
public abstract class AbstractShapeGraphic extends GraphicSupport {

    /** The object that will be drawn. */
    private Shape primitive;
    /** Whether to use stroke or fill style (if not specified) */
    private boolean strokeOnly;

    /**
     * Construct with no style (will use the default)
     * @param primitive the shape to draw
     */
    public AbstractShapeGraphic(Shape primitive) {
        this(primitive, false);
    }

    /**
     * Construct with no style (will use the default)
     * @param primitive the shape to draw
     * @param strokeOnly determines whether to use the solid style or the path/edge style
     */
    public AbstractShapeGraphic(Shape primitive, boolean strokeOnly) {
        this.primitive = primitive;
        this.strokeOnly = strokeOnly;
    }

    @Override
    public String toString() {
        return "Shape"+primitive;
    }

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

    public boolean isStrokeOnly() {
        return strokeOnly;
    }

    public void setStrokeOnly(boolean strokeOnly) {
        if (this.strokeOnly != strokeOnly) {
            this.strokeOnly = strokeOnly;
            fireGraphicChanged();
        }
    }
    
    /**
     * Return style used to draw the shape.
     * @return style
     */
    public abstract ShapeStyle drawStyle();

    /** Return true if painting as a stroke. */
    protected boolean paintingAsStroke() { 
        ShapeStyle style = drawStyle();
        if (style == null && strokeOnly) {
            return true;
        } else if (style instanceof BasicShapeStyle) {
            BasicShapeStyle bss = (BasicShapeStyle) style;
            return bss.getFill() == null;
        } else if (style instanceof PathStyle) {
            return true;
        } else {
            return false;
        }
    }

    public boolean contains(Point point) {
        ShapeStyle style = drawStyle();
        if (!paintingAsStroke() && primitive.contains(point)) {
            return true;
        }
        float thickness = style == null || !(style instanceof PathStyle) ? 1f
                : ((PathStyle)style).getThickness();
        return new BasicStroke(thickness).createStrokedShape(primitive).contains(point);
    }

    public boolean intersects(Rectangle box) {
        ShapeStyle style = drawStyle();
        if (!paintingAsStroke() && primitive.intersects(box)) {
            return true;
        }
        float thickness = style == null || !(style instanceof PathStyle) ? 1f
                : ((PathStyle)style).getThickness();
        return new BasicStroke(thickness).createStrokedShape(primitive).intersects(box);
    }

    public void draw(Graphics2D canvas) {
        drawStyle().draw(primitive, canvas, visibility);
    }
    
}