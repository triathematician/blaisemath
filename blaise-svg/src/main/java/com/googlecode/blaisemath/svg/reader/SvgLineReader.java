package com.googlecode.blaisemath.svg.reader;

/*-
 * #%L
 * blaise-svg
 * --
 * Copyright (C) 2014 - 2020 Elisha Peterson
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

import com.googlecode.blaisemath.graphics.Graphic;
import com.googlecode.blaisemath.graphics.swing.JGraphics;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.svg.xml.SvgLine;

import java.awt.*;
import java.awt.geom.Line2D;

/**
 * Converts an SVG line to its Java shape equivalent.
 * @author Elisha Peterson
 */
public class SvgLineReader extends SvgReader<SvgLine, Line2D> {

    @Override
    protected Line2D.Float createPrimitive(SvgLine r) {
        if (r == null) {
            throw new SvgReadException("Null SVG element");
        }
        return new Line2D.Float(r.x1, r.y1, r.x2, r.y2);
    }

    @Override
    protected Graphic<Graphics2D> createGraphic(Line2D prim, AttributeSet style) {
        // override because we want draw-only, not fill
        return JGraphics.path(prim, style);
    }

}
