/*
 * Copyright 2014 Elisha.
 *
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
 */

package org.blaise.graphics;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
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


import org.blaise.util.CoordinateManager;
import org.junit.Test;

/**
 *
 * @author Elisha
 */
public class DelegatingEdgeSetGraphicTest {
    
    public DelegatingEdgeSetGraphicTest() {
    }

    @Test
    public void testGetCoordinateManager() {
        System.out.println("getCoordinateManager");
        DelegatingEdgeSetGraphic instance = new DelegatingEdgeSetGraphic();
        CoordinateManager result = instance.getCoordinateManager();
    }

    @Test
    public void testSetCoordinateManager() {
        System.out.println("setCoordinateManager");
        // TODO
    }

    @Test
    public void testGetEdges() {
        System.out.println("getEdges");
        // TODO
    }

    @Test
    public void testSetEdges() {
        System.out.println("setEdges");
        // TODO
    }

    @Test
    public void testGetEdgeStyler() {
        System.out.println("getEdgeStyler");
        // TODO
    }

    @Test
    public void testSetEdgeStyler() {
        System.out.println("setEdgeStyler");
        // TODO
    }
    
}
