package com.googlecode.blaisemath.svg.reader;

/*-
 * #%L
 * blaise-svg
 * --
 * Copyright (C) 2014 - 2024 Elisha Peterson
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
