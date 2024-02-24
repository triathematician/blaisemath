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
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("UnstableApiUsage")
public class AdditiveSubsetMetricTest {

    private static Graph<Integer> TEST2;
    private static GraphSubsetMetric INST;

    @BeforeClass
    public static void setUpClass() {
        INST = SubsetMetrics.additiveSubsetMetric(new Degree());
        TEST2 = GraphUtils.createFromArrayEdges(false, Arrays.asList(1,2,3,4,5,6,7),
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
    }

    @Test
    public void testGetValue() {
        assertEquals(10, INST.getValue(TEST2, new HashSet<>(Arrays.asList(1,2,3,4))));
        assertEquals(6, INST.getValue(TEST2, new HashSet<>(Arrays.asList(4,5,6))));
    }

}
