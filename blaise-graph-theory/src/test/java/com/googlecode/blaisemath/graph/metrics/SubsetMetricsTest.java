package com.googlecode.blaisemath.graph.metrics;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2024 Elisha Peterson
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
import com.googlecode.blaisemath.graph.GraphSubsetMetric;
import com.googlecode.blaisemath.graph.GraphUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@SuppressWarnings("UnstableApiUsage")
public class SubsetMetricsTest {

    private static final CooperationMetric<Integer> METRIC1 = new CooperationMetric(SubsetMetrics.additiveSubsetMetric(new Degree()));
    private static final GraphSubsetMetric<Integer> METRIC2 = SubsetMetrics.contractiveSubsetMetric(new Degree());

    private static final Graph<Integer> TEST_GRAPH = GraphUtils.createFromArrayEdges(false, Arrays.asList(1,2,3,4,5,6,7),
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

    @Test
    public void testGetValue_CooperationMetric() {
        double[] result1 = METRIC1.getValue(TEST_GRAPH, new HashSet<>(Arrays.asList(1,2,3,4)));
        double[] result2 = METRIC1.getValue(TEST_GRAPH, new HashSet<>(Arrays.asList(1,4,5)));
        assertEquals(5, result1.length);
        assertEquals(5, result2.length);
        assertArrayEquals(new double[]{10,2,14,4,2}, result1, 1e-10);
        assertArrayEquals(new double[]{7,5,14,7,2}, result2, 1e-10);
    }

    @Test
    public void testGetValue_ContractiveMetric() {
        assertEquals(4, (long) METRIC2.getValue(TEST_GRAPH, new HashSet<>(Arrays.asList(1,2,3,4)))); // 4 not 2 because of the presence of the loop
        assertEquals(4, (long) METRIC2.getValue(TEST_GRAPH, new HashSet<>(Arrays.asList(4,5,6)))); // 4 not 2 because of the presence of the loop
    }

}
