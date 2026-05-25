package com.googlecode.blaisemath.json;

/*-
 * #%L
 * blaise-json
 * --
 * Copyright (C) 2019 - 2026 Elisha Peterson
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

import java.awt.*;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class FontDeserializerTest {

    @Test
    public void testRead() throws IOException {
        Font f = BlaiseJson.allMapper().convertValue("Serif-BOLD-18", Font.class);
        assertEquals(18, f.getSize());
        assertEquals("Serif", f.getFamily());
        assertEquals(Font.BOLD, f.getStyle());
    }

    @Test
    public void testWrite() throws IOException {
        assertEquals("\"Serif-BOLD-18\"", BlaiseJson.allMapper().writeValueAsString(new Font("Serif", 1, 18)));
    }

    @Test
    public void testRoundtrip() throws IOException {
        assertRoundtrip(new Font("Serif", Font.BOLD, 18));
        assertRoundtrip(new Font("SansSerif", Font.PLAIN, 12));
        assertRoundtrip(new Font("Monospaced", Font.ITALIC, 14));
    }

    private void assertRoundtrip(Font f) throws IOException {
        String json = BlaiseJson.allMapper().writeValueAsString(f);
        Font result = BlaiseJson.allMapper().readValue(json, Font.class);
        assertEquals(f.getFamily(), result.getFamily());
        assertEquals(f.getStyle(), result.getStyle());
        assertEquals(f.getSize(), result.getSize());
    }

}
