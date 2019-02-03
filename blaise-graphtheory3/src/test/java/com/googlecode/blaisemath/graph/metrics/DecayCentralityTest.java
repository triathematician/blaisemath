package com.googlecode.blaisemath.graph.metrics;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2019 Elisha Peterson
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
import static org.junit.Assert.fail;

public class DecayCentralityTest {

    @Test
    public void testGetParameter_setParameter() {
        DecayCentrality instance = new DecayCentrality(0.1);
        assertEquals(0.1, instance.getParameter(), 0.0);
        assertEquals(instance.parameter, instance.getParameter(), 0.0);
        instance.setParameter(0.2);
        assertEquals(0.2, instance.parameter, 0.0);
        try {
            instance.setParameter(1.2);
            fail("Illegal Parameter");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            instance.setParameter(-.2);
            fail("Illegal Parameter");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

}
