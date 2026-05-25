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

import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.AttributeSetCoder;
import org.junit.Test;

import java.awt.Color;
import java.io.IOException;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AttributeSetSerializerTest {

    @Test
    public void testWriteEmpty() throws IOException {
        AttributeSet as = AttributeSet.create(Collections.emptyMap());
        String json = BlaiseJson.allMapper().writeValueAsString(as);
        assertNotNull(json);
    }

    @Test
    public void testRoundtripStringValue() throws IOException {
        AttributeSet as = AttributeSet.create(Collections.singletonMap("name", "hello"));
        String json = BlaiseJson.allMapper().writeValueAsString(as);
        AttributeSet result = BlaiseJson.allMapper().readValue(json, AttributeSet.class);
        assertEquals("hello", result.get("name"));
    }

    @Test
    public void testRoundtripColorValue() throws IOException {
        // Color values are encoded as #RRGGBB strings by AttributeSetCoder;
        // they come back as strings (not Color) after deserialization.
        AttributeSet as = AttributeSet.create(Collections.singletonMap("fill", Color.RED));
        String json = BlaiseJson.allMapper().writeValueAsString(as);
        AttributeSet result = BlaiseJson.allMapper().readValue(json, AttributeSet.class);
        // verify the encoded form round-trips intact
        assertEquals(new AttributeSetCoder().encode(as), new AttributeSetCoder().encode(result));
    }

    @Test
    public void testRoundtripMultipleValues() throws IOException {
        AttributeSet as = new AttributeSet();
        as.put("label", "hello");
        as.put("style", "bold");
        String json = BlaiseJson.allMapper().writeValueAsString(as);
        AttributeSet result = BlaiseJson.allMapper().readValue(json, AttributeSet.class);
        assertEquals("hello", result.get("label"));
        assertEquals("bold", result.get("style"));
    }

}
