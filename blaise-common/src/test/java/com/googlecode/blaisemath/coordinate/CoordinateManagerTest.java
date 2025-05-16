package com.googlecode.blaisemath.coordinate;

/*
 * #%L
 * BlaiseCommon
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

import static org.junit.Assert.assertEquals;

public class CoordinateManagerTest {

    private final CoordinateManager inst = CoordinateManager.create(50);

    @Test
    public void testCreate() {
        System.out.println("create");
        assertEquals(50, inst.getMaxCacheSize());
    }

    @Test
    public void testGetMaxCacheSize() {
        System.out.println("getMaxCacheSize");
        assertEquals(50, inst.getMaxCacheSize());
    }
//
//    @Test
//    public void testGetActive() {
//        System.out.println("getActive");
//    }
//
//    @Test
//    public void testGetInactive() {
//        System.out.println("getInactive");
//    }
//
//    @Test
//    public void testLocatesAll() {
//        System.out.println("locatesAll");
//    }
//
//    @Test
//    public void testGetActiveLocationCopy() {
//        System.out.println("getActiveLocationCopy");
//    }
//
//    @Test
//    public void testGetLocationCopy() {
//        System.out.println("getLocationCopy");
//    }
//
//    @Test
//    public void testGetInactiveLocationCopy() {
//        System.out.println("getInactiveLocationCopy");
//    }
//
//    @Test
//    public void testPut() {
//        System.out.println("put");
//    }
//
//    @Test
//    public void testPutAll() {
//        System.out.println("putAll");
//    }
//
//    @Test
//    public void testSetCoordinateMap() {
//        System.out.println("setCoordinateMap");
//    }
//
//    @Test
//    public void testForget() {
//        System.out.println("forget");
//    }
//
//    @Test
//    public void testDeactivate() {
//        System.out.println("deactivate");
//    }
//
//    @Test
//    public void testReactivate() {
//        System.out.println("reactivate");
//    }
//
//    @Test
//    public void testFireCoordinatesChanged() {
//        System.out.println("fireCoordinatesChanged");
//    }
//
//    @Test
//    public void testAddCoordinateListener() {
//        System.out.println("addCoordinateListener");
//    }
//
//    @Test
//    public void testRemoveCoordinateListener() {
//        System.out.println("removeCoordinateListener");
//    }
    
}
