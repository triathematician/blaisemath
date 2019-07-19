package com.googlecode.blaisemath.svg;

/*
 * #%L
 * BlaiseSVG
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

import com.google.common.base.Converter;
import org.junit.Test;

import java.awt.geom.Line2D;

import static org.junit.Assert.assertEquals;

public class SvgLineTest {

    @Test
    public void testConvertToSvg() {
        System.out.println("convertToSvg");
        Converter<SvgLine, Line2D> conv = SvgLine.shapeConverter();
        
        Line2D l1 = new Line2D.Double(1.0, 2.0, 4.0, 3.0);
        SvgLine line = conv.reverse().convert(l1);
        assert line != null;
        assertEquals(1.0, line.getX1(), 1e-6);
        assertEquals(2.0, line.getY1(), 1e-6);
        assertEquals(4.0, line.getX2(), 1e-6);
        assertEquals(3.0, line.getY2(), 1e-6);
        
        assertEquals(null, conv.reverse().convert(null));
    }

    @Test
    public void testConvertFromSvg() {
        System.out.println("convertToSvg");
        Converter<SvgLine, Line2D> conv = SvgLine.shapeConverter();
        
        SvgLine sLine = new SvgLine(1, 2, 3, 4);
        Line2D line = conv.convert(sLine);
        assert line != null;
        assertEquals(1.0, line.getX1(), 1e-6);
        assertEquals(2.0, line.getY1(), 1e-6);
        assertEquals(3.0, line.getX2(), 1e-6);
        assertEquals(4.0, line.getY2(), 1e-6);
        
        assertEquals(null, conv.convert(null));
    }
    
}
