package com.googlecode.blaisemath.style;

/*
 * #%L
 * blaise-graphics
 * --
 * Copyright (C) 2014 - 2020 Elisha Peterson
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


import com.googlecode.blaisemath.primitive.Anchor;
import org.junit.Test;
import static org.junit.Assert.*;

public class StylesTest {
    
    public StylesTest() {
    }

    @Test
    public void testAnchorOf() {
        assertEquals(Anchor.CENTER, Styles.anchorOf(AttributeSet.of("text-anchor","middle","alignment-baseline","middle"), Anchor.SOUTH));
        assertEquals(Anchor.SOUTH, Styles.anchorOf(AttributeSet.of("text-anchor","x","alignment-baseline","x"), Anchor.SOUTH));
    }

    @Test
    public void testToAnchor() {
        assertEquals(Anchor.CENTER, Styles.toAnchor("middle", "middle"));
        assertEquals(Anchor.SOUTHWEST, Styles.toAnchor("x", "x"));
    }

    @Test
    public void testToTextAnchor() {
        assertEquals("middle", Styles.toTextAnchor(Anchor.CENTER));
        assertEquals("middle", Styles.toTextAnchor("CENTER"));
        assertEquals("start", Styles.toTextAnchor("x"));
    }

    @Test
    public void testToAlignBaseline() {
        assertEquals("middle", Styles.toAlignBaseline(Anchor.CENTER));
        assertEquals("middle", Styles.toAlignBaseline("CENTER"));
        assertEquals("baseline", Styles.toAlignBaseline("x"));
    }

}
