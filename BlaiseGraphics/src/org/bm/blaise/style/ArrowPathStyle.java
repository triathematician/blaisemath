/*
 * ArrowPathStyle.java
 * Created Jan 12, 2011
 */

package org.bm.blaise.style;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;

/**
 * Draws a color on the screen, with an arrow at the endpoint.
 * @author Elisha
 */
public class ArrowPathStyle extends BasicPathStyle {

    public ArrowPathStyle() { super(); }
    public ArrowPathStyle(Color stroke) { super(stroke); }
    public ArrowPathStyle(Color stroke, float thickness) { super(stroke, thickness); }
    public ArrowPathStyle(BasicPathStyle rend) { super(rend.getColor(), rend.getWidth()); }

    /** Returns arrow between two points */
    public GeneralPath getArrowPath(float x1, float y1, float x2, float y2) {
        float dx = x2-x1, dy = y2-y1;
        float dsq = (float) Math.sqrt(dx*dx+dy*dy);
        double dth = Math.sqrt(thickness)*3;
        dx *= dth/dsq;
        dy *= dth/dsq;
        float adx = -dy, ady = dx;
        GeneralPath gp = new GeneralPath();
        gp.moveTo(x2-1.5f*dx, y2-1.5f*dy);
        gp.lineTo(x2-2f*dx+1f*adx, y2-2f*dy+1f*ady);
        gp.lineTo(x2, y2);
        gp.lineTo(x2-2f*dx-1f*adx, y2-2f*dy-1f*ady);
        gp.closePath();
        return gp;
    }

    @Override
    public void draw(Shape s, Graphics2D canvas, VisibilityKey visibility) {
        super.draw(s, canvas, visibility);
        if(thickness <= 0f && color != null)
            return;
        if (s instanceof Line2D.Double || s instanceof GeneralPath) {
            GeneralPath shape = new GeneralPath();
            if (s instanceof Line2D.Double) {
                Line2D.Double line = (Line2D.Double) s;
                shape = getArrowPath((float) line.x1, (float) line.y1, (float) line.x2, (float) line.y2);
            } else if (s instanceof GeneralPath) {
                GeneralPath gp = (GeneralPath) s;
                PathIterator pi = gp.getPathIterator(null);
                float[] cur = new float[6], last = new float[6];
                while (!pi.isDone()) {
                    int type = pi.currentSegment(cur);
                    if (type == PathIterator.SEG_LINETO)
                        shape.append(getArrowPath(last[0], last[1], cur[0], cur[1]), false);
                    System.arraycopy(cur, 0, last, 0, 6);
                    pi.next();
                }
            }
//            Color cAlpha = new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()/2);
            canvas.setColor(color);
            canvas.fill(shape);
            canvas.setStroke(new BasicStroke(thickness));
            canvas.draw(shape);
//            canvas.setColor(color);
//            canvas.setColor(new BasicStroke(thickness));
//            canvas.draw(shape);
        }
    }

}
