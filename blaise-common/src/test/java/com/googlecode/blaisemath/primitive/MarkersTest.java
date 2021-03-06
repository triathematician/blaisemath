package com.googlecode.blaisemath.primitive;

/*-
 * #%L
 * blaise-common
 * --
 * Copyright (C) 2014 - 2021 Elisha Peterson
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

import com.googlecode.blaisemath.primitive.Markers;
import com.googlecode.blaisemath.primitive.Marker;
import org.junit.Assert;
import org.junit.Test;

import java.awt.*;
import java.util.List;

public class MarkersTest {

    @Test
    public void testGetAvailableMarkers() {
        System.out.println("getAvailableMarkers");
        List<Marker> result = Markers.getAvailableMarkers();
        Assert.assertFalse(result.isEmpty());
        for (Marker m : result) {
            Shape s = m.create(new Point(), .5, 1f);
            Assert.assertNotNull(s);
        }
    }
    
}
