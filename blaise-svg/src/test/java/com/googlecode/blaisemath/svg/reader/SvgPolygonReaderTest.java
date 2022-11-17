package com.googlecode.blaisemath.svg.reader;

/*-
 * #%L
 * blaise-svg
 * --
 * Copyright (C) 2014 - 2022 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.googlecode.blaisemath.svg.xml.SvgPolygon;
import org.junit.Test;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;

import static com.googlecode.blaisemath.svg.reader.SvgReaderTest.assertSvgReadException;
import static org.junit.Assert.*;

public class SvgPolygonReaderTest {

    @Test
    public void createPrimitive() {
        assertSvgReadException(() -> new SvgPolygonReader().createPrimitive(null));

        SvgPolygon poly1 = SvgPolygon.create("0,0 1,0 0,1");
        Path2D.Float gp1 = new SvgPolygonReader().createPrimitive(poly1);
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
        assertEquals(PathIterator.SEG_CLOSE, pi.currentSegment(crd));
        pi.next();
        assertTrue(pi.isDone());

        try {
            new SvgPolygonReader().createPrimitive(SvgPolygon.create("not points"));
            fail("Shouldn't be able to create");
        } catch (SvgReadException x) {
            // expected
        }

        try {
            new SvgPolygonReader().createPrimitive(SvgPolygon.create("1,a 4,5 6,7"));
            fail("Shouldn't be able to create");
        } catch (SvgReadException x) {
            // expected
        }

        try {
            new SvgPolygonReader().createPrimitive(SvgPolygon.create("1 4,5 6,7"));
        } catch (SvgReadException x) {
            // expected
        }

        // this is an invalid string but parse will let it pass by ignoring the third number
        new SvgPolygonReader().createPrimitive(SvgPolygon.create("1,2,3 4,5 6,7"));
    }

}
