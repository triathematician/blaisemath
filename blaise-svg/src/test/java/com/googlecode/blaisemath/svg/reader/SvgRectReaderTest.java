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

import com.googlecode.blaisemath.svg.xml.SvgRect;
import org.junit.Test;

import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;

import static com.googlecode.blaisemath.svg.reader.SvgReaderTest.assertSvgReadException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SvgRectReaderTest {

    @Test
    public void createPrimitive() {
        assertSvgReadException(() -> new SvgRectReader().createPrimitive(null));

        SvgRect sRect = SvgRect.create(1, 2, 3, 4);
        RectangularShape rect = new SvgRectReader().createPrimitive(sRect);
        assert rect != null;
        assertTrue(rect instanceof Rectangle2D.Float);
        assertEquals(1.0, rect.getX(), 1e-6);
        assertEquals(2.0, rect.getY(), 1e-6);
        assertEquals(3.0, rect.getWidth(), 1e-6);
        assertEquals(4.0, rect.getHeight(), 1e-6);

        SvgRect sRect2 = SvgRect.create(1f, 2f, 3f, 4f, 0.2f, 0.3f);
        RectangularShape rect2 = new SvgRectReader().createPrimitive(sRect2);
        assert rect2 != null;
        assertTrue(rect2 instanceof RoundRectangle2D.Float);
        assertEquals(1.0, rect2.getX(), 1e-6);
        assertEquals(2.0, rect2.getY(), 1e-6);
        assertEquals(3.0, rect2.getWidth(), 1e-6);
        assertEquals(4.0, rect2.getHeight(), 1e-6);
        assertEquals(.2, ((RoundRectangle2D)rect2).getArcWidth(), 1e-6);
        assertEquals(.3, ((RoundRectangle2D)rect2).getArcHeight(), 1e-6);
    }

}
