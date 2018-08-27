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


import java.awt.geom.Rectangle2D;
import junit.framework.TestCase;

/**
 *
 * @author petereb1
 */
public class Rectangle2DAdapterTest extends TestCase {

    public void testUnmarshal() throws Exception {
        System.out.println("unmarshal");
        assertEquals(new Rectangle2D.Double(3,4,5,6), new Rectangle2DAdapter().unmarshal("rectangle[x=3,y=4,w=5,h=6]"));
        assertEquals(new Rectangle2D.Double(3,4,5,6), new Rectangle2DAdapter().unmarshal("rectangle[x=3.0,y=4.0,w=5.0,   h=6.0]"));
        assertEquals(new Rectangle2D.Double(3,4,-5,-6), new Rectangle2DAdapter().unmarshal("rectangle[x=3.0,   y=4.0,w=-5.0,h=-6.0]"));
        assertNull(new Rectangle2DAdapter().unmarshal("3.0,4.0,5.0,6.0"));
        assertNull(new Rectangle2DAdapter().unmarshal(null));
        assertNull(new Rectangle2DAdapter().unmarshal(""));
    }

    public void testMarshal() throws Exception {
        System.out.println("marshal");
        assertEquals("null", new Rectangle2DAdapter().marshal(null));
        assertEquals("rectangle[x=0.000000,y=0.000000,w=50.000000,h=50.000000]", new Rectangle2DAdapter().marshal(new Rectangle2D.Double(0,0,50,50)));
    }
    
}
