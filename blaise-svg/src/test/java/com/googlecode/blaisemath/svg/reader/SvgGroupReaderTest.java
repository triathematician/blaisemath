package com.googlecode.blaisemath.svg.reader;

import com.googlecode.blaisemath.svg.xml.SvgCircle;
import com.googlecode.blaisemath.svg.xml.SvgElement;
import com.googlecode.blaisemath.svg.xml.SvgGroup;
import org.junit.Test;

import java.util.Arrays;

import static com.googlecode.blaisemath.svg.reader.SvgReaderTest.assertSvgReadException;
import static org.junit.Assert.assertEquals;

public class SvgGroupReaderTest {

    @Test
    public void createPrimitive() {
        assertSvgReadException(() -> new SvgGroupReader().createPrimitive(null));

        SvgElement e1 = SvgCircle.create(1, 2, 3);
        SvgGroup g1 = SvgGroup.create(e1);
        assertEquals(Arrays.asList(e1), new SvgGroupReader().createPrimitive(g1));
    }

}