package com.googlecode.blaisemath.svg.reader;

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

