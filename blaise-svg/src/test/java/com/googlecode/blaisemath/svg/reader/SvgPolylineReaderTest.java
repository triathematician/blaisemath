package com.googlecode.blaisemath.svg.reader;

import com.googlecode.blaisemath.svg.xml.SvgPolyline;
import org.junit.Test;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;

import static com.googlecode.blaisemath.svg.reader.SvgReaderTest.assertSvgReadException;
import static org.junit.Assert.*;

public class SvgPolylineReaderTest {

    @Test
    public void createPrimitive() {
        assertSvgReadException(() -> new SvgPolylineReader().createPrimitive(null));

        SvgPolyline poly1 = SvgPolyline.create("0,0 1,0 0,1");
        Path2D.Float gp1 = new SvgPolylineReader().createPrimitive(poly1);
        PathIterator pi = gp1.getPathIterator(null);
        double[] crd = new double[6];
        assertEquals(PathIterator.SEG_MOVETO, pi.currentSegment(crd));
        assertEquals(0.0, crd[0], 1e-6);
        assertEquals(0.0, crd[1], 1e-6);
        pi.next();
        assertEquals(PathIterator.SEG_LINETO, pi.currentSegment(crd));
        assertEquals(1.0, crd[0], 1e-6);
        assertEquals(0.0, crd[1], 1e-6);
        pi.next();
        assertEquals(PathIterator.SEG_LINETO, pi.currentSegment(crd));
        assertEquals(0.0, crd[0], 1e-6);
        assertEquals(1.0, crd[1], 1e-6);
        pi.next();
        assertTrue(pi.isDone());

        try {
            new SvgPolylineReader().createPrimitive(SvgPolyline.create("not points"));
            fail("Shouldn't be able to create");
        } catch (SvgReadException x) {
            // expected
        }

        try {
            new SvgPolylineReader().createPrimitive(SvgPolyline.create("1,a 4,5 6,7"));
            fail("Shouldn't be able to create");
        } catch (SvgReadException x) {
            // expected
        }

        try {
            new SvgPolylineReader().createPrimitive(SvgPolyline.create("1 4,5 6,7"));
        } catch (SvgReadException x) {
            // expected
        }

        // this is an invalid string but parse will let it pass by ignoring the third number
        new SvgPolylineReader().createPrimitive(SvgPolyline.create("1,2,3 4,5 6,7"));
    }

}