package com.googlecode.blaisemath.graphics.swing.render;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2024 Elisha Peterson
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

import com.google.common.primitives.Ints;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.graphics.Renderer;
import com.googlecode.blaisemath.style.Styles;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import static com.google.common.primitives.Doubles.max;
import static com.google.common.primitives.Doubles.min;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Draws a shape using a stroke (with thickness) and a fill color.
 *
 * @author Elisha Peterson
 */
public class PathRenderer implements Renderer<Shape, Graphics2D> {

    private static final PathRenderer INST = new PathRenderer();
    
    public static Renderer<Shape, Graphics2D> getInstance() {
        return INST;
    }
    
    @Override
    public String toString() {
        return "PathRenderer";
    }
    
    @Override
    public void render(Shape primitive, AttributeSet style, Graphics2D canvas) {
        if (Styles.hasStroke(style)) {
            canvas.setColor(Styles.strokeColorOf(style));
            canvas.setStroke(Styles.strokeOf(style));
            drawPatched(primitive, canvas);
        }
    }
    
    public static Shape strokedShape(Shape primitive, AttributeSet style) {
        return Styles.hasStroke(style)
                ? new BasicStroke(style.getFloat(Styles.STROKE_WIDTH)).createStrokedShape(primitive)
                : null;
    }

    @Override
    public Rectangle2D boundingBox(Shape primitive, AttributeSet style, @Nullable Graphics2D canvas) {
        Shape sh = strokedShape(primitive, style);
        return sh == null ? null : sh.getBounds2D();
    }

    @Override
    public boolean contains(Point2D point, Shape primitive, AttributeSet style, @Nullable Graphics2D canvas) {
        Shape sh = strokedShape(primitive, style);
        return sh != null && sh.contains(point);
    }

    @Override
    public boolean intersects(Rectangle2D rect, Shape primitive, AttributeSet style, @Nullable Graphics2D canvas) {
        Shape sh = strokedShape(primitive, style);
        return sh != null && sh.intersects(rect);
    }
    
    /** 
     * Method to draw a path shape on the canvas that addresses a performance issue.
     * For dashed lines, it limits render to the canvas clip because of a JDK bug.
     * See https://bugs.openjdk.java.net/browse/JDK-6620013.
     * @param primitive to draw
     * @param canvas target canvas
     */
    public static void drawPatched(Shape primitive, Graphics2D canvas) {
        if (!(canvas.getStroke() instanceof BasicStroke) || ((BasicStroke) canvas.getStroke()).getDashArray() == null) {
            // draw normally
            canvas.draw(primitive);
            return;
        }

        Rectangle r = canvas.getClipBounds();
        // use a large padding because we still want the dashes to be in the right place
        int pad = Ints.max(canvas.getStroke() instanceof BasicStroke ? (int) Math.ceil(((BasicStroke) canvas.getStroke()).getLineWidth()) : 5,
                r.width * 50, r.height * 50);
        Rectangle paddedClip = new Rectangle(r.x - pad, r.y - pad,
                r.width + 2 * pad, r.height + 2 * pad);

        Shape toDraw = intersectPath(paddedClip, primitive);
        if (toDraw != null) {
            canvas.draw(toDraw);
        }
    }

    /**
     * Compute intersection of path with rectangular area.
     * @param rectangle area
     * @param path path
     * @return intersecting shape, or null if none
     */
    private static @Nullable Shape intersectPath(Rectangle2D rectangle, Shape path) {
        Rectangle2D r2 = path.getBounds2D();

        if (r2.getWidth() == 0 && r2.getHeight() == 0) {
            return null;
        } else if (rectangle.contains(r2)) {
            return path;
        }

        if (r2.getWidth() == 0 || r2.getHeight() == 0) {
            // we have a flat shape, so area intersection doesn't work -- this is not precisely correct for multi-part paths, but close enough?
            path = new Line2D.Double(r2.getMinX(), r2.getMinY(), r2.getMaxX(), r2.getMaxY());
        }

        if (path instanceof Line2D.Double) {
            Line2D line = (Line2D) path;
            return line.intersects(rectangle) ? intersect(toDouble(line), rectangle) : null;
        } else {
            Area a = new Area(rectangle);
            a.intersect(new Area(path));
            return a;
        }
    }

    private static Line2D.Double toDouble(Line2D line) {
        return new Line2D.Double(line.getP1(), line.getP2());
    }

    /**
     * Compute the line segment from intersecting given line with rectangle.
     * @param l line to use
     * @param r rectangle
     * @return portion of line inside the rectangle, null if none
     */
    private static Line2D.@Nullable Double intersect(Line2D.Double l, Rectangle2D r) {
        if (r.contains(l.getP1()) && r.contains(l.getP2())) {
            return l;
        }

        // parameterize line as x=x1+t*(x2-x1), y=y1+t*(y2-y1), so line is between 0 and 1
        // then compute t values for lines bounding rectangles, and intersect the three intervals
        // [0,1], [tx1,tx2], and [ty1,ty2]
        double tx1 = l.x1 == l.x2 ? (between(l.x1, r.getMinX(), r.getMaxX()) ? 0 : -1) : (r.getMinX() - l.x1) / (l.x2 - l.x1);
        double tx2 = l.x1 == l.x2 ? (between(l.x1, r.getMinX(), r.getMaxX()) ? 1 : -1) : (r.getMaxX() - l.x1) / (l.x2 - l.x1);
        double ty1 = l.y1 == l.y2 ? (between(l.x1, r.getMinY(), r.getMaxY()) ? 0 : -1) : (r.getMinY() - l.y1) / (l.y2 - l.y1);
        double ty2 = l.y1 == l.y2 ? (between(l.x1, r.getMinY(), r.getMaxY()) ? 1 : -1) : (r.getMaxY() - l.y1) / (l.y2 - l.y1);

        double t0 = max(0, min(tx1, tx2), min(ty1, ty2));
        double t1 = min(1, max(tx1, tx2), max(ty1, ty2));

        return t0 > t1 ? null : new Line2D.Double(l.x1 + t0 * (l.x2 - l.x1), l.y1 + t0 * (l.y2 - l.y1),
                l.x1 + t1 * (l.x2 - l.x1), l.y1 + t1 * (l.y2 - l.y1));
    }

    private static boolean between(double x, double t0, double t1) {
        return x >= t0 ? x <= t1 : x >= t1;
    }

}
