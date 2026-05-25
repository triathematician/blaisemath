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

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class RectangleSerializerTest {

    @Test
    public void testRectangleWrite() throws IOException {
        assertEquals("{\"height\":4,\"width\":3,\"x\":1,\"y\":2}",
                BlaiseJson.allMapper().writeValueAsString(new Rectangle(1, 2, 3, 4)));
        assertEquals("{\"height\":200,\"width\":100,\"x\":0,\"y\":0}",
                BlaiseJson.allMapper().writeValueAsString(new Rectangle(0, 0, 100, 200)));
    }

    @Test
    public void testRectangleRead() throws IOException {
        assertEquals(new Rectangle(1, 2, 3, 4),
                BlaiseJson.allMapper().readValue("{\"x\":1,\"y\":2,\"width\":3,\"height\":4}", Rectangle.class));
        assertEquals(new Rectangle(0, 0, 0, 0),
                BlaiseJson.allMapper().readValue("{}", Rectangle.class));
    }

    @Test
    public void testRectangleRoundtrip() throws IOException {
        assertRoundtrip(new Rectangle(0, 0, 100, 200));
        assertRoundtrip(new Rectangle(10, 20, 30, 40));
        assertRoundtrip(new Rectangle(-5, -5, 10, 10));
    }

    @Test
    public void testRectangle2DWrite() throws IOException {
        assertEquals("{\"height\":4.0,\"width\":3.0,\"x\":1.0,\"y\":2.0}",
                BlaiseJson.allMapper().writeValueAsString(new Rectangle2D.Double(1, 2, 3, 4)));
    }

    @Test
    public void testRectangle2DRead() throws IOException {
        assertEquals(new Rectangle2D.Double(1, 2, 3, 4),
                BlaiseJson.allMapper().readValue("{\"x\":1.0,\"y\":2.0,\"width\":3.0,\"height\":4.0}", Rectangle2D.Double.class));
    }

    @Test
    public void testRectangle2DRoundtrip() throws IOException {
        assertRoundtrip2D(new Rectangle2D.Double(0, 0, 100, 200));
        assertRoundtrip2D(new Rectangle2D.Double(1.5, 2.5, 3.5, 4.5));
        assertRoundtrip2D(new Rectangle2D.Double(-1, -1, 10, 10));
    }

    private void assertRoundtrip(Rectangle r) throws IOException {
        String json = BlaiseJson.allMapper().writeValueAsString(r);
        assertEquals(r, BlaiseJson.allMapper().readValue(json, Rectangle.class));
    }

    private void assertRoundtrip2D(Rectangle2D.Double r) throws IOException {
        String json = BlaiseJson.allMapper().writeValueAsString(r);
        assertEquals(r, BlaiseJson.allMapper().readValue(json, Rectangle2D.Double.class));
    }

}
