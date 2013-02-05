/*
 * BasicShapeGraphic.java
 * Created Jan 2011 (adapted much from earlier blaise code)
 */

package org.blaise.graphics;

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
public class BasicShapeGraphic extends AbstractShapeGraphic {

    /** The associated style (may be null). */
    private ShapeStyle style;

    /**
     * Construct with no style (will use the default)
     * @param primitive the shape to draw
     */
    public BasicShapeGraphic(Shape primitive) {
        this(primitive, false);
    }

    /**
     * Construct with no style (will use the default)
     * @param primitive the shape to draw
     * @param strokeOnly determines whether to use the solid style or the path/edge style
     */
    public BasicShapeGraphic(Shape primitive, boolean strokeOnly) {
        super(primitive, strokeOnly);
    }

    /**
     * Construct with given primitive and style.
     * @param primitive the shape to draw
     * @param style style used to draw
     */
    public BasicShapeGraphic(Shape primitive, ShapeStyle style) {
        super(primitive, style instanceof PathStyle);
        setStyle(style);
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

    /** Return the actual style used for drawing */
    public ShapeStyle drawStyle() {
        if (style != null) {
            return style;
        }
        return isStrokeOnly() ? parent.getStyleContext().getPathStyle(this)
                : parent.getStyleContext().getShapeStyle(this);
    }
    
}