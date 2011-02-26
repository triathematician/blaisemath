/*
 * BasicShapeEntry.java
 * Created Jan 2011 (adapted much from earlier blaise code)
 */

package graphics;

import graphics.renderer.ShapeRenderer;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import graphics.renderer.BasicStrokeRenderer;
import graphics.renderer.GraphicRendererProvider;

/**
 * Encapsulates a shape that can be drawn on a graphics object, together with style
 * elements, visibility/highlight keys, and a renderer that actually draws the shape.
 *
 * @author Elisha Peterson
 */
public class BasicShapeEntry extends AbstractGraphicEntry implements GraphicEntry {

    /** The object that will be drawn. */
    private Shape primitive;
    /** Whether to use stroke or fill renderer (if not specified) */
    private boolean strokeOnly;
    /** The associated renderer (may be null). */
    private ShapeRenderer renderer;

    //
    // CONSTRUCTORS
    //

    /**
     * Construct with no renderer (will use the default)
     * @param primitive the shape to draw
     * @param strokeOnly whether to use the solid renderer or the path/edge renderer
     */
    public BasicShapeEntry(Shape primitive, boolean strokeOnly) {
        this.primitive = primitive;
        this.renderer = null;
        this.strokeOnly = strokeOnly;
    }

    /** Construct with given primitive and renderer. */
    public BasicShapeEntry(Shape primitive, ShapeRenderer renderer) {
        this.primitive = primitive;
        this.renderer = renderer;
        this.strokeOnly = false;
    }

    //
    // PROPERTY PATTERNS
    //

    public Shape getPrimitive() { return primitive; }
    public void setPrimitive(Shape primitive) {
        this.primitive = primitive;
        fireStateChanged();
    }

    public ShapeRenderer getRenderer() { return renderer; }
    public void setRenderer(ShapeRenderer renderer) {
        if (this.renderer != renderer) {
            this.renderer = renderer; 
            fireStateChanged();
        }
    }

    //
    // DRAW METHODS
    //

    public void draw(Graphics2D canvas, GraphicRendererProvider factory) {
        if (renderer == null)
            if (strokeOnly)
                factory.getPathRenderer().draw(primitive, canvas, visibility);
            else
                factory.getSolidRenderer().draw(primitive, canvas, visibility);
        else
            renderer.draw(primitive, canvas, visibility);
    }

    //
    // POINT & MOUSE METHODS
    //

    private static final BasicStroke CONTAINS_STROKE = new BasicStroke(5f);

    /**
     * Checks to see if the provided window point is covered by the primitive, when drawn in this style.
     * @param point the window point
     */
    public boolean contains(Point point, GraphicRendererProvider factory) {
        if (renderer instanceof BasicStrokeRenderer)
            return CONTAINS_STROKE.createStrokedShape(primitive).contains(point);
        else
            return primitive.contains(point);
    }
}