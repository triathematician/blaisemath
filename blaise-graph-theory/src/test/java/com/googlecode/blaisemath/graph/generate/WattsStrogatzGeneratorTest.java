package com.googlecode.blaisemath.graph.generate;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2025 Elisha Peterson
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

import com.google.common.graph.Graph;
import com.googlecode.blaisemath.graph.generate.WattsStrogatzGenerator.WattsStrogatzParameters;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("UnstableApiUsage")
public class WattsStrogatzGeneratorTest {
    
    @Test
    public void testWattsStrogatzGenerator() {
        System.out.println("getInstance: MANUALLY CHECK FOR DESIRED OUTPUT");
        Graph<Integer> result1 = new WattsStrogatzGenerator().apply(new WattsStrogatzParameters(false, 10, 2, 0f));
        assertEquals(10, result1.nodes().size());
        assertEquals(10, result1.edges().size());
        for (int i = 0; i < 10; i++) {
            assertTrue(result1.hasEdgeConnecting(i, (i + 1) % 10));
        }
        Graph<Integer> result2 = new WattsStrogatzGenerator().apply(new WattsStrogatzParameters(false, 50, 4, .5f));
        System.out.println(result2);
        assertEquals(50, result2.nodes().size());
        assertEquals(100, result2.edges().size());
    }
    
}
