/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.scio.graph;

import java.util.Arrays;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author elisha
 */
public class DegreeRandomGraphTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("-- ContractedDegreeRandomGraphTestGraphTest --");
    }

    @Test
    public void testGetInstance() {
        System.out.println("getInstance: tested by getDirectedInstance & getUndirectedInstance");
    }

    @Test
    public void testGetDirectedInstance() {
        System.out.println("getDirectedInstance");
        int[] expected = new int[]{1,7,3,2,1};
        int sum = 1+7+3+2+1;
        Graph<Integer> result = DegreeRandomGraph.getDirectedInstance(expected);
        assertEquals(sum, result.order());
        int[] foundDegrees = new int[5];
        Arrays.fill(foundDegrees, 0);
        for (Integer i : result.nodes())
            foundDegrees[result.degree(i)]++;
        for (int i = 0; i < foundDegrees.length; i++)
            assertEquals(expected[i], foundDegrees[i]);
    }

    @Test
    public void testGetUndirectedInstance() {
        System.out.println("getUndirectedInstance");
        try { DegreeRandomGraph.getUndirectedInstance(new int[]{1,7,3,2,1}); fail("Shouldn't be able to use odd degree sum."); } catch (IllegalArgumentException ex) {}
        int[] expected = new int[]{1,7,3,3,1};
        int sum = 1+7+3+3+1;
        Graph<Integer> result = DegreeRandomGraph.getUndirectedInstance(expected);
        assertEquals(sum, result.order());
        int[] left = new int[5];
        System.arraycopy(expected, 0, left, 0, 5);
        for (Integer i : result.nodes())
            left[result.degree(i)]--;
        System.out.println("  Unable to test this directly, but this array should be about 0: " + java.util.Arrays.toString(left));
    }

}