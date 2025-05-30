package com.googlecode.blaisemath.primitive;

/*-
 * #%L
 * blaise-common
 * --
 * Copyright (C) 2014 - 2025 Elisha Peterson
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
import java.util.List;

import static org.junit.Assert.*;

public class MarkersTest {

    @Test
    public void testGetAvailableMarkers() {
        List<Marker> result = Markers.getAvailableMarkers();
        assertFalse(result.isEmpty());
        for (Marker m : result) {
            Shape s = m.create(new Point(), .5, 1f);
            assertNotNull(s);
        }
        assertEquals(20, result.size());
    }
    
}
