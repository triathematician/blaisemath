package com.googlecode.blaisemath.json;

/*-
 * #%L
 * blaise-json
 * --
 * Copyright (C) 2019 - 2020 Elisha Peterson
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
    
}
