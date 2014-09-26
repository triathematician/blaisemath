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

import com.googlecode.blaisemath.graphics.core.Graphic;
import com.googlecode.blaisemath.graphics.core.PrimitiveGraphic;
import com.googlecode.blaisemath.graphics.swing.JGraphics;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.util.AnchoredImage;
import com.googlecode.blaisemath.util.geom.AnchoredText;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;

/**
 * Factory methods for converting to/from SVG Objects.
 * @author petereb1
 */
public class SVGElementFactory {
    
    //
    // UTILITY CLASS - PREVENT INSTANTIATION
    //
    private SVGElementFactory() {
    }

    /**
     * Create new svg object from given id, shape, and style.
     * @param id object id
     * @param shape object's shape
     * @param style object's style
     * @return svg object
     */
    public static SVGElement create(String id, Shape shape, AttributeSet style) {
        SVGElement res;
        if (shape instanceof Rectangle2D || shape instanceof RoundRectangle2D) {
            res = SVGRectangle.ADAPTER.marshal((RectangularShape) shape);
        } else if (shape instanceof Ellipse2D) {
            Ellipse2D ell = (Ellipse2D) shape;
            if (ell.getWidth() == ell.getHeight()) {
                res = SVGCircle.ADAPTER.marshal(ell);
            } else {
                res = SVGEllipse.ADAPTER.marshal(ell);
            }
        } else if (shape instanceof Line2D) {
            res = SVGLine.ADAPTER.marshal((Line2D) shape);
        } else {
            throw new UnsupportedOperationException();
        }
        res.setId(id);
        res.setStyle(style);
        return res;
    }
    
    /**
     * Create new primitive graphics object from SVG object.
     * @param sh svg object
     * @return corresponding primitive object
     */
    public static Graphic asGraphic(SVGElement sh) {
        PrimitiveGraphic prim;
        if (sh instanceof SVGRectangle) {
            RectangularShape rsh = SVGRectangle.ADAPTER.unmarshal((SVGRectangle) sh);
            prim = JGraphics.shape(rsh, sh.getStyle());
        } else if (sh instanceof SVGEllipse) {
            Shape rsh = SVGEllipse.ADAPTER.unmarshal((SVGEllipse) sh);
            prim = JGraphics.shape(rsh, sh.getStyle());
        } else if (sh instanceof SVGCircle) {
            Shape rsh = SVGCircle.ADAPTER.unmarshal((SVGCircle) sh);
            prim = JGraphics.shape(rsh, sh.getStyle());
        } else if (sh instanceof SVGImage) {
            AnchoredImage img = SVGImage.ADAPTER.unmarshal((SVGImage) sh);
            prim = JGraphics.image(img);
        } else if (sh instanceof SVGText) {
            AnchoredText text = SVGText.ADAPTER.unmarshal((SVGText) sh);
            prim = JGraphics.text(text, sh.getStyle());
        } else if (sh instanceof SVGLine) {
            Line2D line = SVGLine.ADAPTER.unmarshal((SVGLine) sh);
            prim = JGraphics.path(line, sh.getStyle());
        } else {
            throw new UnsupportedOperationException();
        }
        prim.setDefaultTooltip(sh.getId());
        return prim;
    }
    
}
