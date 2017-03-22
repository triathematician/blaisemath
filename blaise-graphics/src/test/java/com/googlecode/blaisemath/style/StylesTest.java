/*
 * Copyright 2017 elisha.
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
package com.googlecode.blaisemath.style;

/*
 * #%L
 * blaise-graphics
 * --
 * Copyright (C) 2009 - 2017 Elisha Peterson
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


import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author elisha
 */
public class StylesTest {
    
    public StylesTest() {
    }

    @Test
    public void testHasFill() {
    }

    @Test
    public void testHasStroke() {
    }

    @Test
    public void testFillColorOf() {
    }

    @Test
    public void testStrokeColorOf() {
    }

    @Test
    public void testFontOf() {
    }

    @Test
    public void testSetFont() {
    }

    @Test
    public void testStrokeOf() {
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

    @Test
    public void testDefaultShapeStyle() {
    }

    @Test
    public void testDefaultPathStyle() {
    }

    @Test
    public void testDefaultPointStyle() {
    }

    @Test
    public void testDefaultTextStyle() {
    }

    @Test
    public void testFillStroke_Color_Color() {
    }

    @Test
    public void testFillStroke_3args() {
    }

    @Test
    public void testStrokeWidth() {
    }

    @Test
    public void testText() {
    }

    @Test
    public void testMarker() {
    }

    @Test
    public void testDefaultColorModifier() {
    }

    @Test
    public void testDefaultStrokeModifier() {
    }

    @Test
    public void testDefaultStyleContext() {
    }

    @Test
    public void testWithHighlight() {
    }
    
}
