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


import java.awt.Point;
import java.awt.geom.Point2D;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import junit.framework.TestCase;

/**
 *
 * @author petereb1
 */
public class Point2DAdapterTest extends TestCase {

    public void testUnmarshal() {
        System.out.println("unmarshal");
        assertEquals(new Point(3,4), new Point2DAdapter().unmarshal("point[3,4]"));
        assertEquals(new Point(3,4), new Point2DAdapter().unmarshal("point[3.0,4.0]"));
        assertNull(new PointAdapter().unmarshal("3.0,4.0"));
        assertNull(new PointAdapter().unmarshal(null));
        assertNull(new PointAdapter().unmarshal(""));
    }

    public void testMarshal() {
        System.out.println("marshal");
        assertEquals("null", new Point2DAdapter().marshal(null));
        assertEquals("point[3.000000,4.000000]", new Point2DAdapter().marshal(new Point2D.Double(3,4)));
    }
    
}
