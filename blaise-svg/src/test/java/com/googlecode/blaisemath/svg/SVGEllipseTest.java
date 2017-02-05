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
 * Copyright (C) 2014 - 2017 Elisha Peterson
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
import java.awt.geom.Ellipse2D;
import junit.framework.TestCase;
import org.junit.Test;

/**
 *
 * @author elisha
 */
public class SVGEllipseTest extends TestCase {

    @Test
    public void testConvertToSvg() {
        System.out.println("convertToSvg");
        Converter<SVGEllipse, Ellipse2D> conv = SVGEllipse.shapeConverter();
        
        Ellipse2D e1 = new Ellipse2D.Double(1.0, 2.0, 2.0, 3.0);
        SVGEllipse circ = conv.reverse().convert(e1);
        assert circ != null;
        assertEquals(2.0, circ.getCx(), 1e-6);
        assertEquals(3.5, circ.getCy(), 1e-6);
        assertEquals(1.0, circ.getRx(), 1e-6);
        assertEquals(1.5, circ.getRy(), 1e-6);
        
        assertEquals(null, conv.reverse().convert(null));
    }

    @Test
    public void testConvertFromSvg() {
        System.out.println("convertToSvg");
        Converter<SVGEllipse, Ellipse2D> conv = SVGEllipse.shapeConverter();
        
        SVGEllipse sEll = new SVGEllipse(1, 2, 3, 2);
        Ellipse2D ell = conv.convert(sEll);
        assert ell != null;
        assertEquals(-2.0, ell.getMinX(), 1e-6);
        assertEquals(0.0, ell.getMinY(), 1e-6);
        assertEquals(6.0, ell.getWidth(), 1e-6);
        assertEquals(4.0, ell.getHeight(), 1e-6);
        
        assertEquals(null, conv.convert(null));
    }
    
}
