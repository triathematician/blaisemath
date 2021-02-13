/**
 * SVGUtils.java
 * Created on May 5, 2015
 */
package com.googlecode.blaisemath.graphics.svg;

/*
 * #%L
 * BlaiseSVG
 * --
 * Copyright (C) 2014 - 2021 Elisha Peterson
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
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import static java.util.Objects.requireNonNull;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for SVG paths.
 * @author petereb1
 */
public class SVGUtils {

    private static final Logger LOG = Logger.getLogger(SVGUtils.class.getName());

    private static final Pattern LEN_PATTERN = Pattern.compile("^auto$|^[+-]?[0-9]+\\.?([0-9]+)?(px|em|ex|%|in|cm|mm|pt|pc)?$");
    
    private SVGUtils() {
    }
    
    /**
     * Convert the given path string to a marker with given size.
     * @param svgPath SVG path string
     * @param sz size of the resulting marker, as the side length/diameter
     * @return marker
     */
    public static final Marker pathToMarker(String svgPath, final float sz) {
        final Path2D path = SVGPath.shapeConverter().convert(new SVGPath(svgPath));
        if (path == null) {
            
        }
        return (Point2D point, double orientation, float markerRadius) -> {
            double scale = markerRadius/(.5*sz);
            return path.createTransformedShape(new AffineTransformBuilder()
                    .translate(point.getX()-markerRadius, point.getY()-markerRadius)
                    .scale(scale, scale)
                    .build());
        };
    }
    
    /**
     * Parses a length as a number of points. Note that this does not handle
     * relative units such as "auto", "%", "em", "ex" properly.
     * @param len input length
     * @return equivalent # of points
     */
    public static double parseLength(String len) {
        if (len == null) {
            LOG.log(Level.WARNING, "Null length: {0}", len);
            return 0;
        }
        String uselen = len.trim();
        Matcher m = LEN_PATTERN.matcher(uselen);
        if (m.matches()) {
            if ("auto".equals(len) || len.contains("%")) {
                LOG.log(Level.WARNING, "Unsupported length: {0}", len);
                return 0;
            }
            String sunit = m.group(2);
            Unit unit = unit(sunit);
            String num = sunit == null ? len : len.substring(0, len.length()-sunit.length());
            Double val = Double.parseDouble(num);
            return unit.toPixels(val);
        }
        return 0;
    }
    
    private static Unit unit(String unit) {
        if (unit == null) {
            return Unit.PT;
        }
        for (Unit u : Unit.values()) {
            if (u.name.equalsIgnoreCase(unit)) {
                return u;
            }
        }
        LOG.log(Level.WARNING, "Invalid unit: {0}", unit);
        return Unit.PT;
    }
    
    private static enum Unit {
        PX("px", 1.3f),
        EM("em", 11.955f),
        EX("ex", 11.955f),
        PCT("%", 1f),
        IN("in", 72f),
        CM("cm", 28.3464f),
        MM("mm", 2.83464f),
        PT("pt", 1f),
        PC("pc", 12f);
        
        private String name;
        private float scale;

        private Unit(String name, float scale) {
            this.name = name;
            this.scale = scale;
        }
        
        private double toPixels(double val) {
            return val*scale;
        }
    }
    
}
