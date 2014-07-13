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

package com.googlecode.blaisemath.graphics;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
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


import com.googlecode.blaisemath.graphics.DelegatingPointSetGraphic;
import com.googlecode.blaisemath.coordinate.CoordinateManager;
import org.junit.Test;

/**
 *
 * @author Elisha
 */
public class DelegatingPointSetGraphicTest {
    
    public DelegatingPointSetGraphicTest() {
    }

    @Test
    public void testGetCoordinateManager() {
        System.out.println("getCoordinateManager");
        DelegatingPointSetGraphic instance = new DelegatingPointSetGraphic();
        CoordinateManager result = instance.getCoordinateManager();
    }

    @Test
    public void testSetCoordinateManager() {
        System.out.println("setCoordinateManager");
        // todo
    }

    @Test
    public void testGetObjects() {
        System.out.println("getObjects");
        DelegatingPointSetGraphic instance = new DelegatingPointSetGraphic();
        // todo
    }

    @Test
    public void testGetStyler() {
        System.out.println("getStyler");
        DelegatingPointSetGraphic instance = new DelegatingPointSetGraphic();
        // todo
    }

    @Test
    public void testSetStyler() {
        System.out.println("setStyler");
        DelegatingPointSetGraphic instance = new DelegatingPointSetGraphic();
        // todo
    }

    @Test
    public void testAddObjects() {
        System.out.println("addObjects");
        DelegatingPointSetGraphic instance = new DelegatingPointSetGraphic();
        // todo
    }

    @Test
    public void testInitContextMenu() {
        System.out.println("initContextMenu");
        // todo
    }
    
}
