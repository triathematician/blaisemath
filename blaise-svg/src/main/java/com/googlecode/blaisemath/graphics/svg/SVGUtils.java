/**
 * SVGUtils.java
 * Created on May 5, 2015
 */
package com.googlecode.blaisemath.graphics.svg;

/*
 * #%L
 * BlaiseSVG
 * --
 * Copyright (C) 2014 - 2017 Elisha Peterson
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

import com.googlecode.blaisemath.style.Marker;
import com.googlecode.blaisemath.svg.SVGPath;
import com.googlecode.blaisemath.util.AffineTransformBuilder;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

/**
 * Utility class for SVG paths.
 * @author petereb1
 */
public class SVGUtils {
    
    /**
     * Convert the given path string to a marker with given size.
     * @param svgPath SVG path string
     * @param sz size of the resulting marker, as the side length/diameter
     * @return marker
     */
    public static final Marker pathToMarker(String svgPath, final float sz) {
        final Path2D path = SVGPath.shapeConverter().convert(new SVGPath(svgPath));
        return new Marker() {
            @Override
            public Shape create(Point2D point, double orientation, float markerRadius) {
            double scale = markerRadius/(.5*sz);
                return path.createTransformedShape(new AffineTransformBuilder()
                    .translate(point.getX()-markerRadius, point.getY()-markerRadius)
                    .scale(scale, scale)
                    .build());
            }
        };
    }
}
