package com.googlecode.blaisemath.svg.reader;

/*-
 * #%L
 * blaise-svg
 * --
 * Copyright (C) 2014 - 2025 Elisha Peterson
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

import com.googlecode.blaisemath.svg.xml.SvgRect;

import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;

/**
 * Converts an SVG rect to its Java shape equivalent.
 * @author Elisha Peterson
 */
public final class SvgRectReader extends SvgShapeReader<SvgRect, RectangularShape> {

    @Override
    public RectangularShape createPrimitive(SvgRect r) {
        if (r == null) {
            throw new SvgReadException("Null SVG element");
        }
        if (r.rx == null && r.ry == null) {
            return new Rectangle2D.Float(r.x, r.y, r.width, r.height);
        } else {
            return new RoundRectangle2D.Float(r.x, r.y, r.width, r.height, r.rx, r.ry);
        }
    }

}

