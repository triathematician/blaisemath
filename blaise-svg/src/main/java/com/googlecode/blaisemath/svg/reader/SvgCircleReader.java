package com.googlecode.blaisemath.svg.reader;

import com.googlecode.blaisemath.svg.xml.SvgCircle;

import java.awt.geom.Ellipse2D;

/**
 * Converts an SVG circle to its Java shape equivalent.
 * @author Elisha Peterson
 */
public class SvgCircleReader extends SvgShapeReader<SvgCircle, Ellipse2D.Float> {

    @Override
    protected Ellipse2D.Float createPrimitive(SvgCircle r) {
        if (r == null) {
            throw new SvgReadException("Null SVG element");
        }
        return new Ellipse2D.Float(r.cx - r.r, r.cy - r.r, 2 * r.r, 2 * r.r);
    }

}