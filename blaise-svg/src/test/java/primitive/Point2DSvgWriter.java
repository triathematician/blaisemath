package primitive;

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

import com.googlecode.blaisemath.coordinate.OrientedPoint2D;
import com.googlecode.blaisemath.primitive.Marker;
import com.googlecode.blaisemath.primitive.Markers;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.svg.render.SvgRenderException;
import com.googlecode.blaisemath.svg.xml.SvgElement;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Converts point markers to corresponding SVG shape elements.
 * @author Elisha Peterson
 */
public class Point2DSvgWriter implements PrimitiveSvgWriter<Point2D, SvgElement> {

    @Override
    public SvgElement write(String id, Point2D point, AttributeSet style) throws SvgRenderException {
        Marker m = (Marker) style.get(Styles.MARKER);
        if (m == null) {
            m = Markers.CIRCLE;
        }
        Float ort = style.getFloat(Styles.MARKER_ORIENT, point instanceof OrientedPoint2D ? (float) ((OrientedPoint2D)point).getAngle() : 0f);
        Float rad = style.getFloat(Styles.MARKER_RADIUS, 4f);
        Shape shape = m.create(point, ort, rad);
        return new ShapeSvgWriter().write(id, shape, style);
    }

}
