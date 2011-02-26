/*
 * AbstractPointStyle.java
 * Created May 13, 2010
 */

package old.styles.transferred;

import old.other.StyleMap;
import graphics.renderer.ShapeRenderer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.util.Map;
import graphics.renderer.ShapeLibrary;
import static graphics.renderer.StyleUtils.*;

/**
 * <p>
 *   <code>AbstractPointStyle</code> contains the basic stylizing features for drawing a point
 *   on a plane as a small circle, or within a small radius. Handles parameters for the radius,
 *   color/stroke/fill, and the type of visual mark displayed.
 *   Intended to be used as a superclass for any styles that draw points on the screen.
 * </p>
 * @see PointStyle
 * @author Elisha Peterson
 */
public abstract class AbstractPointStyle {

    private static final StyleMap $KEY_MAP = new StyleMap(
            $KEY_SHAPE, ShapeLibrary.class,
            $KEY_STROKE, BasicStroke.class,
            $KEY_STROKE_COLOR, Color.class,
            $KEY_FILL_COLOR, Color.class,
            $KEY_SIZE, int.class
            );

    /** Determines shape used to indicate the point. */
    ShapeLibrary shape = ShapeLibrary.CIRCLE;
    /** Stroke outline */
    BasicStroke stroke = ShapeRenderer.DEFAULT_STROKE;
    /** Stroke color */
    Color strokeColor = Color.BLACK;
    /** Fill of object */
    Color fillColor = Color.LIGHT_GRAY;
    /** Radius of the point (in pixels) */
    int radius = 5;

    /** Construct with defaults. */
    public AbstractPointStyle() { }
    /** Construct with colors only. */
    public AbstractPointStyle(Color strokeColor, Color fillColor) { this.strokeColor = strokeColor; this.fillColor = fillColor; }
    /** Construct with specified elements. */
    public AbstractPointStyle(ShapeLibrary shape, int radius) { this.shape = shape; this.radius = radius; }
    /** Construct with specified elements. */
    public AbstractPointStyle(ShapeLibrary shape, BasicStroke stroke, Color strokeColor, Color fillColor, int radius) {
        this.shape = shape;
        this.stroke = stroke;
        this.strokeColor = strokeColor;
        this.fillColor = fillColor;
        this.radius = radius;
    }

    public Map<String, Class> getCustomKeys() { return $KEY_MAP; }

    /** @return shape of the point */
    public ShapeLibrary getShape() { return shape; }
    /** @param shape new shape of the point */
    public void setShape(ShapeLibrary shape) { this.shape = shape; }
    /** @return radius of displayed point */
    public int getRadius() { return radius; }
    /** @param radius new point radius */
    public void setRadius(int radius) { this.radius = radius; }
    /** @return fill color */
    public Color getFillColor() { return fillColor; }
    /** @param fillColor the fill color */
    public void setFillColor(Color fillColor) { this.fillColor = fillColor; }
    /** @return stroke for the outline of the point */
    public BasicStroke getStroke() { return stroke; }
    /** @param stroke new stroke for the outline of the point */
    public void setStroke(BasicStroke stroke) { this.stroke = stroke; }
    /** @return color of the outline stroke */
    public Color getStrokeColor() { return strokeColor; }
    /** @param strokeColor new color of the outline stroke */
    public void setStrokeColor(Color strokeColor) { this.strokeColor = strokeColor; }
    /** @return thickness of the outline stroke */
    public float getThickness() { return stroke.getLineWidth(); }
    /** @param width new thickness of the outline stroke */
    public void setThickness(float width) {
        stroke = new BasicStroke(width, stroke.getEndCap(), stroke.getLineJoin(), stroke.getMiterLimit(), stroke.getDashArray(), stroke.getDashPhase());
    }
}
