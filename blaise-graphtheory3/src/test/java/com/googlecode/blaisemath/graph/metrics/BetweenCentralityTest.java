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

import com.google.common.graph.Graph;
import com.googlecode.blaisemath.graph.GraphUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("UnstableApiUsage")
public class BetweenCentralityTest {

    private static Graph<Integer> TEST2;
    private static BetweenCentrality INST1;

    @BeforeClass
    public static void setUpClass() {
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
        INST1 = new BetweenCentrality();
    }

    @Test
    public void testApply() {
        assertEquals(9.0, INST1.apply(TEST2, 4), 1e-10);
    }

    @Test
    public void testApply_All() {
        Map<Integer,Double> values = INST1.apply(TEST2);
        assertEquals(1.0, values.get(1), 1e-6);
        assertEquals(1.5, values.get(2), 1e-6);
        assertEquals(6.5, values.get(3), 1e-6);
        assertEquals(9.0, values.get(4), 1e-6);
        assertEquals(5.0, values.get(5), 1e-6);
        assertEquals(0.0, values.get(6), 1e-6);
        assertEquals(0.0, values.get(7), 1e-6);
    }

}
