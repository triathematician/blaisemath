package com.googlecode.blaisemath.util.encode;

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


import com.googlecode.blaisemath.util.*;
import java.awt.Color;
import junit.framework.TestCase;
import org.junit.Test;

/**
 *
 * @author Elisha
 */
public class ColorCoderTest extends TestCase {

    @Test
    public void testEncode() {
        System.out.println("encode");
        assertNPE(() -> new ColorCoder().encode(null));
        assertEquals("#ff0000", new ColorCoder().encode(Color.red));
        assertEquals("#00ff00", new ColorCoder().encode(Color.green));
        assertEquals("#0000ff", new ColorCoder().encode(Color.blue));
        assertEquals("#01020304", new ColorCoder().encode(new Color(1,2,3,4)));
    }

    @Test
    public void testDecode() {
        System.out.println("decode");
        assertEquals(Color.red, new ColorCoder().decode("ff0000"));
        assertEquals(Color.red, new ColorCoder().decode("#ff0000"));
        assertEquals(Color.green, new ColorCoder().decode("#00ff00"));
        assertEquals(Color.blue, new ColorCoder().decode("#0000ff"));
        assertEquals(new Color(0,0,255,128), new ColorCoder().decode("#0000ff80"));
        assertEquals(Color.blue, new ColorCoder().decode("#00f"));
        assertEquals(Colors.decode("#ff0033"), new ColorCoder().decode("#f03"));
        assertEquals(Color.blue, new ColorCoder().decode("blue"));
        assertEquals(new Color(218, 165, 32), new ColorCoder().decode("goldenrod"));
        assertIllegal(() -> new ColorCoder().decode("null"));
        assertIllegal(() -> new ColorCoder().decode("not a color"));
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
