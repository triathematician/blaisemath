package com.googlecode.blaisemath.json;

/*-
 * #%L
 * blaise-json
 * --
 * Copyright (C) 2019 - 2022 Elisha Peterson
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

import java.awt.geom.Rectangle2D;
import org.junit.Test;
import static org.junit.Assert.*;

public class RectProxyTest {

    @Test
    public void testToRange() {
        Rectangle2DProxy rp = new Rectangle2DProxy();
        rp.setX(1);
        rp.setY(2);
        rp.setWidth(3);
        rp.setHeight(3);
        
        assertEquals(new Rectangle2D.Double(1,2,3,3), rp.toRectangle());
    }
    
    @Test
    public void testCreate() {
        Rectangle2DProxy rp = new Rectangle2DProxy(new Rectangle2D.Double());
        assertEquals(0.0, rp.getX(), 1e-6);
        assertEquals(0.0, rp.getY(), 1e-6);
        assertEquals(0.0, rp.getWidth(), 1e-6);
        assertEquals(0.0, rp.getHeight(), 1e-6);
    }
    
}
