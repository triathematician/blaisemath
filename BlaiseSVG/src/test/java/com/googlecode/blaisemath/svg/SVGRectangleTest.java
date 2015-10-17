/*
 * Copyright 2015 elisha.
 *
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
 */
package com.googlecode.blaisemath.svg;

/*
 * #%L
 * BlaiseSVG
 * --
 * Copyright (C) 2014 - 2015 Elisha Peterson
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


import com.google.common.base.Converter;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;
import junit.framework.TestCase;
import static junit.framework.TestCase.assertEquals;
import org.junit.Test;

/**
 *
 * @author elisha
 */
public class SVGRectangleTest extends TestCase {

    @Test
    public void testConvertToSvg() {
        System.out.println("convertToSvg");
        Converter<SVGRectangle, RectangularShape> conv = SVGRectangle.shapeConverter();
        
        Rectangle2D r = new Rectangle2D.Double(1.0, 2.0, 4.0, 3.0);
        SVGRectangle rect = conv.reverse().convert(r);
        assert rect != null;
        assertEquals(1.0, rect.getX(), 1e-6);
        assertEquals(2.0, rect.getY(), 1e-6);
        assertEquals(4.0, rect.getWidth(), 1e-6);
        assertEquals(3.0, rect.getHeight(), 1e-6);
        assertEquals(0, rect.getRx(), 1e-6);
        assertEquals(0, rect.getRy(), 1e-6);
        
        RoundRectangle2D rr = new RoundRectangle2D.Double(1.0, 2.0, 4.0, 3.0, .2, .3);
        SVGRectangle rrect = conv.reverse().convert(rr);
        assert rrect != null;
        assertEquals(1.0, rrect.getX(), 1e-6);
        assertEquals(2.0, rrect.getY(), 1e-6);
        assertEquals(4.0, rrect.getWidth(), 1e-6);
        assertEquals(3.0, rrect.getHeight(), 1e-6);
        assertEquals(.2, rrect.getRx(), 1e-6);
        assertEquals(.3, rrect.getRy(), 1e-6);
        
        assertEquals(null, conv.reverse().convert(null));
    }

    @Test
    public void testConvertFromSvg() {
        System.out.println("convertToSvg");
        Converter<SVGRectangle, RectangularShape> conv = SVGRectangle.shapeConverter();
        
        SVGRectangle sRect = new SVGRectangle(1, 2, 3, 4);
        RectangularShape rect = conv.convert(sRect);
        assert rect != null;
        assertTrue(rect instanceof Rectangle2D.Double);
        assertEquals(1.0, rect.getX(), 1e-6);
        assertEquals(2.0, rect.getY(), 1e-6);
        assertEquals(3.0, rect.getWidth(), 1e-6);
        assertEquals(4.0, rect.getHeight(), 1e-6);
        
        SVGRectangle sRect2 = new SVGRectangle(1, 2, 3, 4, .2, .3);
        RectangularShape rect2 = conv.convert(sRect2);
        assert rect2 != null;
        assertTrue(rect2 instanceof RoundRectangle2D.Double);
        assertEquals(1.0, rect2.getX(), 1e-6);
        assertEquals(2.0, rect2.getY(), 1e-6);
        assertEquals(3.0, rect2.getWidth(), 1e-6);
        assertEquals(4.0, rect2.getHeight(), 1e-6);
        assertEquals(.2, ((RoundRectangle2D)rect2).getArcWidth());
        assertEquals(.3, ((RoundRectangle2D)rect2).getArcHeight());
        
        assertEquals(null, conv.convert(null));
    }
    
}
