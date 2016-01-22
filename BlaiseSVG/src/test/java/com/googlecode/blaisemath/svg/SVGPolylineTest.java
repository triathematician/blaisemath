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
 * Copyright (C) 2014 - 2016 Elisha Peterson
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
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import junit.framework.TestCase;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import org.junit.Test;

/**
 *
 * @author elisha
 */
public class SVGPolylineTest extends TestCase {

    @Test
    public void testConvertToSvg() {
        System.out.println("convertToSvg");
        Converter<SVGPolyline, GeneralPath> conv = SVGPolyline.shapeConverter();
        
        GeneralPath gp1 = new GeneralPath();
        gp1.moveTo(0, 0);
        gp1.lineTo(1, 1);
        gp1.closePath();
        assertEquals("0,0 1,1", conv.reverse().convert(gp1).getPointStr());
        
        GeneralPath gp2 = new GeneralPath();
        gp2.moveTo(0, 0);
        gp2.curveTo(0, 0, 1, 1, 2, 2);
        try {
            conv.reverse().convert(gp2);
            fail("Shouldn't be able to convert.");
        } catch (IllegalArgumentException x) {
            // expected
        }
        
        assertEquals(null, conv.reverse().convert(null));        
    }

    @Test
    public void testConvertFromSvg() {
        System.out.println("convertToSvg");
        Converter<SVGPolyline, GeneralPath> conv = SVGPolyline.shapeConverter();
        
        SVGPolyline poly1 = new SVGPolyline("0,0 1,0 0,1");
        GeneralPath gp1 = conv.convert(poly1);
        PathIterator pi = gp1.getPathIterator(null);
        double[] crd = new double[6];
        assertEquals(PathIterator.SEG_MOVETO, pi.currentSegment(crd));
        assertEquals(0.0, crd[0]);
        assertEquals(0.0, crd[1]);
        pi.next();
        assertEquals(PathIterator.SEG_LINETO, pi.currentSegment(crd));
        assertEquals(1.0, crd[0]);
        assertEquals(0.0, crd[1]);
        pi.next();
        assertEquals(PathIterator.SEG_LINETO, pi.currentSegment(crd));
        assertEquals(0.0, crd[0]);
        assertEquals(1.0, crd[1]);
        pi.next();
        assertTrue(pi.isDone());
        
        try {
            new SVGPolyline("not points");
            fail("Shouldn't be able to create");
        } catch (IllegalArgumentException x) {
            // expected
        }
        
        try {
            new SVGPolyline("1,2,3 4,5 6,7");
            fail("Shouldn't be able to create");
        } catch (IllegalArgumentException x) {
            // expected
        }
        
        try {
            new SVGPolyline("1,a 4,5 6,7");
            fail("Shouldn't be able to create");
        } catch (IllegalArgumentException x) {
            // expected
        }
        
        assertEquals(null, conv.convert(null));
    }
    
}
