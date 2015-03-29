/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.blaisemath.graph.mod.generators;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2015 Elisha Peterson
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

import com.googlecode.blaisemath.graph.mod.generators.WattsStrogatzGenerator;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.mod.generators.WattsStrogatzGenerator.WattsStrogatzParameters;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author elisha
 */
public class WattsStrogatzGeneratorTest {
    
    @Test
    public void testWattsStrogatzGenerator() {
        System.out.println("-- WattsStrogatzGeneratorTest --");
        System.out.println("getInstance: MANUALLY CHECK FOR DESIRED OUTPUT");
        Graph<Integer> result1 = new WattsStrogatzGenerator().generate(new WattsStrogatzParameters(false, 10, 2, 0f));
        assertEquals(10, result1.nodeCount());
        assertEquals(10, result1.edgeCount());
        for (int i = 0; i < 10; i++) {
            assertTrue(result1.adjacent(i, (i + 1) % 10));
        }
        Graph<Integer> result2 = new WattsStrogatzGenerator().generate(new WattsStrogatzParameters(false, 50, 4, .5f));
        System.out.println(result2);
        assertEquals(50, result2.nodeCount());
        assertEquals(100, result2.edgeCount());
    }
    
}
