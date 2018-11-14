/*
 * ShapeRenderer.java
 * Created Jan 9, 2011 (based on much earlier code)
 */

package com.googlecode.blaisemath.graphics.swing;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2018 Elisha Peterson
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
    
    @Override
    public String toString() {
        return "ShapeRenderer";
    }
    
    @Override
    public void render(Shape primitive, AttributeSet style, Graphics2D canvas) {
        if (Styles.hasFill(style)) {
            canvas.setColor(Styles.fillColorOf(style));
            canvas.fill(primitive);
        }
        
        if (Styles.hasStroke(style)) {
            canvas.setColor(Styles.strokeColorOf(style));
            canvas.setStroke(Styles.strokeOf(style));
            PathRenderer.drawPatched(primitive, canvas);
        }
    }

    @Override
    public Rectangle2D boundingBox(Shape primitive, AttributeSet style) {
        boolean filled = Styles.hasFill(style);
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
        if (Styles.hasFill(style) && primitive.contains(point)) {
            return true;
        } else {
            Shape sh = PathRenderer.strokedShape(primitive, style);
            return sh != null && sh.contains(point);
        }
    }

    @Override
    public boolean intersects(Shape primitive, AttributeSet style, Rectangle2D rect) {
        if (Styles.hasFill(style) && primitive.intersects(rect)) {
            return true;            
        } else {
            Shape sh = PathRenderer.strokedShape(primitive, style);
            return sh != null && sh.intersects(rect);
        }
    }

}
