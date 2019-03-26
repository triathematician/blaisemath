/*
 * MarkerRendererToClip.java
 * Created Oct 1, 2011
 */
package com.googlecode.blaisemath.graphics.swing;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2019 Elisha Peterson
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

import static com.google.common.base.Preconditions.checkNotNull;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.util.OrientedPoint2D;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;

/**
 * Draws a point along with a ray from the point to the outer edge of the graphics canvas.
 *
 * @author elisha
 */
public class MarkerRendererToClip extends MarkerRenderer {

    /** Line style for drawing the ray */
    protected PathRenderer rayRenderer = ArrowPathRenderer.getInstance();
    /** Whether to extend in both directions, or just forward */
    protected boolean extendBothDirections = false;

    /** Construct the style */
    public MarkerRendererToClip() {
    }

    
    //<editor-fold defaultstate="collapsed" desc="BUILDER PATTERNS">

    /** 
     * Sets ray style and returns pointer to this object. 
     * @param rayStyle the style for rays
     * @return this
     */
    public MarkerRendererToClip rayStyle(PathRenderer rayStyle) {
        setRayRenderer(rayStyle);
        return this;
    }

    /** 
     * Sets extension rule and returns pointer to this object.
     * @param extendBoth whether to extend line in both directions
     * @return this
     */
    public MarkerRendererToClip extendBothDirections(boolean extendBoth) {
        setExtendBothDirections(extendBoth);
        return this;
    }

    // </editor-fold>

    @Override
    public String toString() {
        return String.format("PointStyleInfinite[rayStyle=%s, extendBoth=%s]", 
                rayRenderer, extendBothDirections);
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //

    public PathRenderer getRayRenderer() {
        return rayRenderer;
    }

    public void setRayRenderer(PathRenderer rayStyle) {
        this.rayRenderer = checkNotNull(rayStyle);
    }

    public boolean isExtendBothDirections() {
        return extendBothDirections;
    }

    public void setExtendBothDirections(boolean extendBoth) {
        this.extendBothDirections = extendBoth;
    }
    
    //</editor-fold>
    
    
    @Override
    public void render(Point2D p, AttributeSet style, Graphics2D canvas) {
        double angle = p instanceof OrientedPoint2D ? ((OrientedPoint2D)p).angle : 0;
        Point2D p2 = new Point2D.Double(p.getX() + Math.cos(angle), p.getY() + Math.sin(angle));
        Point2D endpt = boundaryHit(p, p2, canvas.getClipBounds());
        if (extendBothDirections) {
            Point2D endpt1 = boundaryHit(p2, p, canvas.getClipBounds());
            rayRenderer.render(new Line2D.Double(endpt1, endpt), style, canvas);
        } else {
            rayRenderer.render(new Line2D.Double(p, endpt), style, canvas);
        }
        super.render(p, style, canvas);
    }


    //
    // UTILITY
    //

    /**
     * Returns points at which the ray beginning at p1 and passing through p2 intersects the boundary of the window.
     * @param p1p first point
     * @param p2p second point
     * @param bounds the window boundaries
     * @return the point on the boundary
     */
    public static Point2D.Double boundaryHit(Point2D p1p, Point2D p2p, RectangularShape bounds) {
        Point2D.Double p1 = new Point2D.Double(p1p.getX(), p1p.getY());
        Point2D.Double p2 = new Point2D.Double(p2p.getX(), p2p.getY());
        if (p2.x > p1.x && p1.x <= bounds.getMaxX()) {
            // line goes to the right
            double slope = (p2.y - p1.y) / (p2.x - p1.x);
            double yRight = slope * (bounds.getMaxX() - p1.x) + p1.y;
            if (yRight <= bounds.getMaxY() && yRight >= bounds.getMinY()) {
                // point is on the right
                return new Point2D.Double(bounds.getMaxX(), yRight);
            } else if (p2.y > p1.y && p1.y <= bounds.getMaxY()) {
                // line goes up
                return new Point2D.Double((bounds.getMaxY() - p1.y) / slope + p1.x, bounds.getMaxY());
            } else if (p1.y > p2.y && p1.y >= bounds.getMinY()) {
                // line goes down
                return new Point2D.Double((bounds.getMinY() - p1.y) / slope + p1.x, bounds.getMinY());
            }
        } else if (p2.x < p1.x && p1.x >= bounds.getMinX()) {
            // line goes to the left
            double slope = (p2.y - p1.y) / (p2.x - p1.x);
            double yLeft = slope * (bounds.getMinX() - p1.x) + p1.y;
            if (yLeft <= bounds.getMaxY() && yLeft >= bounds.getMinY()) {
                // point is on the right
                return new Point2D.Double(bounds.getMinX(), yLeft);
            } else if (p2.y > p1.y && p1.y <= bounds.getMaxY()) {
                // line goes up
                return new Point2D.Double((bounds.getMaxY() - p1.y) / slope + p1.x, bounds.getMaxY());
            } else if (p1.y > p2.y && p1.y >= bounds.getMinY()) {
                // line goes down
                return new Point2D.Double((bounds.getMinY() - p1.y) / slope + p1.x, bounds.getMinY());
            }
        } else if (p1.x == p2.x) {
            // line is vertical
            if (p2.y < p1.y && p1.y >= bounds.getMinY()) {
                // line goes up
                return new Point2D.Double(p1.x, bounds.getMinY());
            } else if (p1.y <= bounds.getMaxY()) {
                return new Point2D.Double(p1.x, bounds.getMaxY());
            }
        }
        return new Point2D.Double(p2.x, p2.y);
    }


}
