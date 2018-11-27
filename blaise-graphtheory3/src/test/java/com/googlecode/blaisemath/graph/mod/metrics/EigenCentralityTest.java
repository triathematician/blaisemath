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

import com.google.common.graph.Graph;
import com.googlecode.blaisemath.graph.GraphUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class EigenCentralityTest {

    static Graph<Integer> TEST2;
    static EigenCentrality INST;

    @BeforeClass
    public static void setUpClass() {
        INST = new EigenCentrality();
        TEST2 = GraphUtils.createFromArrayEdges(false, Arrays.asList(1,2,3,4,5,6),
                Arrays.asList(
                    new Integer[]{1,2},
                    new Integer[]{1,3},
                    new Integer[]{2,3},
                    new Integer[]{2,6},
                    new Integer[]{3,4},
                    new Integer[]{4,5} ));
        //   1
        //  / \
        // 2---3--4--5
        // |
        // 6
        //
    }

    @Test
    public void testApply() {
        assertEquals(.475349771, INST.apply(TEST2, 1), 1e-8);
        assertEquals(.564129165, INST.apply(TEST2, 3), 1e-8);
        assertEquals(.296008301, INST.apply(TEST2, 4), 1e-8);
    }
    
    @Test
    public void testApply_All() {
        Map<Integer,Double> values = INST.apply(TEST2);
        assertEquals(6, values.size());
        for (int i = 0; i < 6; i++)
            assertEquals(INST.apply(TEST2, i+1), values.get(i+1));
    }

}
