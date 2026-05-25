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

import java.awt.Color;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ColorSerializerTest {

    @Test
    public void testWrite() throws IOException {
        assertEquals("\"#ff0000\"", BlaiseJson.allMapper().writeValueAsString(Color.RED));
        assertEquals("\"#0000ff\"", BlaiseJson.allMapper().writeValueAsString(Color.BLUE));
        assertEquals("\"#00ff00\"", BlaiseJson.allMapper().writeValueAsString(Color.GREEN));
    }

    @Test
    public void testRead() throws IOException {
        assertEquals(Color.RED, BlaiseJson.allMapper().readValue("\"#ff0000\"", Color.class));
        assertEquals(Color.BLUE, BlaiseJson.allMapper().readValue("\"#0000ff\"", Color.class));
        assertEquals(Color.GREEN, BlaiseJson.allMapper().readValue("\"#00ff00\"", Color.class));
    }

    @Test
    public void testRoundtrip() throws IOException {
        assertRoundtrip(Color.RED);
        assertRoundtrip(Color.BLUE);
        assertRoundtrip(Color.GREEN);
        assertRoundtrip(Color.WHITE);
        assertRoundtrip(Color.BLACK);
        assertRoundtrip(new Color(100, 150, 200));
    }

    private void assertRoundtrip(Color c) throws IOException {
        String json = BlaiseJson.allMapper().writeValueAsString(c);
        assertEquals(c, BlaiseJson.allMapper().readValue(json, Color.class));
    }

}
