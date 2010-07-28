/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.scio.graph.metrics;

import java.util.Arrays;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.GraphFactory;
import org.bm.blaise.scio.graph.io.PajekGraphIOTest;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author elisha
 */
public class EigenCentralityTest {

    static Graph<Integer> SAMPLE_PADGETT;
    static Graph<Integer> TEST2;

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("-- DecayCentralityTest --");
        SAMPLE_PADGETT = PajekGraphIOTest.sampleXPadgett();
        TEST2 = GraphFactory.getGraph(false, Arrays.asList(1,2,3,4,5,6),
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
    public void testGetValue() {
        System.out.println("getValue");
        EigenCentrality ec = EigenCentrality.getInstance();
        assertEquals(.475349771, ec.value(TEST2, 1), 1e-8);
        assertEquals(.564129165, ec.value(TEST2, 3), 1e-8);
        assertEquals(.296008301, ec.value(TEST2, 4), 1e-8);
    }

}