/*
 * ShapeFillRenderer.java
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
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Draws a shape using fill color and no outline.
 *
 * @author Elisha
 */
public class ShapeFillRenderer implements Renderer<Shape, Graphics2D> {  

    private static final ShapeFillRenderer INST = new ShapeFillRenderer();
    
    public static ShapeFillRenderer getInstance() {
        return INST;
    }
    
    public void render(Shape primitive, AttributeSet style, Graphics2D canvas) {
        if (style.contains(Styles.FILL)) {
            Color fill = style.getColor(Styles.FILL);
            canvas.setColor(fill);
            canvas.fill(primitive);
        }
    }

    public boolean contains(Shape primitive, AttributeSet style, Point2D point) {
        return style.contains(Styles.FILL) && primitive.contains(point);
    }

    public boolean intersects(Shape primitive, AttributeSet style, Rectangle2D rect) {
        return style.contains(Styles.FILL) && primitive.intersects(rect);
    }

}
