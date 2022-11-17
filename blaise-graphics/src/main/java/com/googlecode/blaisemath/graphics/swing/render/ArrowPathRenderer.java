package com.googlecode.blaisemath.graphics.swing.render;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2022 Elisha Peterson
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

import com.googlecode.blaisemath.primitive.ArrowLocation;
import static com.google.common.base.Preconditions.checkNotNull;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Styles;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Draws a stroke on the screen, with an arrow at the endpoint.
 * 
 * @author Elisha Peterson
 */
public class ArrowPathRenderer extends PathRenderer {

    private static final Logger LOG = Logger.getLogger(ArrowPathRenderer.class.getName());

    protected ArrowLocation arrowLoc = ArrowLocation.END;

    /**
     * Initialize renderer w/ default arrow location (end).
     */
    public ArrowPathRenderer() {
    }
    
    /**
     * Initialize renderer w/ specified arrow location.
     * @param dir arrow location(s)
     */
    public ArrowPathRenderer(ArrowLocation dir) {
        checkNotNull(dir);
        this.arrowLoc = dir;
    }
    
    /**
     * Get instance of the arrow renderer.
     * @return instance
     */
    public static ArrowPathRenderer getInstance() {
        return new ArrowPathRenderer();
    }
    
    @Override
    public String toString() {
        return String.format("ArrowPathRenderer[arrowLoc=%s]", arrowLoc);
    }
    
    public ArrowPathRenderer arrowLocation(ArrowLocation loc) {
        setArrowLocation(loc);
        return this;
    }
    
    
    //region PROPERTIES
    
    public ArrowLocation getArrowLocation() {
        return arrowLoc;
    }
    
    public void setArrowLocation(ArrowLocation loc) {
        checkNotNull(loc);
        if (this.arrowLoc != loc) {
            this.arrowLoc = loc;
        }
    }
    
    //endregion
    
    
    @Override
    public void render(Shape s, AttributeSet style, Graphics2D canvas) {
        super.render(s, style, canvas);
        
        Color stroke = style.getColor(Styles.STROKE);
        Float strokeWidth = style.getFloat(Styles.STROKE_WIDTH);
        
        // can only draw if stroke is appropriate
        if (stroke == null || strokeWidth == null || strokeWidth <= 0) {
            return;
        }
        
        // arrow heads can only be drawn on certain shapes
        if (!(s instanceof Line2D || s instanceof GeneralPath)) {
            LOG.log(Level.WARNING, "Unable to draw arrowheads on this shape: {0}", s);
            return;
        }

        // create and draw arrowhead shape(s) at end of path
        GeneralPath arrowShapes = arrowShapes(s, arrowLoc, strokeWidth);
        canvas.setColor(stroke);
        canvas.fill(arrowShapes);
        canvas.setStroke(new BasicStroke(strokeWidth));
        PathRenderer.drawPatched(arrowShapes, canvas);
    }

    public static GeneralPath arrowShapes(Shape s, ArrowLocation loc, float strokeWidth) {
        return s instanceof Line2D ? lineArrowShapes((Line2D) s, loc, strokeWidth) : pathArrowShapes((GeneralPath) s, loc, strokeWidth);
    }

    private static GeneralPath lineArrowShapes(Line2D line, ArrowLocation loc, float strokeWidth) {
        GeneralPath res = new GeneralPath();
        if (loc == ArrowLocation.END || loc == ArrowLocation.BOTH) {
            res.append(createArrowhead((float) line.getX1(), (float) line.getY1(),
                    (float) line.getX2(), (float) line.getY2(), strokeWidth), false);
        }
        if (loc == ArrowLocation.START || loc == ArrowLocation.BOTH) {
            res.append(createArrowhead((float) line.getX2(), (float) line.getY2(),
                    (float) line.getX1(), (float) line.getY1(), strokeWidth), false);
        }
        return res;
    }

    private static GeneralPath pathArrowShapes(GeneralPath path, ArrowLocation loc, float strokeWidth) {
        GeneralPath res = new GeneralPath();
        PathIterator pi = path.getPathIterator(null);
        float[] cur = new float[6], last = new float[6];
        while (!pi.isDone()) {
            int type = pi.currentSegment(cur);
            if (type == PathIterator.SEG_LINETO) {
                if (loc == ArrowLocation.END || loc == ArrowLocation.BOTH) {
                    res.append(createArrowhead(last[0], last[1], cur[0], cur[1], strokeWidth), false);
                }
                if (loc == ArrowLocation.START || loc == ArrowLocation.BOTH) {
                    res.append(createArrowhead(cur[0], cur[1], last[0], last[1], strokeWidth), false);
                }
            }
            System.arraycopy(cur, 0, last, 0, 6);
            pi.next();
        }
        return res;
    }
    
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
