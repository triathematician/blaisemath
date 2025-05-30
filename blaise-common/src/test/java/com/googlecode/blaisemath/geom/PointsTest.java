package com.googlecode.blaisemath.geom;

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

import com.googlecode.blaisemath.geom.Points;
import java.awt.Point;
import java.awt.geom.Point2D;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class PointsTest {

    @Test
    public void testFormatPoint() {
        System.out.println("formatPoint");
        assertEquals("(0, 0)", Points.format(new Point(), 0));
        assertEquals("(0.0, 0.0)", Points.format(new Point(), 1));
        assertEquals("(1.02, -3.00)", Points.format(new Point2D.Double(1.02,-3), 2));
    }
    
}
