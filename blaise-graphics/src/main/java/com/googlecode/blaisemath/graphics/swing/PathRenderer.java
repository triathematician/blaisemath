/*
 * PathRenderer.java
 * Created Jan 9, 2011 (based on much earlier code)
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

import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Renderer;
import com.googlecode.blaisemath.style.Styles;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Draws a shape using a stroke (with thickness) and a fill color.
 *
 * @author Elisha
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
    public Rectangle2D boundingBox(Shape primitive, AttributeSet style) {
        Shape sh = strokedShape(primitive, style);
        return sh == null ? null : sh.getBounds2D();
    }

    @Override
    public boolean contains(Shape primitive, AttributeSet style, Point2D point) {
        Shape sh = strokedShape(primitive, style);
        return sh != null && sh.contains(point);
    }

    @Override
    public boolean intersects(Shape primitive, AttributeSet style, Rectangle2D rect) {
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
        if (!(canvas.getStroke() instanceof BasicStroke)
                || ((BasicStroke) canvas.getStroke()).getDashArray() == null) {
            canvas.draw(primitive);
            return;
        }
        
        Rectangle r = canvas.getClipBounds();
        Rectangle2D r2 = primitive.getBounds2D();
        if (r.contains(r2)) {
            canvas.draw(primitive);
        } else {
            int pad = canvas.getStroke() instanceof BasicStroke
                    ? (int) Math.ceil(((BasicStroke) canvas.getStroke()).getLineWidth())
                    : 5;
            Rectangle paddedClip = new Rectangle(r.x - pad, r.y - pad,
                    r.width + 2 * pad, r.height + 2 * pad);
            Area a = new Area(paddedClip);
            a.intersect(new Area(primitive));
            canvas.draw(a);
        }
    }

}
