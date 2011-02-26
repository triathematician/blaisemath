/*
 * StrokeRenderer.java
 * Created Jan 12, 2011
 */

package graphics.renderer;

import graphics.GraphicVisibility;
import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;

/**
 * Draws a point on the screen.
 * @author Elisha
 */
public class BasicStrokeRenderer implements ShapeRenderer {

    Color stroke;
    float thickness = 1f;

    public BasicStrokeRenderer(Color stroke) {
        this.stroke = stroke;
    }

    public BasicStrokeRenderer(Color stroke, float thickness) {
        this.stroke = stroke;
        this.thickness = thickness;
    }

    public Color getStroke() {
        return stroke;
    }

    public void setStroke(Color stroke) {
        this.stroke = stroke;
    }

    public float getThickness() {
        return thickness;
    }

    public void setThickness(float thickness) {
        this.thickness = thickness;
    }

    /** test case to remove later */
    boolean fancy = true;

    /** Retrieves fancy shape between points */
    public GeneralPath getFancy(float x1, float y1, float x2, float y2) {
        float dx = x2-x1, dy = y2-y1;
        float dsq = (float) Math.sqrt(dx*dx+dy*dy);
        float adx = -dy*thickness/dsq, ady = dx*thickness/dsq;
        GeneralPath gp = new GeneralPath();
        gp.moveTo(x1-adx, y1-ady); gp.lineTo(x1+adx, y1+ady);
        gp.curveTo(x1+.25f*dx+.25f*adx, y1+.25f*dy+.25f*ady,
                x1+.75f*dx+.25f*adx, y1+.75f*dy+.25f*ady,
                x2+.5f*adx, y2+.5f*ady); gp.lineTo(x2-.5f*adx, y2-.5f*ady);
        gp.curveTo(x1+.75f*dx-.25f*adx, y1+.75f*dy-.25f*ady,
                x1+.25f*dx-.25f*adx, y1+.25f*dy-.25f*ady,
                x1-adx, y1-ady); gp.closePath();
        return gp;
    }

    public void draw(Shape s, Graphics2D canvas, GraphicVisibility visibility) {
        if(thickness <= 0f && stroke != null)
            return;
        if (fancy && (s instanceof Line2D.Double || s instanceof GeneralPath)) {
            GeneralPath shape = new GeneralPath();
            if (s instanceof Line2D.Double) {
                Line2D.Double line = (Line2D.Double) s;
                shape = getFancy((float) line.x1, (float) line.y1, (float) line.x2, (float) line.y2);
            } else if (s instanceof GeneralPath) {
                GeneralPath gp = (GeneralPath) s;
                PathIterator pi = gp.getPathIterator(null);
                float[] cur = new float[6], last = new float[6];
                while (!pi.isDone()) {
                    int type = pi.currentSegment(cur);
                    if (type == PathIterator.SEG_LINETO)
                        shape.append(getFancy(last[0], last[1], cur[0], cur[1]), false);
                    System.arraycopy(cur, 0, last, 0, 6);
                    pi.next();
                }
            }
            Color cAlpha = new Color(stroke.getRed(), stroke.getGreen(), stroke.getBlue(), stroke.getAlpha()/2);
            canvas.setColor(cAlpha);
            canvas.fill(shape);
            canvas.setColor(stroke);
            canvas.draw(shape);
        } else {
            if (thickness != 1f)
                canvas.setStroke(new BasicStroke(thickness));

            canvas.setColor(visibility == GraphicVisibility.Highlight  ? StyleUtils.lighterThan(stroke) : stroke);
            canvas.draw(s);

            if (thickness != 1f)
                canvas.setStroke(StyleUtils.DEFAULT_STROKE);
        }
    }

    public void drawAll(Iterable<Shape> primitives, Graphics2D canvas, GraphicVisibility visibility) {
        if (thickness != 1f)
            canvas.setStroke(new BasicStroke(thickness));

        if (stroke != null) {
            canvas.setColor(visibility == GraphicVisibility.Highlight  ? StyleUtils.lighterThan(stroke) : stroke);
            for (Shape s : primitives)
                canvas.draw(s);
        }

        if (thickness != 1f)
            canvas.setStroke(StyleUtils.DEFAULT_STROKE);
    }

}
