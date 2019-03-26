/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.blaisemath.graph.mod.metrics;

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
public class EigenCentralityTest {

    static Graph<Integer> TEST2;
    static EigenCentrality INST;

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("-- EigenCentralityTest --");
        INST = new EigenCentrality();
        TEST2 = SparseGraph.createFromArrayEdges(false, Arrays.asList(1,2,3,4,5,6),
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
        System.out.println("testApply");
        assertEquals(.475349771, INST.apply(TEST2, 1), 1e-8);
        assertEquals(.564129165, INST.apply(TEST2, 3), 1e-8);
        assertEquals(.296008301, INST.apply(TEST2, 4), 1e-8);
    }
    
    @Test
    public void testApply_All() {
        System.out.println("testApply_All");
        Map<Integer,Double> vals = INST.apply(TEST2);
        assertEquals(6, vals.size());
        for (int i = 0; i < 6; i++)
            assertEquals(INST.apply(TEST2, i+1), vals.get(i+1));
    }

}
