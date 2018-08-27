package com.googlecode.blaisemath.util;

/*
 * #%L
 * BlaiseGraphics
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
import org.junit.Test;

/**
 *
 * @author Elisha
 */
public class ColorsTest extends TestCase {

    @Test
    public void testLighterThan() {
        System.out.println("lighterThan");
        assertEquals(Color.white, Colors.lighterThan(Color.white));
        assertEquals(Color.darkGray, Colors.lighterThan(Color.black));
        assertEquals(new Color(114,64,64,128), Colors.lighterThan(new Color(50,0,0,128)));
    }

    @Test
    public void testBlanderThan() {
        System.out.println("blanderThan");
        assertEquals(Color.white, Colors.blanderThan(Color.white));
        assertEquals(Color.black, Colors.blanderThan(Color.black));
        assertEquals(new Color(50,25,25,128), Colors.blanderThan(new Color(50,0,0,128)));
    }
    
    @Test
    public void testAlpha() {
        System.out.println("alphas");
        assertEquals(new Color(255, 255, 255, 0), Colors.alpha(Color.white, 0));
    }
    
    @Test
    public void testInterpolate() {
        System.out.println("interpolate");
        assertEquals(Color.green, Colors.interpolate(Color.red, 0f, Color.green));
        assertEquals(new Color(77, 179, 0), Colors.interpolate(Color.red, .3f, Color.green));
        assertEquals(new Color(128, 128, 0), Colors.interpolate(Color.red, .5f, Color.green));
        assertEquals(Color.red, Colors.interpolate(Color.red, 1f, Color.green));
    }

    @Test
    public void testToString() {
        System.out.println("toString");
        assertEquals("#ff0000", Colors.encode(Color.red));
        assertEquals("#00ff00", Colors.encode(Color.green));
        assertEquals("#0000ff", Colors.encode(Color.blue));
        assertEquals("#01020304", Colors.encode(new Color(1,2,3,4)));
    }

    @Test
    public void testEncode() {
        System.out.println("encode");
        assertNPE(() -> Colors.encode(null));
        assertEquals("#ff0000", Colors.encode(Color.red));
        assertEquals("#00ff00", Colors.encode(Color.green));
        assertEquals("#0000ff", Colors.encode(Color.blue));
        assertEquals("#01020304", Colors.encode(new Color(1,2,3,4)));
    }

    @Test
    public void testDecode() {
        System.out.println("decode");
        assertEquals(Color.red, Colors.decode("ff0000"));
        assertEquals(Color.red, Colors.decode("#ff0000"));
        assertEquals(Color.green, Colors.decode("#00ff00"));
        assertEquals(Color.blue, Colors.decode("#0000ff"));
        assertEquals(new Color(0,0,255,128), Colors.decode("#0000ff80"));
        assertEquals(Color.blue, Colors.decode("#00f"));
        assertEquals(Colors.decode("#ff0033"), Colors.decode("#f03"));
        assertEquals(Color.blue, Colors.decode("blue"));
        assertEquals(new Color(218, 165, 32), Colors.decode("goldenrod"));
        assertIllegal(() -> Colors.decode("null"));
        assertIllegal(() -> Colors.decode("not a color"));
    }
    
    private static void assertIllegal(Runnable r) {
        try {
            r.run();
            fail();
        } catch (IllegalArgumentException x) {
            // expected
        }
    }
    
    private static void assertNPE(Runnable r) {
        try {
            r.run();
            fail();
        } catch (NullPointerException x) {
            // expected
        }
    }

}
