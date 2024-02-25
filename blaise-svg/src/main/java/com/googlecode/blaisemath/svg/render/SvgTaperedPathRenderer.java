package com.googlecode.blaisemath.svg.render;

/*-
 * #%L
 * blaise-svg
 * --
 * Copyright (C) 2014 - 2024 Elisha Peterson
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
import com.googlecode.blaisemath.svg.render.SvgRenderer;
import com.googlecode.blaisemath.svg.render.SvgShapeRenderer;
import com.googlecode.blaisemath.svg.render.SvgTreeBuilder;
import com.googlecode.blaisemath.util.Colors;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

import static com.googlecode.blaisemath.graphics.swing.render.TaperedPathRenderer.createBezierShape;

/**
 * Write SVG shape for path with tapered style.
 * @author Elisha Peterson
 */
public class SvgTaperedPathRenderer extends SvgRenderer<Shape> {

    @Override
    public void render(Shape s, AttributeSet style, SvgTreeBuilder canvas) {
        Color stroke = style.getColor(Styles.STROKE);
        Float strokeWidth = style.getFloat(Styles.STROKE_WIDTH, 1f);

        if(strokeWidth <= 0f || stroke == null) {
            return;
        }

        Shape shape = s instanceof Line2D.Double ? createBezierShape((Line2D.Double) s, strokeWidth)
                : s instanceof GeneralPath ? createBezierShape((GeneralPath) s, strokeWidth)
                : s;

        Color cAlpha = Colors.alpha(stroke, stroke.getAlpha()/2);
        new SvgShapeRenderer().render(shape, Styles.fillStroke(cAlpha, stroke, strokeWidth), canvas);
    }

}
