package com.googlecode.blaisemath.svg.reader;

import com.googlecode.blaisemath.graphics.Graphic;
import com.googlecode.blaisemath.graphics.PrimitiveGraphic;
import com.googlecode.blaisemath.style.AttributeSet;
import org.junit.Test;

import java.awt.*;
import java.awt.geom.Ellipse2D;

import static com.googlecode.blaisemath.svg.reader.SvgReaderTest.assertSvgReadException;
import static org.junit.Assert.assertEquals;

public class SvgShapeReaderTest {

    @Test
    public void createGraphic() {
        assertSvgReadException(() -> new SvgEllipseReader().createGraphic(null, null));
        assertSvgReadException(() -> new SvgEllipseReader().createGraphic(null, AttributeSet.EMPTY));
        Ellipse2D.Float e1 = new Ellipse2D.Float(1, 1, 2, 4);

        Graphic<Graphics2D> g1 = new SvgEllipseReader().createGraphic(e1, null);
        assertEquals(PrimitiveGraphic.class, g1.getClass());
        assertEquals(e1, ((PrimitiveGraphic) g1).getPrimitive());
        assertEquals(null, ((PrimitiveGraphic) g1).getStyle());

        Graphic<Graphics2D> g2 = new SvgEllipseReader().createGraphic(e1, AttributeSet.EMPTY);
        assertEquals(PrimitiveGraphic.class, g2.getClass());
        assertEquals(e1, ((PrimitiveGraphic) g2).getPrimitive());
        assertEquals(AttributeSet.EMPTY, ((PrimitiveGraphic) g2).getStyle());
    }

}