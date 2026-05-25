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

import com.googlecode.blaisemath.palette.MapPalette;
import com.googlecode.blaisemath.palette.Palette;
import org.junit.Test;

import java.awt.Color;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class PaletteSerializerTest {

    @Test
    public void testRoundtripSingleColor() throws IOException {
        Map<String, Color> colors = new LinkedHashMap<>();
        colors.put("primary", Color.RED);
        assertRoundtrip(MapPalette.create(colors));
    }

    @Test
    public void testRoundtripMultipleColors() throws IOException {
        Map<String, Color> colors = new LinkedHashMap<>();
        colors.put("primary", Color.RED);
        colors.put("secondary", Color.BLUE);
        colors.put("accent", Color.GREEN);
        assertRoundtrip(MapPalette.create(colors));
    }

    private void assertRoundtrip(Palette original) throws IOException {
        String json = BlaiseJson.allMapper().writeValueAsString(original);
        Palette result = BlaiseJson.allMapper().readValue(json, Palette.class);
        assertEquals(original.colorMap(), result.colorMap());
    }

}
