package com.googlecode.blaisemath.svg.reader;

/*-
 * #%L
 * blaise-svg
 * --
 * Copyright (C) 2014 - 2019 Elisha Peterson
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
