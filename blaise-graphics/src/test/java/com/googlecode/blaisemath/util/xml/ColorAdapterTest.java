package com.googlecode.blaisemath.util.xml;

/*
 * #%L
 * BlaiseCommon
 * --
 * Copyright (C) 2009 - 2018 Elisha Peterson
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

    public void testMarshal() throws Exception {
        System.out.println("marshal");
        ColorAdapter ca = new ColorAdapter();
        try {
            ca.marshal(null);
            fail();
        } catch (NullPointerException x) {
            // expected
        }
        assertEquals("#ff0000", ca.marshal(Color.red));
        assertEquals("#0000ff", ca.marshal(Color.blue));
        assertEquals("#ff8080c0", ca.marshal(new Color(255,128,128,192)));
        assertEquals("#00000000", ca.marshal(new Color(0,0,0,0)));
    }

    public void testUnmarshal() throws Exception {
        System.out.println("unmarshal");
        ColorAdapter ca = new ColorAdapter();
        try {
            ca.unmarshal(null);
            fail();
        } catch (NullPointerException x) {
            // expected
        }
        try {
            ca.unmarshal("null");
            fail();
        } catch (IllegalArgumentException x) {
            // expected
        }
        assertEquals(Color.red, ca.unmarshal(ca.marshal(Color.red)));
        assertEquals(Color.blue, ca.unmarshal(ca.marshal(Color.blue)));
        assertEquals(new Color(255,128,128,192), ca.unmarshal(ca.marshal(new Color(255,128,128,192))));
        assertEquals(new Color(0,0,37,0), ca.unmarshal(ca.marshal(new Color(0,0,37,0))));
        try {
            ca.unmarshal("not a color");
            fail();
        } catch (IllegalArgumentException x) {
            // expected
        }
    }
}