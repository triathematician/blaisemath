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

import java.awt.Insets;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class InsetsDeserializerTest {

    @Test
    public void testReadPartial() throws IOException {
        assertEquals(new Insets(1, 2, 0, 0),
                BlaiseJson.allMapper().readValue("{\"top\":1,\"left\":2}", Insets.class));
    }

    @Test
    public void testReadAllFields() throws IOException {
        assertEquals(new Insets(5, 10, 15, 20),
                BlaiseJson.allMapper().readValue("{\"top\":5,\"left\":10,\"bottom\":15,\"right\":20}", Insets.class));
    }

    @Test
    public void testRoundtrip() throws IOException {
        assertRoundtrip(new Insets(5, 10, 15, 20));
        assertRoundtrip(new Insets(0, 0, 0, 0));
        assertRoundtrip(new Insets(1, 2, 3, 4));
    }

    private void assertRoundtrip(Insets insets) throws IOException {
        // Insets serializes via default Jackson (public fields), deserializes via InsetsProxy
        String json = BlaiseJson.allMapper().writeValueAsString(insets);
        assertEquals(insets, BlaiseJson.allMapper().readValue(json, Insets.class));
    }

}
