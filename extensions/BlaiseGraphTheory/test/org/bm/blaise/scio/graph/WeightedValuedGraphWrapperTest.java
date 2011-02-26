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
public class WeightedValuedGraphWrapperTest {

    static Integer[] VV;
    static Integer[][] EE;
    static MatrixGraph<Integer> UNDIRECTED_INSTANCE, DIRECTED_INSTANCE;
    static WeightedValuedGraphWrapper<Integer, String, String> UNDIR_VALUE, DIR_VALUE;

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("-- WeightedNodeValueGraphWrapperTest --");
        VV = new Integer[] { 1, 2, 3, 4, 5, 6, 10, 11, 15, 20, 21 };
        EE = new Integer[][] {
            {1,2}, {2,1}, {2,3}, {2,4}, {2,5}, {1,6}, {6,6}, {6,10}, {10,11}, {11,1}, {15, 15}, {20, 21}
        };
        UNDIRECTED_INSTANCE = MatrixGraph.getInstance(false, Arrays.asList(VV), Arrays.asList(EE));
        DIRECTED_INSTANCE = MatrixGraph.getInstance(true, Arrays.asList(VV), Arrays.asList(EE));

        UNDIR_VALUE = new WeightedValuedGraphWrapper<Integer, String, String>(UNDIRECTED_INSTANCE);
        DIR_VALUE = new WeightedValuedGraphWrapper<Integer, String, String>(DIRECTED_INSTANCE);

        assertEquals(UNDIRECTED_INSTANCE.adjacent(1, 2), UNDIR_VALUE.adjacent(1, 2));
        assertEquals(UNDIRECTED_INSTANCE.contains(2), UNDIR_VALUE.contains(2));
        assertEquals(UNDIRECTED_INSTANCE.degree(2), UNDIR_VALUE.degree(2));
        assertEquals(UNDIRECTED_INSTANCE.isDirected(), UNDIR_VALUE.isDirected());
        assertEquals(UNDIRECTED_INSTANCE.neighbors(2), UNDIR_VALUE.neighbors(2));
        assertEquals(UNDIRECTED_INSTANCE.nodes(), UNDIR_VALUE.nodes());
        assertEquals(UNDIRECTED_INSTANCE.order(), UNDIR_VALUE.order());
        assertEquals(UNDIRECTED_INSTANCE.edgeNumber(), UNDIR_VALUE.edgeNumber());

