package com.googlecode.blaisemath.svg.reader;

import com.googlecode.blaisemath.svg.xml.SvgPolygon;

import java.awt.geom.Path2D;

/**
 * Converts an SVG polygon to its Java shape equivalent.
 * @author Elisha Peterson
 */
public final class SvgPolygonReader extends SvgShapeReader<SvgPolygon, Path2D.Float> {

    @Override
    protected Path2D.Float createPrimitive(SvgPolygon r) {
        if (r == null) {
            throw new SvgReadException("Null SVG element");
        }
        Path2D.Float res = new SvgPolylineReader().createPrimitive(r);
        res.closePath();
        return res;
    }

}
