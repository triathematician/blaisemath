package com.googlecode.blaisemath.svg.reader;

import com.googlecode.blaisemath.graphics.Graphic;
import com.googlecode.blaisemath.graphics.GraphicComposite;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.svg.xml.*;

import java.awt.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reads group of SVG content as Blaise graphics content. Supported SVG types include:
 * <ul>
 * <li>{@link SvgRect}, {@link SvgEllipse}, {@link SvgCircle}, {@link SvgPolygon}</li>
 * <li>{@link SvgLine}, {@link SvgPolyline}</li>
 * <li>{@link SvgPath}</li>
 * <li>{@link SvgImage}</li>
 * <li>{@link SvgText}</li>
 * <li>{@link SvgGroup}</li>
 * </ul>
 * @author Elisha Peterson
 */
public final class SvgGroupReader extends SvgReader<SvgGroup, List<SvgElement>> {

    private static final Logger LOG = Logger.getLogger(SvgGroupReader.class.getName());

    @Override
    protected List<SvgElement> createPrimitive(SvgGroup el) {
        if (el == null) {
            throw new SvgReadException("Null SVG element");
        }
        return el.elements;
    }

    @Override
    protected Graphic<Graphics2D> createGraphic(List<SvgElement> prims, AttributeSet style) {
        GraphicComposite<Graphics2D> res = new GraphicComposite<>();
        res.setStyle(style);
        for (SvgElement p : prims) {
            try {
                Graphic<Graphics2D> g = readGraphic(p);
                g.setDefaultTooltip(p.id);
                res.addGraphic(g);
            } catch (SvgReadException e) {
                LOG.log(Level.SEVERE, "Graphic read error", e);
            }
        }
        return res;
    }

    public static Graphic<Graphics2D> readGraphic(SvgElement el) throws SvgReadException {
        for (SvgReader r : SvgReader.readers()) {
            if (r.canRead(el)) {
                return r.read(el);
            }
        }
        throw new SvgReadException("Unsupported: "+el);
    }

}
