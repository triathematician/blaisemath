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

import com.googlecode.blaisemath.graphics.svg.SvgPathCoder;
import com.googlecode.blaisemath.svg.xml.SvgPath;

import java.awt.geom.Path2D;

/**
 * Converts an SVG path to its Java shape equivalent.
 * @author Elisha Peterson
 */
public class SvgPathReader extends SvgShapeReader<SvgPath, Path2D.Float> {

    @Override
    protected Path2D.Float createPrimitive(SvgPath r) {
        if (r == null) {
            throw new SvgReadException("Null SVG element");
        }
        return (Path2D.Float) new SvgPathCoder().decode(r.pathStr);
    }

}
