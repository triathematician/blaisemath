package com.googlecode.blaisemath.graphics.swing.render;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2021 Elisha Peterson
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
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.util.Colors;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 * Draws a shape using a stroke (with thickness) and a fill color.
 * Uses a gradient with a slight color variation to fill the shape.
 *
 * @author Elisha Peterson
 */
public class GradientShapeRenderer extends ShapeRenderer {  

    private static final GradientShapeRenderer INST = new GradientShapeRenderer();
    
    public static GradientShapeRenderer getInstance() {
        return INST;
    }
    
    @Override
    public void render(Shape primitive, AttributeSet style, Graphics2D canvas) {
        if (style.contains(Styles.FILL)) {
            Rectangle2D bds = primitive.getBounds2D();
            Color fill = style.getColor(Styles.FILL);
            canvas.setPaint(new GradientPaint(
                    (float) bds.getMinX(), (float) bds.getMinY(), fill,
                    (float) bds.getMaxX(), (float) bds.getMaxY(), Colors.blanderThan(fill)));
            canvas.fill(primitive);
        }
        
        Color stroke = style.getColor(Styles.STROKE);
        Float strokeWidth = style.getFloat(Styles.STROKE_WIDTH);
        if (stroke != null && strokeWidth != null && strokeWidth > 0) {
            canvas.setColor(stroke);
            canvas.setStroke(new BasicStroke(strokeWidth));
            PathRenderer.drawPatched(primitive, canvas);
        }
    }

}
