/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.blaisemath.graph.modules.metrics;

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

import com.googlecode.blaisemath.graph.modules.metrics.GraphCentrality;
import com.googlecode.blaisemath.graph.modules.metrics.ClosenessCentrality;
import java.util.Arrays;
import java.util.Map;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.SparseGraph;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author elisha
 */
public class ClosenessCentralityTest {

    static Graph<Integer> TEST2;
    static ClosenessCentrality INST1;
    static GraphCentrality INST2;

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("-- ClosenessCentralityTest --");
        TEST2 = SparseGraph.createFromArrayEdges(false, Arrays.asList(1,2,3,4,5,6,7),
                Arrays.asList(
                    new Integer[]{1,2},
                    new Integer[]{1,3},
                    new Integer[]{2,4},
                    new Integer[]{3,4},
                    new Integer[]{3,7},
                    new Integer[]{4,5},
                    new Integer[]{5,6} ));
        // 1--2
        // |  |
        // 3--4--5--6
        // |
        // 7
        INST1 = new ClosenessCentrality();
        INST2 = new GraphCentrality(); // TODO - use max set to true here ??
    }

    @Test
    public void testValue() {
        System.out.println("value");
        assertEquals(1.0/2, INST2.apply(TEST2, 4), 1e-10);
    }

    @Test
    public void testAllValues() {
        System.out.println("allValues");
        Map<Integer,Double> vals = INST1.allValues(TEST2);
        assertEquals(7, vals.size());
        for (int i = 0; i < 7; i++)
            assertEquals(INST1.apply(TEST2, i+1), vals.get(i+1), 1e-10);
        Map<Integer,Double> vals2 = INST2.allValues(TEST2);
        assertEquals(7, vals2.size());
        for (int i = 0; i < 7; i++)
            assertEquals(INST2.apply(TEST2, i+1), vals2.get(i+1), 1e-10);
    }

}
