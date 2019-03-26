/*
 * Copyright 2014 Elisha.
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

package com.googlecode.blaisemath.util;

/*
 * #%L
 * BlaiseGraphics
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


import com.google.common.base.Converter;
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
    public void testConvertToString() {
        System.out.println("convertToString");
        assertEquals(null, Colors.stringConverter().convert(null));
        assertEquals("#ff0000", Colors.stringConverter().convert(Color.red));
        assertEquals("#00ff00", Colors.stringConverter().convert(Color.green));
        assertEquals("#0000ff", Colors.stringConverter().convert(Color.blue));
        assertEquals("#04010203", Colors.stringConverter().convert(new Color(1,2,3,4)));
    }

    @Test
    public void testConvertFromString() {
        System.out.println("convertFromString");
        Converter<String, Color> rev = Colors.stringConverter().reverse();
        assertEquals(null, rev.convert(null));
        assertEquals(Color.red, rev.convert("#ff0000"));
        assertEquals(Color.green, rev.convert("#00ff00"));
        assertEquals(Color.blue, rev.convert("#0000ff"));
        assertEquals(new Color(0,0,255,128), rev.convert("#800000ff"));
        assertEquals(Color.blue, rev.convert("#00f"));
        assertEquals(rev.convert("#ff0033"), rev.convert("#f03"));
        try {
            assertEquals(null, rev.convert("null"));
            fail();
        } catch (IllegalArgumentException x) {
            // expected
        }        
        try {
            assertEquals(null, rev.convert("not a color"));
            fail();
        } catch (IllegalArgumentException x) {
            // expected
        }
    }

}
