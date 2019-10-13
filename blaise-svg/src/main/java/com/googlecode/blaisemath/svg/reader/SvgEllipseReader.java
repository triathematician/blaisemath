package com.googlecode.blaisemath.svg.reader;

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
