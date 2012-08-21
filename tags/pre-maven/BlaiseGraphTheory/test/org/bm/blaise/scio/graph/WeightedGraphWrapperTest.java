/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.scio.graph;

import java.util.Arrays;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author elisha
 */
public class WeightedGraphWrapperTest {

    public WeightedGraphWrapperTest() {
    }

    static Integer[] VV;
    static Integer[][] EE;
    static MatrixGraph<Integer> UNDIRECTED_INSTANCE, DIRECTED_INSTANCE;
    static WeightedGraphWrapper<Integer, String> UNDIR_WEIGHTED, DIR_WEIGHTED;

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("-- WeightedGraphWrapperTest --");
        VV = new Integer[] { 1, 2, 3, 4, 5, 6, 10, 11, 15, 20, 21 };
        EE = new Integer[][] {
            {1,2}, {2,1}, {2,3}, {2,4}, {2,5}, {1,6}, {6,6}, {6,10}, {10,11}, {11,1}, {15, 15}, {20, 21}
        };
        UNDIRECTED_INSTANCE = MatrixGraph.getInstance(false, Arrays.asList(VV), Arrays.asList(EE));
        DIRECTED_INSTANCE = MatrixGraph.getInstance(true, Arrays.asList(VV), Arrays.asList(EE));

        UNDIR_WEIGHTED = new WeightedGraphWrapper<Integer, String>(UNDIRECTED_INSTANCE);        
        DIR_WEIGHTED = new WeightedGraphWrapper<Integer, String>(DIRECTED_INSTANCE);
        
        assertEquals(UNDIRECTED_INSTANCE.adjacent(1, 2), UNDIR_WEIGHTED.adjacent(1, 2));
        assertEquals(UNDIRECTED_INSTANCE.contains(2), UNDIR_WEIGHTED.contains(2));
        assertEquals(UNDIRECTED_INSTANCE.degree(2), UNDIR_WEIGHTED.degree(2));
        assertEquals(UNDIRECTED_INSTANCE.isDirected(), UNDIR_WEIGHTED.isDirected());
        assertEquals(UNDIRECTED_INSTANCE.neighbors(2), UNDIR_WEIGHTED.neighbors(2));
        assertEquals(UNDIRECTED_INSTANCE.nodes(), UNDIR_WEIGHTED.nodes());
        assertEquals(UNDIRECTED_INSTANCE.order(), UNDIR_WEIGHTED.order());
        assertEquals(UNDIRECTED_INSTANCE.edgeCount(), UNDIR_WEIGHTED.edgeCount());
        
        assertEquals(DIRECTED_INSTANCE.adjacent(1, 2), DIR_WEIGHTED.adjacent(1, 2));
        assertEquals(DIRECTED_INSTANCE.contains(2), DIR_WEIGHTED.contains(2));
        assertEquals(DIRECTED_INSTANCE.degree(2), DIR_WEIGHTED.degree(2));
        assertEquals(DIRECTED_INSTANCE.isDirected(), DIR_WEIGHTED.isDirected());
        assertEquals(DIRECTED_INSTANCE.neighbors(2), DIR_WEIGHTED.neighbors(2));
        assertEquals(DIRECTED_INSTANCE.nodes(), DIR_WEIGHTED.nodes());
        assertEquals(DIRECTED_INSTANCE.order(), DIR_WEIGHTED.order());
        assertEquals(DIRECTED_INSTANCE.edgeCount(), DIR_WEIGHTED.edgeCount());

    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testToString() {
        System.out.println("toString");
        for (Integer i1 : UNDIR_WEIGHTED.nodes())
            for (Integer i2 : UNDIR_WEIGHTED.nodes())
                if (i2 >= i1)
                    UNDIR_WEIGHTED.setWeight(i1, i2, i1 + "-" + i2);
        for (Integer i1 : DIR_WEIGHTED.nodes())
            for (Integer i2 : DIR_WEIGHTED.nodes())
                DIR_WEIGHTED.setWeight(i1, i2, i1 + "->" + i2);

        assertEquals("NODES: [1, 2, 3, 4, 5, 6, 10, 11, 15, 20, 21]  EDGES: {1={2=1-2, 6=1-6, 11=1-11}, 2={1=1-2, 3=2-3, 4=2-4, 5=2-5}, 3={2=2-3}, 4={2=2-4}, 5={2=2-5}, 6={1=1-6, 6=6-6, 10=6-10}, 10={6=6-10, 11=10-11}, 11={1=1-11, 10=10-11}, 15={15=15-15}, 20={21=20-21}, 21={20=20-21}}", UNDIR_WEIGHTED.toString());
        assertEquals("NODES: [1, 2, 3, 4, 5, 6, 10, 11, 15, 20, 21]  EDGES: {1={2=1->2, 6=1->6}, 2={1=2->1, 3=2->3, 4=2->4, 5=2->5}, 3={}, 4={}, 5={}, 6={6=6->6, 10=6->10}, 10={11=10->11}, 11={1=11->1}, 15={15=15->15}, 20={21=20->21}, 21={}}", DIR_WEIGHTED.toString());
    }

    @Test
    public void testGetWeight_setWeight() {
        System.out.println("getWeight/setWeight");

        UNDIR_WEIGHTED = new WeightedGraphWrapper<Integer, String>(UNDIRECTED_INSTANCE);
        DIR_WEIGHTED = new WeightedGraphWrapper<Integer, String>(DIRECTED_INSTANCE);
        UNDIR_WEIGHTED.setWeight(1, 2, "1->2");
        DIR_WEIGHTED.setWeight(1, 2, "1->2");
        assertEquals("1->2", UNDIR_WEIGHTED.getWeight(1, 2));
        assertEquals("1->2", DIR_WEIGHTED.getWeight(1, 2));
        assertEquals("1->2", UNDIR_WEIGHTED.getWeight(2, 1));
        assertEquals(null, DIR_WEIGHTED.getWeight(2, 1));

        UNDIR_WEIGHTED.setWeight(3, 4, "???");
        DIR_WEIGHTED.setWeight(3, 4, "???");
        assertEquals(null, UNDIR_WEIGHTED.getWeight(3, 4));
        assertEquals(null, DIR_WEIGHTED.getWeight(3, 4));
        
        UNDIR_WEIGHTED.setWeight(6, 6, "cycle");
        DIR_WEIGHTED.setWeight(6, 6, "cycle");
        assertEquals("cycle", UNDIR_WEIGHTED.getWeight(6, 6));
        assertEquals("cycle", DIR_WEIGHTED.getWeight(6, 6));
        
        UNDIR_WEIGHTED.setWeight(2, 5, "2->5");
        DIR_WEIGHTED.setWeight(2, 5, "2->5");
        assertEquals("2->5", UNDIR_WEIGHTED.getWeight(2, 5));
        assertEquals("2->5", DIR_WEIGHTED.getWeight(2, 5));
        assertEquals("2->5", UNDIR_WEIGHTED.getWeight(5, 2));
        assertEquals(null, DIR_WEIGHTED.getWeight(5, 2));

        DIR_WEIGHTED.setWeight(1, 2, "1->2");
        DIR_WEIGHTED.setWeight(2, 1, "2->1");
        assertEquals("1->2", DIR_WEIGHTED.getWeight(1, 2));
        assertEquals("2->1", DIR_WEIGHTED.getWeight(2, 1));

    }

}