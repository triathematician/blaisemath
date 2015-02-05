/*
 * PathStyleTapered.java
 * Created Jan 12, 2011
 */

package com.googlecode.blaisemath.graphics.swing;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2015 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Renderer;
import com.googlecode.blaisemath.style.Styles;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;

/**
 * Draws a path on the screen using a fancy tapered-outline style.
 * 
 * @author Elisha
 */
public class TaperedPathRenderer extends PathRenderer {

    private static final TaperedPathRenderer INST = new TaperedPathRenderer();
    
    public static Renderer<Shape, Graphics2D> getInstance() {
        return INST;
    }

    public TaperedPathRenderer() {
    }
    
    @Override
    public String toString() {
        return "TaperedPathRenderer";
    }

    @Override
    public void render(Shape s, AttributeSet style, Graphics2D canvas) {
        Color stroke = style.getColor(Styles.STROKE);
        Float strokeWidth = style.getFloat(Styles.STROKE_WIDTH, 1f);
        
        if(strokeWidth <= 0f || stroke == null) {
            return;
        }
        if (s instanceof Line2D.Double || s instanceof GeneralPath) {
            s = createBezierShape(s, strokeWidth);
        }
        Color cAlpha = new Color(stroke.getRed(), stroke.getGreen(), stroke.getBlue(), stroke.getAlpha()/2);
        canvas.setColor(cAlpha);
        canvas.fill(s);
        canvas.setColor(stroke);
        canvas.draw(s);
    }

    
    //
    // STATIC SHAPE METHODS
    //
    
    public static Shape createBezierShape(Shape s, float strokeWidth) {
        if (s instanceof Line2D.Double) {
            Line2D.Double line = (Line2D.Double) s;
            return createBezierShape((float) line.x1, (float) line.y1, (float) line.x2, (float) line.y2, strokeWidth);
        } else if (s instanceof GeneralPath) {
            GeneralPath shape = new GeneralPath();
            GeneralPath gp = (GeneralPath) s;
            PathIterator pi = gp.getPathIterator(null);
            float[] cur = new float[6], last = new float[6];
            while (!pi.isDone()) {
                int type = pi.currentSegment(cur);
                if (type == PathIterator.SEG_LINETO) {
                    shape.append(createBezierShape(last[0], last[1], cur[0], cur[1], strokeWidth), false);
                }
                System.arraycopy(cur, 0, last, 0, 6);
                pi.next();
            }
            return shape;
        } else {
            return s;
        }
    }
    
    /** 
     * Returns path representing a "fancy shape" between points, using Bezier curves.
     * @param x1 first x-coord
     * @param y1 first y-coord
     * @param x2 second x-coord
     * @param y2 second y-coord
     * @param pathWidth width of resulting line (determines size of arrowhead)
     * @return created shape
     */
    public static GeneralPath createBezierShape(float x1, float y1, float x2, float y2, float pathWidth) {
        float dx = x2-x1, dy = y2-y1;
        float dsq = (float) Math.sqrt(dx*dx+dy*dy);
        float adx = -dy*pathWidth/dsq, ady = dx*pathWidth/dsq;
        GeneralPath gp = new GeneralPath();
        gp.moveTo(x1-adx, y1-ady); 
        gp.lineTo(x1+adx, y1+ady);
        gp.curveTo(x1+.25f*dx+.25f*adx, y1+.25f*dy+.25f*ady,
                x1+.75f*dx+.25f*adx, y1+.75f*dy+.25f*ady,
                x2+.5f*adx, y2+.5f*ady); gp.lineTo(x2-.5f*adx, y2-.5f*ady);
        gp.curveTo(x1+.75f*dx-.25f*adx, y1+.75f*dy-.25f*ady,
                x1+.25f*dx-.25f*adx, y1+.25f*dy-.25f*ady,
                x1-adx, y1-ady); gp.closePath();
        return gp;
    }
    
}
