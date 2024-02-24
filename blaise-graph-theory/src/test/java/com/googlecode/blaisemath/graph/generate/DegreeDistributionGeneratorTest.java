package com.googlecode.blaisemath.graph.generate;

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
import com.googlecode.blaisemath.graph.GraphUtils;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@SuppressWarnings("UnstableApiUsage")
public class DegreeDistributionGeneratorTest {
    
    @Test
    public void testGetDirectedInstance() {
        int sum = 1+7+3+2+1;
        int[] expected = new int[]{1,7,3,2,1};
        Graph<Integer> result = DegreeDistributionGenerator.generateDirected(expected);
        System.out.println("graph: " + GraphUtils.printGraph(result));
        assertEquals(sum, result.nodes().size());
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
        try {
            DegreeDistributionGenerator.generateUndirected(new int[]{1, 7, 3, 2, 1});
            fail("Shouldn't be able to use odd degree sum.");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        int[] expected = new int[]{1, 7, 3, 3, 1};
        int sum = 1+7+3+3+1;
        Graph<Integer> result = DegreeDistributionGenerator.generateUndirected(expected);
        assertEquals(sum, result.nodes().size());
        int[] left = new int[5];
        System.arraycopy(expected, 0, left, 0, 5);
        for (Integer i : result.nodes()) {
            left[result.degree(i)]--;
        }
        System.out.println("  Unable to test this directly, but this array should be about 0: " + java.util.Arrays.toString(left));
    }

}
