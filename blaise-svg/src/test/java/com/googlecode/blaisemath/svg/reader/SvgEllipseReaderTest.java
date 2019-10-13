package com.googlecode.blaisemath.svg.reader;

import com.googlecode.blaisemath.svg.xml.SvgEllipse;
import org.junit.Test;

import java.awt.geom.Ellipse2D;

import static com.googlecode.blaisemath.svg.reader.SvgReaderTest.assertSvgReadException;
import static org.junit.Assert.assertEquals;

public class SvgEllipseReaderTest {

    @Test
    public void createPrimitive() {
        assertSvgReadException(() -> new SvgEllipseReader().createPrimitive(null));
        assertEquals(new Ellipse2D.Float(1, 1, 2, 4), new SvgEllipseReader().createPrimitive(SvgEllipse.create(2, 3, 1, 2)));
    }

}