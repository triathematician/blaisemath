package com.googlecode.blaisemath.svg.reader;

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
