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

import com.googlecode.blaisemath.graphics.swing.render.MarkerRenderer;
import com.googlecode.blaisemath.style.AttributeSet;

import java.awt.geom.Point2D;

/**
 * Write SVG marker representation.
 * @author Elisha Peterson
 */
public class SvgMarkerRenderer extends SvgRenderer<Point2D> {

    @Override
    public void render(Point2D primitive, AttributeSet style, SvgTreeBuilder canvas) {
        MarkerRenderer m = new MarkerRenderer();
        new SvgShapeRenderer().render(m.getShape(primitive, style), style, canvas);
    }

}
