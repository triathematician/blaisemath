package com.googlecode.blaisemath.svg.reader;

import com.googlecode.blaisemath.graphics.Graphic;
import com.googlecode.blaisemath.graphics.swing.JGraphics;
import com.googlecode.blaisemath.primitive.AnchoredText;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.svg.xml.SvgText;

import java.awt.*;

/**
 * Converts an SVG text element to its Java shape equivalent.
 * @author Elisha Peterson
 */
public final class SvgTextReader extends SvgReader<SvgText, AnchoredText> {

    @Override
    protected AnchoredText createPrimitive(SvgText r) {
        if (r == null) {
            throw new SvgReadException("Null SVG element");
        }
        return new AnchoredText(r.x, r.y, r.value);
    }

    @Override
    protected Graphic<Graphics2D> createGraphic(AnchoredText prim, AttributeSet style) {
        Graphic<Graphics2D> res = JGraphics.text(prim, style);
        res.setMouseDisabled(true);
        return res;
    }

}
