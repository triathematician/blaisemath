package com.googlecode.blaisemath.graphics.core;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2016 Elisha Peterson
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


import com.googlecode.blaisemath.util.coordinate.CoordinateManager;
import org.junit.Test;

/**
 *
 * @author Elisha
 */
public class DelegatingPointSetGraphicTest {

    @Test
    public void testGetCoordinateManager() {
        System.out.println("getCoordinateManager");
        DelegatingPointSetGraphic instance = new DelegatingPointSetGraphic();
        CoordinateManager result = instance.getCoordinateManager();
    }
    
}
