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
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ClassSerializerTest {

    @Test
    public void testWriteStripsJavaLang() throws IOException {
        assertEquals("\"String\"", BlaiseJson.allMapper().writeValueAsString(String.class));
        assertEquals("\"Integer\"", BlaiseJson.allMapper().writeValueAsString(Integer.class));
        assertEquals("\"Double\"", BlaiseJson.allMapper().writeValueAsString(Double.class));
    }

    @Test
    public void testWriteStripsJavaUtil() throws IOException {
        assertEquals("\"List\"", BlaiseJson.allMapper().writeValueAsString(List.class));
        assertEquals("\"ArrayList\"", BlaiseJson.allMapper().writeValueAsString(ArrayList.class));
    }

    @Test
    public void testWriteFullyQualified() throws IOException {
        assertEquals("\"java.awt.Color\"", BlaiseJson.allMapper().writeValueAsString(Color.class));
    }

    @Test
    public void testReadPrimitives() throws IOException {
        assertEquals(int.class, BlaiseJson.allMapper().readValue("\"int\"", Class.class));
        assertEquals(long.class, BlaiseJson.allMapper().readValue("\"long\"", Class.class));
        assertEquals(double.class, BlaiseJson.allMapper().readValue("\"double\"", Class.class));
        assertEquals(float.class, BlaiseJson.allMapper().readValue("\"float\"", Class.class));
        assertEquals(boolean.class, BlaiseJson.allMapper().readValue("\"boolean\"", Class.class));
        assertEquals(byte.class, BlaiseJson.allMapper().readValue("\"byte\"", Class.class));
        assertEquals(short.class, BlaiseJson.allMapper().readValue("\"short\"", Class.class));
    }

    @Test
    public void testReadJavaLang() throws IOException {
        assertEquals(String.class, BlaiseJson.allMapper().readValue("\"String\"", Class.class));
        assertEquals(Integer.class, BlaiseJson.allMapper().readValue("\"Integer\"", Class.class));
    }

    @Test
    public void testReadJavaUtil() throws IOException {
        assertEquals(List.class, BlaiseJson.allMapper().readValue("\"List\"", Class.class));
        assertEquals(ArrayList.class, BlaiseJson.allMapper().readValue("\"ArrayList\"", Class.class));
    }

    @Test
    public void testRoundtrip() throws IOException {
        assertRoundtrip(String.class);
        assertRoundtrip(Integer.class);
        assertRoundtrip(List.class);
        assertRoundtrip(Color.class);
    }

    private void assertRoundtrip(Class<?> c) throws IOException {
        String json = BlaiseJson.allMapper().writeValueAsString(c);
        assertEquals(c, BlaiseJson.allMapper().readValue(json, Class.class));
    }

}
