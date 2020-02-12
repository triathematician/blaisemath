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

import com.googlecode.blaisemath.svg.xml.SvgEllipse;

import java.awt.geom.Ellipse2D;

/**
 * Converts an SVG ellipse to its Java shape equivalent.
 * @author Elisha Peterson
 */
public class SvgEllipseReader extends SvgShapeReader<SvgEllipse, Ellipse2D.Float> {

    @Override
    protected Ellipse2D.Float createPrimitive(SvgEllipse r) {
        if (r == null) {
            throw new SvgReadException("Null SVG element");
        }
        return new Ellipse2D.Float(r.cx - r.rx, r.cy - r.ry, 2 * r.rx, 2 * r.ry);
    }

}
