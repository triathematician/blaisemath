package com.googlecode.blaisemath.svg.reader;

/*-
 * #%L
 * blaise-svg
 * --
 * Copyright (C) 2014 - 2021 Elisha Peterson
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

import com.googlecode.blaisemath.svg.xml.SvgPath;
import org.junit.Test;

import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;

import static com.googlecode.blaisemath.svg.reader.SvgReaderTest.assertSvgReadException;
import static java.awt.geom.PathIterator.*;
import static org.junit.Assert.assertEquals;

public class SvgPathReaderTest {

    @Test
    public void createPrimitive() {
        assertSvgReadException(() -> new SvgPathReader().createPrimitive(null));
        Path2D.Float path = new SvgPathReader().createPrimitive(SvgPath.create("M0,0 L1,0 L0,1 Z"));
        assertEquals(Path2D.Float.class, path.getClass());
        PathIterator pi = path.getPathIterator(null);
        float[] cc = new float[6];
        assertEquals(SEG_MOVETO, pi.currentSegment(cc));
        assertEquals(0, cc[0], .01f);
        assertEquals(0, cc[1], .01f);
        pi.next();
        assertEquals(SEG_LINETO, pi.currentSegment(cc));
        assertEquals(1, cc[0], .01f);
        assertEquals(0, cc[1], .01f);
        pi.next();
        assertEquals(SEG_LINETO, pi.currentSegment(cc));
        assertEquals(0, cc[0], .01f);
        assertEquals(1, cc[1], .01f);
        pi.next();
        assertEquals(SEG_CLOSE, pi.currentSegment(cc));
    }

}
