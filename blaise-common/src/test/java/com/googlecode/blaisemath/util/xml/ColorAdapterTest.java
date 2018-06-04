/*
 * To change this template, choose Tools | Templates
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


import java.awt.Color;
import junit.framework.TestCase;

/**
 *
 * @author Elisha Peterson
 */
public class ColorAdapterTest extends TestCase {

    /**
     * Test of marshal method, of class ColorAdapter.
     */
    public void testMarshal() throws Exception {
        System.out.println("marshal");
        ColorAdapter ca = new ColorAdapter();
        assertEquals("null", ca.marshal(null));
        assertEquals("#ff0000", ca.marshal(Color.red));
        assertEquals("#0000ff", ca.marshal(Color.blue));
        assertEquals("#c0ff8080", ca.marshal(new Color(255,128,128,192)));
        assertEquals("#00000000", ca.marshal(new Color(0,0,0,0)));
    }

    /**
     * Test of unmarshal method, of class ColorAdapter.
     */
    public void testUnmarshal() throws Exception {
        System.out.println("unmarshal");
        ColorAdapter ca = new ColorAdapter();
        assertEquals(null, ca.unmarshal(null));
        assertEquals(null, ca.unmarshal("null"));
        assertEquals(Color.red, ca.unmarshal(ca.marshal(Color.red)));
        assertEquals(Color.blue, ca.unmarshal(ca.marshal(Color.blue)));
        assertEquals(new Color(255,128,128,192), ca.unmarshal(ca.marshal(new Color(255,128,128,192))));
        assertEquals(new Color(0,0,37,0), ca.unmarshal(ca.marshal(new Color(0,0,37,0))));
        try {
            assertEquals(null, ca.unmarshal("not a color"));
            fail();
        } catch (IllegalArgumentException x) {
            // expected
        }
    }
}
