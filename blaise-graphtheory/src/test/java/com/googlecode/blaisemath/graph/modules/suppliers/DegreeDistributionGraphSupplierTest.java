/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.blaisemath.graph.modules.suppliers;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2016 Elisha Peterson
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

import com.googlecode.blaisemath.graph.modules.suppliers.DegreeDistributionGraphSupplier;
import java.util.Arrays;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.GraphUtils;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author elisha
 */
public class DegreeDistributionGraphSupplierTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("-- DegreeCountBuilderTest --");
    }

    @Test
    public void testGetDirectedInstance() {
        System.out.println("getDirectedInstance");
        int sum = 1+7+3+2+1;
        int[] expected = new int[]{1,7,3,2,1};
        Graph<Integer> result = DegreeDistributionGraphSupplier.getDirectedInstance(expected);
        System.out.println("graph: " + GraphUtils.printGraph(result));
        assertEquals(sum, result.nodeCount());
        int[] foundDegrees = new int[5];
        Arrays.fill(foundDegrees, 0);
        for (Integer i : result.nodes()) {
            foundDegrees[result.outDegree(i)]++;
        }
        for (int i = 0; i < foundDegrees.length; i++) {
            assertEquals(expected[i], foundDegrees[i]);
        }
    }

    @Test
    public void testGetUndirectedInstance() {
        System.out.println("getUndirectedInstance");
        try { DegreeDistributionGraphSupplier.getUndirectedInstance(new int[]{1,7,3,2,1}); fail("Shouldn't be able to use odd degree sum."); } catch (IllegalArgumentException ex) {}
        int[] expected = new int[]{1,7,3,3,1};
        int sum = 1+7+3+3+1;
        Graph<Integer> result = DegreeDistributionGraphSupplier.getUndirectedInstance(expected);
        assertEquals(sum, result.nodeCount());
        int[] left = new int[5];
        System.arraycopy(expected, 0, left, 0, 5);
        for (Integer i : result.nodes())
            left[result.degree(i)]--;
        System.out.println("  Unable to test this directly, but this array should be about 0: " + java.util.Arrays.toString(left));
    }

}
