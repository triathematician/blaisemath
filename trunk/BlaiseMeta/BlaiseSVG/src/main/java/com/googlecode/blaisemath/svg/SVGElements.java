/**
 * SVGObjectFactory.java
 * Created on Sep 26, 2014
 */
package com.googlecode.blaisemath.svg;

/*
 * #%L
 * BlaiseSVG
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
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
import com.googlecode.blaisemath.style.Marker;
import com.googlecode.blaisemath.style.Markers;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.util.AnchoredImage;
import com.googlecode.blaisemath.util.AnchoredText;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;

/**
 * Factory methods for converting to/from SVG Objects.
 * @author petereb1
 */
public class SVGElements {
    
    
    
    //
    // UTILITY CLASS - PREVENT INSTANTIATION
    //
    private SVGElements() {
    }

    /**
     * Create new svg object from given id, shape, and style. Supports shapes
     * {@link Rectanlge2D}, {@link RoundRectangle2D}, {@link Ellipse2D}, and
     * {@link Line2D}.
     * @param id object id
     * @param shape object's shape
     * @param style object's style
     * @return svg object
     */
    public static SVGElement create(String id, Shape shape, AttributeSet style) {
        SVGElement res;
        if (shape instanceof Rectangle2D || shape instanceof RoundRectangle2D) {
            res = SVGRectangle.shapeConverter().reverse().convert((RectangularShape) shape);
        } else if (shape instanceof Ellipse2D) {
            Ellipse2D ell = (Ellipse2D) shape;
            if (ell.getWidth() == ell.getHeight()) {
                res = SVGCircle.shapeConverter().reverse().convert(ell);
            } else {
                res = SVGEllipse.shapeConverter().reverse().convert(ell);
            }
        } else if (shape instanceof Line2D) {
            res = SVGLine.shapeConverter().reverse().convert((Line2D) shape);
        } else {
            throw new UnsupportedOperationException("Shapes of type "+shape.getClass()+" are not yet supported.");
        }
        res.setId(id);
        res.setStyle(style);
        return res;
    }

    /**
     * Create new svg object from a provided point/style.
     * @param id object id
     * @param point the point
     * @param style object's style
     * @return svg object
     */
    public static SVGElement create(String id, Point2D point, AttributeSet style) {
        Marker m = (Marker) style.get(Styles.MARKER);
        if (m == null) {
            m = Markers.CIRCLE;
        }
        Float ort = style.getFloat(Styles.MARKER_ORIENT, 0f);
        Float rad = style.getFloat(Styles.MARKER_RADIUS, 4f);
        Shape shape = m.create(point, ort, rad);
        return create(id, shape, style);
    }

    /**
     * Create new svg text object from given id, anchored text, and style.
     * @param id object id
     * @param text object text
     * @param style object's style
     * @return svg text object
     */
    public static SVGText create(String id, AnchoredText text, AttributeSet style) {
        SVGText res = SVGText.textConverter().reverse().convert(text);
        res.setId(id);
        res.setStyle(style);
        return res;
    }

    /**
     * Create new svg image object from given id, anchored image, and style.
     * @param id object id
     * @param img image object
     * @param style object's style
     * @return svg image object
     */
    public static SVGImage create(String id, AnchoredImage img, AttributeSet style) {
        SVGImage res = SVGImage.imageConverter().reverse().convert(img);
        res.setId(id);
        res.setStyle(style);
        return res;
    }

    /**
     * Return true if element is a path type (i.e. no fill expected)
     * @param el an element
     * @return true if its a path
     */
    public static boolean isPath(SVGElement el) {
        return el instanceof SVGLine || el instanceof SVGPolyline
                || el instanceof SVGPath;
    }
    
}
