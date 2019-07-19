package com.googlecode.blaisemath.json;

/*-
 * #%L
 * blaise-json
 * --
 * Copyright (C) 2019 Elisha Peterson
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

import com.fasterxml.jackson.core.JsonProcessingException;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;

public class PointSerializerTest {

    @Test
    public void testRead() throws IOException {
        assertEquals(new Point(1, 2), BlaiseJson.allMapper().readValue("{\"x\":1,\"y\":2}", Point.class));
        assertEquals(new Point2D.Double(1, 2), BlaiseJson.allMapper().readValue("{\"x\":1.0,\"y\":2.0}", Point2D.class));
        assertEquals(new Point2D.Double(1, 2), BlaiseJson.allMapper().readValue("{\"x\":1.0,\"y\":2.0}", Point2D.Double.class));
    }
    
    @Test
    public void testWrite() throws JsonProcessingException {
        assertEquals("{\"x\":1,\"y\":2}", BlaiseJson.allMapper().writeValueAsString(new Point(1, 2)));
        assertEquals("{\"x\":1.0,\"y\":2.0}", BlaiseJson.allMapper().writeValueAsString(new Point2D.Double(1, 2)));
    }
    
}
