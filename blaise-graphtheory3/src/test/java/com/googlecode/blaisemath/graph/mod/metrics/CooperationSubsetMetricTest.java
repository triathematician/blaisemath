/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.blaisemath.graph.mod.metrics;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2018 Elisha Peterson
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

import com.googlecode.blaisemath.graph.mod.metrics.DegreeCentrality;
import java.util.Arrays;
import java.util.HashSet;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.GraphMetrics;
import com.googlecode.blaisemath.graph.SparseGraph;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author elisha
 */
public class CooperationSubsetMetricTest {

    static Graph<Integer> TEST2 = SparseGraph.createFromArrayEdges(false, Arrays.asList(1,2,3,4,5,6,7),
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
    static CooperationMetric INST = new CooperationMetric(GraphMetrics.additiveSubsetMetric(new DegreeCentrality()));

    @Test
    public void testGetValue() {
        System.out.println("getValue");
        double[] result1 = INST.getValue(TEST2, new HashSet(Arrays.asList(1,2,3,4)));
        double[] result2 = INST.getValue(TEST2, new HashSet(Arrays.asList(1,4,5)));
        assertEquals(5, result1.length);
        assertEquals(5, result2.length);
        assertArrayEquals(new double[]{10,2,14,4,2}, result1, 1e-10);
        assertArrayEquals(new double[]{7,5,14,7,2}, result2, 1e-10);
    }
}