        assertEquals(DIRECTED_INSTANCE.adjacent(1, 2), DIR_VALUE.adjacent(1, 2));
        assertEquals(DIRECTED_INSTANCE.contains(2), DIR_VALUE.contains(2));
        assertEquals(DIRECTED_INSTANCE.degree(2), DIR_VALUE.degree(2));
        assertEquals(DIRECTED_INSTANCE.isDirected(), DIR_VALUE.isDirected());
        assertEquals(DIRECTED_INSTANCE.neighbors(2), DIR_VALUE.neighbors(2));
        assertEquals(DIRECTED_INSTANCE.nodes(), DIR_VALUE.nodes());
        assertEquals(DIRECTED_INSTANCE.order(), DIR_VALUE.order());
        assertEquals(DIRECTED_INSTANCE.edgeNumber(), DIR_VALUE.edgeNumber());
    }

    @Test
    public void testToString() {
        System.out.println("toString");
        for (Integer i1 : UNDIR_VALUE.nodes())
                UNDIR_VALUE.setValue(i1, "@" + i1);
        for (Integer i1 : DIR_VALUE.nodes())
                DIR_VALUE.setValue(i1, "@" + i1);

        for (Integer i1 : UNDIR_VALUE.nodes())
            for (Integer i2 : UNDIR_VALUE.nodes())
                if (i2 >= i1)
                    UNDIR_VALUE.setWeight(i1, i2, i1 + "-" + i2);
        for (Integer i1 : DIR_VALUE.nodes())
            for (Integer i2 : DIR_VALUE.nodes())
                DIR_VALUE.setWeight(i1, i2, i1 + "->" + i2);

        assertEquals("NODES: {1=@1, 2=@2, 3=@3, 4=@4, 5=@5, 6=@6, 10=@10, 11=@11, 15=@15, 20=@20, 21=@21}  EDGES: {1={2=1-2, 6=1-6, 11=1-11}, 2={1=1-2, 3=2-3, 4=2-4, 5=2-5}, 3={2=2-3}, 4={2=2-4}, 5={2=2-5}, 6={1=1-6, 6=6-6, 10=6-10}, 10={6=6-10, 11=10-11}, 11={1=1-11, 10=10-11}, 15={15=15-15}, 20={21=20-21}, 21={20=20-21}}", UNDIR_VALUE.toString());
        assertEquals("NODES: {1=@1, 2=@2, 3=@3, 4=@4, 5=@5, 6=@6, 10=@10, 11=@11, 15=@15, 20=@20, 21=@21}  EDGES: {1={2=1->2, 6=1->6}, 2={1=2->1, 3=2->3, 4=2->4, 5=2->5}, 3={}, 4={}, 5={}, 6={6=6->6, 10=6->10}, 10={11=10->11}, 11={1=11->1}, 15={15=15->15}, 20={21=20->21}, 21={}}", DIR_VALUE.toString());
    }

    @Test
    public void testGetValue_setValue() {
        System.out.println("getValue/setValue");

        UNDIR_VALUE = new WeightedValuedGraphWrapper<Integer, String, String>(UNDIRECTED_INSTANCE);
        DIR_VALUE = new WeightedValuedGraphWrapper<Integer, String, String>(DIRECTED_INSTANCE);

        UNDIR_VALUE.setValue(1, "test1");
        DIR_VALUE.setValue(1, "test1");
        assertEquals("test1", UNDIR_VALUE.getValue(1));
        assertEquals("test1", DIR_VALUE.getValue(1));
        assertEquals(null, UNDIR_VALUE.getValue(2));
        assertEquals(null, DIR_VALUE.getValue(2));

        UNDIR_VALUE.setValue(999, "xxx");
        DIR_VALUE.setValue(999, "xxx");
        assertEquals(null, UNDIR_VALUE.getValue(999));
        assertEquals(null, DIR_VALUE.getValue(999));

        UNDIR_VALUE.setWeight(1, 2, "1->2");
        DIR_VALUE.setWeight(1, 2, "1->2");
        assertEquals("1->2", UNDIR_VALUE.getWeight(1, 2));
        assertEquals("1->2", DIR_VALUE.getWeight(1, 2));
        assertEquals("1->2", UNDIR_VALUE.getWeight(2, 1));
        assertEquals(null, DIR_VALUE.getWeight(2, 1));

        UNDIR_VALUE.setWeight(3, 4, "???");
        DIR_VALUE.setWeight(3, 4, "???");
        assertEquals(null, UNDIR_VALUE.getWeight(3, 4));
        assertEquals(null, DIR_VALUE.getWeight(3, 4));

        UNDIR_VALUE.setWeight(6, 6, "cycle");
        DIR_VALUE.setWeight(6, 6, "cycle");
        assertEquals("cycle", UNDIR_VALUE.getWeight(6, 6));
        assertEquals("cycle", DIR_VALUE.getWeight(6, 6));

        UNDIR_VALUE.setWeight(2, 5, "2->5");
        DIR_VALUE.setWeight(2, 5, "2->5");
        assertEquals("2->5", UNDIR_VALUE.getWeight(2, 5));
        assertEquals("2->5", DIR_VALUE.getWeight(2, 5));
        assertEquals("2->5", UNDIR_VALUE.getWeight(5, 2));
        assertEquals(null, DIR_VALUE.getWeight(5, 2));

        DIR_VALUE.setWeight(1, 2, "1->2");
        DIR_VALUE.setWeight(2, 1, "2->1");
        assertEquals("1->2", DIR_VALUE.getWeight(1, 2));
        assertEquals("2->1", DIR_VALUE.getWeight(2, 1));
    }

}