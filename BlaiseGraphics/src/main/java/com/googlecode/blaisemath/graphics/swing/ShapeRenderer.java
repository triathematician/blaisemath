/*
 * ShapeRenderer.java
 * Created Jan 9, 2011 (based on much earlier code)
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
import com.googlecode.blaisemath.util.Colors;
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
public class ShapeRenderer implements Renderer<Shape, Graphics2D> {  

    private static final ShapeRenderer INST = new ShapeRenderer();
    
    public static ShapeRenderer getInstance() {
        return INST;
    }
    
    private static Color fillColor(AttributeSet style) {
        Color fill = style.getColor(Styles.FILL);
        if (style.contains(Styles.OPACITY)) {
            fill = Colors.alpha(fill, (int) (255*style.getFloat(Styles.OPACITY, 1f)));
        }
        if (style.contains(Styles.FILL_OPACITY)) {
            fill = Colors.alpha(fill, (int) (255*style.getFloat(Styles.FILL_OPACITY, 1f)));
        }
        return fill;
    }
    
    private static Color strokeColor(AttributeSet style) {
        Color fill = style.getColor(Styles.STROKE);
        if (style.contains(Styles.OPACITY)) {
            fill = Colors.alpha(fill, (int) (255*style.getFloat(Styles.OPACITY, 1f)));
        }
        if (style.contains(Styles.STROKE_OPACITY)) {
            fill = Colors.alpha(fill, (int) (255*style.getFloat(Styles.STROKE_OPACITY, 1f)));
        }
        return fill;
    }
    
    @Override
    public void render(Shape primitive, AttributeSet style, Graphics2D canvas) {
        boolean filled = style.contains(Styles.FILL) && style.get(Styles.FILL) != null;
        if (filled) {
            canvas.setColor(fillColor(style));
            canvas.fill(primitive);
        }
        
        Color stroke = strokeColor(style);
        Float strokeWidth = style.getFloat(Styles.STROKE_WIDTH);
        if (stroke != null && strokeWidth != null && strokeWidth > 0) {
            canvas.setColor(stroke);
            canvas.setStroke(new BasicStroke(strokeWidth));
            canvas.draw(primitive);
        }
    }

    @Override
    public Rectangle2D boundingBox(Shape primitive, AttributeSet style) {
        boolean filled = style.contains(Styles.FILL) && style.get(Styles.FILL) != null;
        Shape sh = PathRenderer.strokedShape(primitive, style);
        if (filled && sh != null) {
            return primitive.getBounds2D().createUnion(sh.getBounds2D());
        } else if (filled) {
            return primitive.getBounds2D();
        } else if (sh != null) {
            return sh.getBounds2D();
        } else {
            return null;
        }
    }

    @Override
    public boolean contains(Shape primitive, AttributeSet style, Point2D point) {
        boolean filled = style.contains(Styles.FILL) && style.get(Styles.FILL) != null;
        if (filled && primitive.contains(point)) {
            return true;
        } else {
            Shape sh = PathRenderer.strokedShape(primitive, style);
            return sh != null && sh.contains(point);
        }
    }

    @Override
    public boolean intersects(Shape primitive, AttributeSet style, Rectangle2D rect) {
        boolean filled = style.contains(Styles.FILL) && style.get(Styles.FILL) != null;
        if (filled && primitive.intersects(rect)) {
            return true;            
        } else {
            Shape sh = PathRenderer.strokedShape(primitive, style);
            return sh != null && sh.intersects(rect);
        }
    }

}
