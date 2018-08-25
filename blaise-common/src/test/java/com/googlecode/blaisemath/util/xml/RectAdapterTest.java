/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.blaisemath.util.xml;

/*
 * #%L
 * BlaiseCommon
 * --
 * Copyright (C) 2014 - 2018 Elisha Peterson
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


import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import junit.framework.TestCase;

/**
 *
 * @author petereb1
 */
public class RectAdapterTest extends TestCase {

    public void testUnmarshal() {
        System.out.println("unmarshal");
        assertEquals(new Rectangle2D.Double(3,4,5,6), new RectAdapter().unmarshal("rectangle[x=3,y=4,w=5,h=6]"));
        assertEquals(new Rectangle2D.Double(3,4,5,6), new RectAdapter().unmarshal("rectangle[x=3.0,y=4.0,w=5.0,   h=6.0]"));
        assertEquals(new Rectangle2D.Double(3,4,-5,-6), new RectAdapter().unmarshal("rectangle[x=3.0,   y=4.0,w=-5.0,h=-6.0]"));
        assertNull(new RectAdapter().unmarshal("3.0,4.0,5.0,6.0"));
        assertNull(new RectAdapter().unmarshal(null));
        assertNull(new RectAdapter().unmarshal(""));
    }

    public void testMarshal() {
        System.out.println("marshal");
        assertEquals("null", new RectAdapter().marshal(null));
        assertEquals("rectangle[x=0.000000,y=0.000000,w=50.000000,h=50.000000]", new RectAdapter().marshal(new Rectangle(0,0,50,50)));
        assertEquals("rectangle[x=0.000000,y=0.000000,w=50.000000,h=50.000000]", new RectAdapter().marshal(new Rectangle2D.Double(0,0,50,50)));
    }
    
}
