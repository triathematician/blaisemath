package com.googlecode.blaisemath.svg.reader;

import com.googlecode.blaisemath.svg.xml.SvgLine;
import org.junit.Test;

import java.awt.geom.Line2D;

import static com.googlecode.blaisemath.svg.reader.SvgReaderTest.assertSvgReadException;
import static org.junit.Assert.assertEquals;

public class SvgLineReaderTest {

    @Test
    public void createPrimitive() {
        assertSvgReadException(() -> new SvgLineReader().createPrimitive(null));
        Line2D.Float l = new SvgLineReader().createPrimitive(SvgLine.create(1,2,3,4));
        assertLineEquals(new Line2D.Float(1, 2, 3, 4), l);
    }

    private void assertLineEquals(Line2D.Float a, Line2D.Float b) {
        assertEquals(a.x1, b.x1, 1e-6);
        assertEquals(a.x2, b.x2, 1e-6);
        assertEquals(a.y1, b.y1, 1e-6);
        assertEquals(a.y2, b.y2, 1e-6);
    }

}