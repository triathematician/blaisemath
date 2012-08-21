/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.blaise.graph.metrics;

import java.util.Arrays;
import org.blaise.graph.Graph;
import org.blaise.graph.SparseGraph;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author elisha
 */
public class BetweenCentralityTest {

    static Graph<Integer> TEST2;

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("-- BetweenCentralityTest --");
        TEST2 = new SparseGraph(false, Arrays.asList(1,2,3,4,5,6,7),
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

}