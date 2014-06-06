/*
 * PathStyleArrow.java
 * Created Jan 12, 2011
 */

package org.blaise.style;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
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

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;

/**
 * Draws a stroke on the screen, with an arrow at the endpoint.
 * 
 * @author Elisha
 */
public class PathStyleArrow extends PathStyleBasic {

    public PathStyleArrow() {
    }
    
    @Override
    public String toString() {
        return String.format("PathStyleArrow[stroke=%s, strokeWidth=%.1f]", 
                stroke, strokeWidth);
    }
    
    @Override
    public void draw(Shape s, Graphics2D canvas, StyleHintSet visibility) {
        super.draw(s, canvas, visibility);
        if (strokeWidth <= 0f && stroke != null) {
            return;
        }
        
        // arrow heads can only be drawn on certain shapes
        if (!(s instanceof Line2D || s instanceof GeneralPath)) {
            return;
        }

        // create arrowhead shape(s) at end of path
        GeneralPath shape = new GeneralPath();
        if (s instanceof Line2D.Double) {
            Line2D.Double line = (Line2D.Double) s;
            shape = createArrowhead((float) line.x1, (float) line.y1, (float) line.x2, (float) line.y2, strokeWidth);
        } else if (s instanceof GeneralPath) {
            GeneralPath gp = (GeneralPath) s;
            PathIterator pi = gp.getPathIterator(null);
            float[] cur = new float[6], last = new float[6];
            while (!pi.isDone()) {
                int type = pi.currentSegment(cur);
                if (type == PathIterator.SEG_LINETO) {
                    shape.append(createArrowhead(last[0], last[1], cur[0], cur[1], strokeWidth), false);
                }
                System.arraycopy(cur, 0, last, 0, 6);
                pi.next();
            }
        }
        
        // draw filled arrowhead on top of path
        canvas.setColor(stroke);
        canvas.fill(shape);
        canvas.setStroke(new BasicStroke(strokeWidth));
        canvas.draw(shape);
    }

    
    //
    // STATIC SHAPE METHOD
    //
    
    /** 
     * Returns path representing an arrow from one point to another.
     * @param x1 first x-coord
     * @param y1 first y-coord
     * @param x2 second x-coord
     * @param y2 second y-coord
     * @param thickness width of resulting line (determines size of arrowhead)
     * @return created path
     */
    public static GeneralPath createArrowhead(float x1, float y1, float x2, float y2, float thickness) {
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
    
}
