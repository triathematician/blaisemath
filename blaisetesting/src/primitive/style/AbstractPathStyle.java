/*
 * AbstractPathStyle.java
 * Created May 13,2010
 */

package primitive.style;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

/**
 * Contains basic feature support for a stroke and a color setting, for use with
 * different classes that implement a stroke style, possibly for different primitives.
 *
 * @see PathStyle
 * @see PointPathStyle
 * @author Elisha Peterson
 */

public abstract class AbstractPathStyle {

    protected BasicStroke stroke = PrimitiveStyle.DEFAULT_STROKE;
    protected Color strokeColor = Color.BLACK;

    /** Construct with default stroke and color black */
    public AbstractPathStyle() {}
    /** Construct with specific color. */
    public AbstractPathStyle(Color color) { this.strokeColor = color; }
    /** Construct with specific color/width. */
    public AbstractPathStyle(Color color, float width){ this.strokeColor = color; this.stroke = new BasicStroke(width); }
    /** Construct with specified color/stroke. */
    public AbstractPathStyle(Color color, BasicStroke stroke){ this.strokeColor = color; this.stroke = stroke; }
    
    /** @return stroke */
    public Stroke getStroke() { return stroke; }
    /** @param stroke new stroke */
    public void setStroke(BasicStroke stroke) { this.stroke = stroke; }
    /** @return thickness of current stroke */
    public float getThickness() { return stroke == null ? 0 : stroke.getLineWidth(); }
    /** @param width new thickness for current stroke */
    public void setThickness(float width) {
        stroke = stroke == null ? new BasicStroke(width)
                : new BasicStroke(width, stroke.getEndCap(), stroke.getLineJoin(), stroke.getMiterLimit(), stroke.getDashArray(), stroke.getDashPhase());
    }
    /** @return color of stroke */
    public Color getStrokeColor() { return strokeColor; }
    /** @param color new color for current stroke */
    public void setStrokeColor(Color color) { this.strokeColor = color; }


    /** Draws a single path via a shape */
    public void drawPath(Graphics2D canvas, Shape path) {
        canvas.setColor(strokeColor);
        canvas.setStroke(stroke);
        canvas.draw(path);
    }
    /** Draws several shapes */
    public void drawPaths(Graphics2D canvas, Shape[] paths) {
        canvas.setColor(strokeColor);
        canvas.setStroke(stroke);
        for (Shape p : paths)
            canvas.draw(p);

    }
    /** Draws a single path via an array of points */
    public void drawPath(Graphics2D canvas, Point2D.Double[] path) {
        canvas.setColor(strokeColor);
        canvas.setStroke(stroke);
        GeneralPath gp = new GeneralPath();
        gp.moveTo((float)path[0].x, (float)path[0].y);
        for (int i = 0; i < path.length; i++)
            gp.lineTo((float)path[i].x, (float)path[i].y);
        canvas.draw(gp);
    }
    /** Draws several paths */
    public void drawPaths(Graphics2D canvas, Point2D.Double[][] paths) {
        canvas.setColor(strokeColor);
        canvas.setStroke(stroke);
        GeneralPath gp = new GeneralPath();
        for (Point2D.Double[] path : paths) {
            gp.moveTo((float)path[0].x, (float)path[0].y);
            for (int i = 0; i < path.length; i++)
                gp.lineTo((float)path[i].x, (float)path[i].y);
        }
        canvas.draw(gp);
    }


}
