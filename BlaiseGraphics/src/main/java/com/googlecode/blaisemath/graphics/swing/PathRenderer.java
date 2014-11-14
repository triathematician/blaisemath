/*
 * PathRenderer.java
 * Created Jan 9, 2011 (based on much earlier code)
 */

package com.googlecode.blaisemath.graphics.swing;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
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
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
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
    
    public void render(Shape primitive, AttributeSet style, Graphics2D canvas) {
        Color stroke = style.getColor(Styles.STROKE);
        Float strokeWidth = style.getFloat(Styles.STROKE_WIDTH);
        if (stroke != null && strokeWidth != null && strokeWidth > 0) {
            canvas.setColor(stroke);
            canvas.setStroke(Styles.getStroke(style));
            canvas.draw(primitive);
        }
    }
    
    public static Shape strokedShape(Shape primitive, AttributeSet style) {
        Color stroke = style.getColor(Styles.STROKE);
        Float strokeWidth = style.getFloat(Styles.STROKE_WIDTH);
        if (stroke != null && strokeWidth != null && strokeWidth > 0) {
            return new BasicStroke(strokeWidth).createStrokedShape(primitive);
        } else {
            return null;
        }
    }

    public Rectangle2D boundingBox(Shape primitive, AttributeSet style) {
        Shape sh = strokedShape(primitive, style);
        return sh == null ? null : sh.getBounds2D();
    }

    public boolean contains(Shape primitive, AttributeSet style, Point2D point) {
        Shape sh = strokedShape(primitive, style);
        return sh != null && sh.contains(point);
    }

    public boolean intersects(Shape primitive, AttributeSet style, Rectangle2D rect) {
        Shape sh = strokedShape(primitive, style);
        return sh != null && sh.intersects(rect);
    }

}
