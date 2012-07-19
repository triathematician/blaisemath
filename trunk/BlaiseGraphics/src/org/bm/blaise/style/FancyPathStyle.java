/*
 * FancyPathStyle.java
 * Created Jan 12, 2011
 */

package org.bm.blaise.style;

import java.awt.Shape;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;

/**
 * Draws a color on the screen, using a fancy outlined style.
 * @author Elisha
 */
public class FancyPathStyle extends BasicPathStyle {

    public FancyPathStyle() {
    }

    public FancyPathStyle(Color stroke) {
        super(stroke);
    }

    public FancyPathStyle(Color stroke, float thickness) {
        super(stroke, thickness);
    }

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

    @Override
    public void draw(Shape s, Graphics2D canvas, VisibilityKey visibility) {
        if(thickness <= 0f && color != null)
            return;
        if (s instanceof Line2D.Double || s instanceof GeneralPath) {
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
            Color cAlpha = new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()/2);
            canvas.setColor(cAlpha);
            canvas.fill(shape);
            canvas.setColor(color);
            canvas.draw(shape);
        }
    }

}
