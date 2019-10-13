package com.googlecode.blaisemath.svg.reader;

import com.googlecode.blaisemath.svg.xml.SvgCircle;
import org.junit.Test;

import java.awt.geom.Ellipse2D;

import static com.googlecode.blaisemath.svg.reader.SvgReaderTest.assertSvgReadException;
import static org.junit.Assert.*;

public class SvgCircleReaderTest {

    @Test
    public void createPrimitive() {
        assertSvgReadException(() -> new SvgCircleReader().createPrimitive(null));
        assertEquals(new Ellipse2D.Float(1, 2, 2, 2), new SvgCircleReader().createPrimitive(SvgCircle.create(2, 3, 1)));
    }

}