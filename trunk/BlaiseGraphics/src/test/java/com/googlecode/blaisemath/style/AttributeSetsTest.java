/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.blaisemath.style;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2015 Elisha Peterson
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


import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Elisha
 */
public class AttributeSetsTest {

    @Test
    public void testConvertToText() throws Exception {
        System.out.println("testConvertToText");
        assertEquals("fill:#ff0000; stroke:#00ff00", 
                AttributeSets.stringConverter().convert(Styles.fillStroke(Color.red, Color.green)));
    }

    @Test
    public void testConvertFromText() throws Exception {
        System.out.println("testConvertFromText");
        AttributeSet as = AttributeSets.stringConverter().reverse().convert("fill:  #ff0000 ; stroke :#00ff00;");
        assertEquals(2, as.getAttributes().size());
        assertEquals(Color.red, as.get(Styles.FILL));
        assertEquals(Color.green, as.get(Styles.STROKE));
    }
    
    @Test
    public void testConvertFromPoint() {
        System.out.println("testConvertFromPoint");
        Object res = AttributeSets.valueConverter().reverse().convert("!point[5,6]");
        assertEquals(new Point2D.Double(5, 6), res);
    }
    
    @Test
    public void testConvertFromRect() {
        System.out.println("testConvertFromRect");
        Object res = AttributeSets.valueConverter().reverse().convert("!rectangle[x=5,y=6,w=7,h=8]");
        assertEquals(new Rectangle2D.Double(5, 6, 7, 8), res);
    }
    
}
