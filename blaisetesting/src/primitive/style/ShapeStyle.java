/**
 * ShapeStyle.java
 * Created on Sep 6, 2009
 */

package primitive.style;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;

/**
 * <p>
 *   <code>ShapeStyle</code> represents a shape, with style determined by both
 *   a fill color and a stroke/stroke color.
 * </p>
 *
 * @author Elisha Peterson
 */
public class ShapeStyle extends AbstractPathStyle implements PrimitiveStyle<Shape> {

    /** Fill of object */
    Color fillColor = Color.LIGHT_GRAY;
    /** Opacity of fill */
    float opacity = .5f;

    /** Construct with defaults. */
    public ShapeStyle() {}
    /** Construct with stroke only. Sets fill color to null so that it is not displayed. */
    public ShapeStyle(PathStyle style) { this(style, null); }
    /** Construct with stroke and fill color */
    public ShapeStyle(PathStyle style, Color fillColor) { super(style.strokeColor, style.stroke); this.fillColor = fillColor; }
    /** Construct with specified parameters. */
    public ShapeStyle(Color strokeColor, Color fillColor) { super(strokeColor); this.fillColor = fillColor; }
    /** Construct with specified parameters. */
    public ShapeStyle(Color strokeColor, BasicStroke stroke, Color fillColor, float opacity) { super(strokeColor, stroke); this.fillColor = fillColor; this.opacity = opacity; }

    @Override
    public String toString() {
        return "ShapeStyle [stroke=" + strokeColor + ", fill=" + fillColor + ", opacity=" + opacity + "]";
    }

    public Class<? extends Shape> getTargetType() {
        return Shape.class;
    }

    /** @return color of fill */
    public Color getFillColor() { return fillColor; }
    /** Sets color of fill */
    public void setFillColor(Color fillColor) { this.fillColor = fillColor; }
    /** @return opacity of fill */
    public float getFillOpacity() { return opacity; }
    /** Sets opacity of fill */
    public void setFillOpacity(float opacity) { this.opacity = opacity; }

    //
    // GRAPHICS METHODS
    //

    public void draw(Graphics2D canvas, Shape sh) {
        if (fillColor != null) {
            canvas.setColor(fillColor);
            canvas.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            canvas.fill(sh);
            canvas.setComposite(AlphaComposite.SrcOver);
        }
        if (stroke != null && strokeColor != null) {
            canvas.setStroke(stroke);
            canvas.setColor(strokeColor);
            canvas.draw(sh);
        }
    }

    public void draw(Graphics2D canvas, Shape[] shapes) {
        if (stroke != null && strokeColor != null) {
            canvas.setStroke(stroke);
            if (fillColor != null) {
                // both fill and stroke
                for (Shape sh: shapes) {
                    canvas.setColor(fillColor);
                    canvas.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
                    canvas.fill(sh);
                    canvas.setComposite(AlphaComposite.SrcOver);
                    canvas.setStroke(stroke);
                    canvas.setColor(strokeColor);
                    canvas.draw(sh);
                }
            } else {
                // only stroke shapes
                canvas.setColor(strokeColor);
                for (Shape sh : shapes)
                    canvas.draw(sh);
            }
        } else if (fillColor != null) {
            // only fill shapes
            canvas.setColor(fillColor);
            canvas.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            for (Shape sh: shapes)
                canvas.fill(sh);
            canvas.setComposite(AlphaComposite.SrcOver);
        }
    }

    public boolean contained(Shape primitive, Graphics2D canvas, Point point) {
        return primitive.contains(point);
    }

    public int containedInArray(Shape[] primitives, Graphics2D canvas, Point point) {
        for (int i = 0; i < primitives.length; i++)
            if (contained(primitives[i], canvas, point))
                return i;
        return -1;
    }


}
