package com.googlecode.blaisemath.svg.reader;

import com.googlecode.blaisemath.graphics.Graphic;
import com.googlecode.blaisemath.graphics.swing.JGraphics;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.svg.xml.SvgLine;

import java.awt.*;
import java.awt.geom.Line2D;

/**
 * Converts an SVG line to its Java shape equivalent.
 * @author Elisha Peterson
 */
public class SvgLineReader extends SvgReader<SvgLine, Line2D> {

    @Override
    protected Line2D.Float createPrimitive(SvgLine r) {
        if (r == null) {
            throw new SvgReadException("Null SVG element");
        }
        return new Line2D.Float(r.x1, r.y1, r.x2, r.y2);
    }

    @Override
    protected Graphic<Graphics2D> createGraphic(Line2D prim, AttributeSet style) {
        // override because we want draw-only, not fill
        return JGraphics.path(prim, style);
    }

}
