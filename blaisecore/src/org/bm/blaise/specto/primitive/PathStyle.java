/**
 * PathStyle.java
 * Created on Aug 4, 2009
 */

package org.bm.blaise.specto.primitive;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;

/**
 * <p>
 *   <code>PathStyle</code> represents any style that is applied to paths or lines.
 *   The default options are the <code>Stroke</code> object and the color... I may
 *   consider changing this to alter the stroke directly via patterns here such as
 *   width, dashes, etc. Alternately, I may design a "builder" to construct various
 *   stroke types.
 * </p>
 *
 * @author Elisha Peterson
 */
public class PathStyle implements PrimitiveStyle<Shape> {

    //
    // PROPERTIES
    //

    /** Stroke for the stroke. */
    Stroke stroke = DEFAULT_STROKE;

    /** Color of stroke. */
    Color strokeColor = Color.BLACK;

    //
    // CONSTRUCTORS
    //

    /** Default constructor. */
    public PathStyle() {
    }

    public PathStyle(Color color) {
        this.strokeColor = color;
    }

    /** Construct with parameters. */
    public PathStyle(Color color, float width){
        this.strokeColor = color;
        this.stroke = new BasicStroke(width);
    }

    /** Construct with parameters. */
    public PathStyle(Color color, Stroke stroke){
        this.strokeColor = color;
        this.stroke = stroke;
    }

    //
    // UTILITIES
    //

    @Override
    public String toString() {
        return "PathStyle [wid=" + getThickness() + "]";
    }

    //
    // PATTERNS
    //

    public Stroke getStroke() {
        return stroke;
    }

    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }

    public float getThickness() {
        return (stroke instanceof BasicStroke) ? ((BasicStroke)stroke).getLineWidth() : -1;
    }

    public void setThickness(float width) {
        if (stroke instanceof BasicStroke) {
            BasicStroke bs = (BasicStroke) stroke;
            stroke = new BasicStroke(width, bs.getEndCap(), bs.getLineJoin(), bs.getMiterLimit(), bs.getDashArray(), bs.getDashPhase());
        }
    }

    public Color getColor() {
        return strokeColor;
    }

    public void setColor(Color color) {
        this.strokeColor = color;
    }

    //
    // IMPLEMENTATION
    //

    public void draw(Shape path, Graphics2D canvas) {
        canvas.setStroke(stroke);
        canvas.setColor(strokeColor);
        canvas.draw(path);
    }

    public void draw(Shape[] paths, Graphics2D canvas) {
        for (Shape p : paths) {
            draw(p, canvas);
        }
    }
}
