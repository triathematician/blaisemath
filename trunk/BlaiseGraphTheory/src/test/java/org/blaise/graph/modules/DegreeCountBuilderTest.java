/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.blaise.graph.modules;

import java.util.Arrays;
import org.blaise.graph.Graph;
import org.blaise.graph.GraphUtils;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author elisha
 */
public class DegreeCountBuilderTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("-- DegreeCountBuilderTest --");
    }

    @Test
    public void testGetDirectedInstance() {
        System.out.println("getDirectedInstance");
        int sum = 1+7+3+2+1;
        int[] expected = new int[]{1,7,3,2,1};
        Graph<Integer> result = DegreeCountBuilder.getDirectedInstance(expected);
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
        try { DegreeCountBuilder.getUndirectedInstance(new int[]{1,7,3,2,1}); fail("Shouldn't be able to use odd degree sum."); } catch (IllegalArgumentException ex) {}
        int[] expected = new int[]{1,7,3,3,1};
        int sum = 1+7+3+3+1;
        Graph<Integer> result = DegreeCountBuilder.getUndirectedInstance(expected);
        assertEquals(sum, result.nodeCount());
        int[] left = new int[5];
        System.arraycopy(expected, 0, left, 0, 5);
        for (Integer i : result.nodes())
            left[result.degree(i)]--;
        System.out.println("  Unable to test this directly, but this array should be about 0: " + java.util.Arrays.toString(left));
    }

}