/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.scio.graph.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.io.AbstractGraphIO.GraphType;
import org.bm.blaise.scio.graph.io.UCINetGraphIO.DataFormat;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author elisha
 */
public class UCINetGraphIOTest {

    static Graph<Integer>
            SAMPLE_FRAT, SAMPLE_PADGETT, SAMPLE_PADGETT2, SAMPLE_TARO;
    static UCINetGraphIO io = UCINetGraphIO.getInstance();

    public static Graph<Integer> sampleFrat() {
        if (SAMPLE_FRAT == null) SAMPLE_FRAT = (Graph<Integer>) io.importGraph(new HashMap<Integer,double[]>(),
                UCINetGraphIO.class.getResource("data/newfrat.dat"), GraphType.REGULAR);
        return SAMPLE_FRAT; }
    public static Graph<Integer> samplePadgett1() {
        if (SAMPLE_PADGETT == null) SAMPLE_PADGETT = (Graph<Integer>) io.importGraph(new HashMap<Integer,double[]>(),
                UCINetGraphIO.class.getResource("data/padgett.dat"), GraphType.REGULAR);
        return SAMPLE_PADGETT; }
    public static Graph<Integer> samplePadgett2() {
        if (SAMPLE_PADGETT2 == null) SAMPLE_PADGETT2 = (Graph<Integer>) io.importGraph(new HashMap<Integer,double[]>(),
                UCINetGraphIO.class.getResource("data/padgw.dat"), GraphType.REGULAR);
        return SAMPLE_PADGETT2; }
    public static Graph<Integer> sampleTaro() {
        if (SAMPLE_TARO == null) SAMPLE_TARO = (Graph<Integer>) io.importGraph(new HashMap<Integer,double[]>(),
                UCINetGraphIO.class.getResource("data/taro.dat"), GraphType.REGULAR);
        return SAMPLE_TARO; }

    @Test
    public void testGetInstance() {
        System.out.println("getInstance -- no test");
    }

    @Test
    public void testImportGraph() {
        System.out.println("importGraph");
        Graph<Integer> graph;

        graph = sampleTaro();
          assertEquals(22, graph.order()); assertEquals(39, graph.edgeNumber()); assertFalse(graph.isDirected());
        graph = sampleFrat();
          assertEquals(17, graph.order());
        graph = samplePadgett1();
          assertEquals(16, graph.order());
        graph = samplePadgett2();
          assertEquals(16, graph.order());
    }

    @Test
    public void testParseDataFormat() {
        System.out.println("parseDataFormat");
        assertEquals(DataFormat.FULLMATRIX, UCINetGraphIO.parseDataFormat("format = fullmatrix"));
        assertEquals(DataFormat.FULLMATRIX, UCINetGraphIO.parseDataFormat("format = fullmatrix diagonal present  "));
        assertEquals(DataFormat.EDGELIST1, UCINetGraphIO.parseDataFormat("format=edgelist1"));
        assertEquals(DataFormat.NODELIST1, UCINetGraphIO.parseDataFormat("format =nodelist1"));
        assertEquals(DataFormat.UNKNOWN, UCINetGraphIO.parseDataFormat("format =nodeList1"));
        assertEquals(DataFormat.UNKNOWN, UCINetGraphIO.parseDataFormat("format = pp"));
        assertEquals(DataFormat.UNKNOWN, UCINetGraphIO.parseDataFormat("format fullmatrix"));
    }

    @Test
    public void testCheckVertex() {
        System.out.println("checkVertex -- tested within other methods");
        Map<Integer,String> v = new HashMap<Integer,String>();
        v.put(1, null);
        v.put(3, "a");
        v.put(10, "abcdefg");

        assertEquals(1, UCINetGraphIO.checkVertex("  1", v, false));
        assertEquals(3, UCINetGraphIO.checkVertex("  a ", v, true));
        assertEquals(2, UCINetGraphIO.checkVertex("  another ", v, true));
        assertEquals(4, UCINetGraphIO.checkVertex("one_more", v, true));
        try { assertEquals(-1, UCINetGraphIO.checkVertex("  a", v, false)); fail("Should throw number exception"); } catch (NumberFormatException ex) {}
        try { assertEquals(-1, UCINetGraphIO.checkVertex("2.1", v, false)); fail("Should throw number exception"); } catch (NumberFormatException ex) {}
    }

    @Test
    public void testImportLine_edge() {
        System.out.println("importLine_edge");

        Map<Integer,String> v = new HashMap<Integer,String>();
        List<Integer[]> e = new ArrayList<Integer[]>();
        List<Double> w = new ArrayList<Double>();

        // regular
        UCINetGraphIO.importLine_edge(0, "1 2", v, e, w, false);
        UCINetGraphIO.importLine_edge(1, "3 4 .1", v, e, w, false);
        UCINetGraphIO.importLine_edge(2, "5 6 1 a b c", v, e, w, false);
        UCINetGraphIO.importLine_edge(3, "7 8 abc", v, e, w, false);
          for (int i = 1; i <= 8; i++) assertTrue(v.containsKey(i));
          assertEquals(1, (int)e.get(0)[0]); assertEquals(2, (int)e.get(0)[1]); assertEquals(1.0, w.get(0), 1e-10);
          assertEquals(3, (int)e.get(1)[0]); assertEquals(4, (int)e.get(1)[1]); assertEquals(0.1, w.get(1), 1e-10);
          assertEquals(5, (int)e.get(2)[0]); assertEquals(6, (int)e.get(2)[1]); assertEquals(1.0, w.get(2), 1e-10);
          assertEquals(7, (int)e.get(3)[0]); assertEquals(8, (int)e.get(3)[1]); assertEquals(1.0, w.get(3), 1e-10);
          
        // embedded labels
        v.clear(); e.clear(); w.clear();
        UCINetGraphIO.importLine_edge(0, "a b", v, e, w, true);
        UCINetGraphIO.importLine_edge(1, "c d .1", v, e, w, true);
        UCINetGraphIO.importLine_edge(2, "e a 1 a b c", v, e, w, true);
        UCINetGraphIO.importLine_edge(3, "g d abc", v, e, w, true);
          for (int i = 1; i <= 6; i++) assertTrue(v.containsKey(i));
          assertEquals("c", v.get(3)); assertEquals("g", v.get(6));
          assertEquals(1, (int)e.get(0)[0]); assertEquals(2, (int)e.get(0)[1]); assertEquals(1.0, w.get(0), 1e-10);
          assertEquals(3, (int)e.get(1)[0]); assertEquals(4, (int)e.get(1)[1]); assertEquals(0.1, w.get(1), 1e-10);
          assertEquals(5, (int)e.get(2)[0]); assertEquals(1, (int)e.get(2)[1]); assertEquals(1.0, w.get(2), 1e-10);
          assertEquals(6, (int)e.get(3)[0]); assertEquals(4, (int)e.get(3)[1]); assertEquals(1.0, w.get(3), 1e-10);
    }

    @Test
    public void testImportLine_edgelist() {
        System.out.println("importLine_edgelist");

        Map<Integer,String> v = new HashMap<Integer,String>();
        List<Integer[]> e = new ArrayList<Integer[]>();
        List<Double> w = new ArrayList<Double>();

        // regular
        UCINetGraphIO.importLine_edgelist(0, "1", v, e, w, false);
        UCINetGraphIO.importLine_edgelist(1, "2 3", v, e, w, false);
        UCINetGraphIO.importLine_edgelist(2, "3 1 4 5", v, e, w, false);
        UCINetGraphIO.importLine_edgelist(4, "1 2.1 3", v, e, w, false); // this line should fail, printing an error
          for (int i = 1; i <= 5; i++) assertTrue(v.containsKey(i));
          assertEquals(4, e.size()); assertEquals(4, w.size());
          assertEquals(2, (int)e.get(0)[0]); assertEquals(3, (int)e.get(0)[1]);
          assertEquals(3, (int)e.get(1)[0]); assertEquals(1, (int)e.get(1)[1]);
          assertEquals(3, (int)e.get(2)[0]); assertEquals(4, (int)e.get(2)[1]);
          assertEquals(3, (int)e.get(3)[0]); assertEquals(5, (int)e.get(3)[1]);

        // embedded labels
        v.clear(); e.clear(); w.clear();
        UCINetGraphIO.importLine_edgelist(0, "a", v, e, w, true);
        UCINetGraphIO.importLine_edgelist(1, "0a", v, e, w, true); // should fail to parse
        UCINetGraphIO.importLine_edgelist(2, "b c", v, e, w, true);
        UCINetGraphIO.importLine_edgelist(3, "c a d e", v, e, w, true);
        UCINetGraphIO.importLine_edgelist(4, "a b 2.1 c", v, e, w, true); // first entry should work; remainder should be ignored
          for (int i = 1; i <= 5; i++) assertTrue(v.containsKey(i)); assertFalse(v.containsKey(6));
          assertEquals(5, e.size()); assertEquals(5, w.size());
          assertEquals(2, (int)e.get(0)[0]); assertEquals(3, (int)e.get(0)[1]);
          assertEquals(3, (int)e.get(1)[0]); assertEquals(1, (int)e.get(1)[1]);
          assertEquals(3, (int)e.get(2)[0]); assertEquals(4, (int)e.get(2)[1]);
          assertEquals(3, (int)e.get(3)[0]); assertEquals(5, (int)e.get(3)[1]);
          assertEquals(1, (int)e.get(4)[0]); assertEquals(2, (int)e.get(4)[1]);
    }

    @Test
    public void testImportLine_matrix() {
        System.out.println("importLine_matrix");

        // regular
        Map<Integer,String> v = new HashMap<Integer,String>();
        List<Integer[]> e = new ArrayList<Integer[]>();
        List<Double> w = new ArrayList<Double>();

        UCINetGraphIO.importLine_matrix(1, "1 0 1 1", 1, v, e, w, false);
        UCINetGraphIO.importLine_matrix(2, "0 0 0 .5", 2, v, e, w, false);
        UCINetGraphIO.importLine_matrix(3, "1 1 0 -1", 3, v, e, w, false);
        UCINetGraphIO.importLine_matrix(4, "0 0 3 0", 4, v, e, w, false);
          for (int i = 1; i <= 4; i++) assertTrue(v.containsKey(i));
          assertEquals(1, (int)e.get(0)[0]); assertEquals(1, (int)e.get(0)[1]); assertEquals(1, w.get(0), 1e-10);
          assertEquals(1, (int)e.get(1)[0]); assertEquals(3, (int)e.get(1)[1]); assertEquals(1, w.get(1), 1e-10);
          assertEquals(2, (int)e.get(3)[0]); assertEquals(4, (int)e.get(3)[1]); assertEquals(.5, w.get(3), 1e-10);
          assertEquals(4, (int)e.get(7)[0]); assertEquals(3, (int)e.get(7)[1]); assertEquals(3, w.get(7), 1e-10);

        // embedded labels
        v.clear(); e.clear(); w.clear();
        UCINetGraphIO.importLine_matrix(1, "a _dd c09 b", 1, v, e, w, true);
        UCINetGraphIO.importLine_matrix(2, "a 1 0 1 1", 2, v, e, w, true);
        UCINetGraphIO.importLine_matrix(3, "b 0 0 0 .5", 3, v, e, w, true);
        UCINetGraphIO.importLine_matrix(4, "c09 1 1 0 -1", 4, v, e, w, true);
        UCINetGraphIO.importLine_matrix(5, "_dd 0 0 3 0", 5, v, e, w, true);
          for (int i = 1; i <= 4; i++) assertTrue(v.containsKey(i)); assertFalse(v.containsKey(5));
          assertEquals("a", v.get(1)); assertEquals("_dd", v.get(2)); assertEquals("c09", v.get(3));
          assertEquals(1, (int)e.get(0)[0]); assertEquals(1, (int)e.get(0)[1]); assertEquals(1, w.get(0), 1e-10);
          assertEquals(1, (int)e.get(1)[0]); assertEquals(3, (int)e.get(1)[1]); assertEquals(1, w.get(1), 1e-10);
          assertEquals(4, (int)e.get(3)[0]); assertEquals(4, (int)e.get(3)[1]); assertEquals(.5, w.get(3), 1e-10);
          assertEquals(2, (int)e.get(7)[0]); assertEquals(3, (int)e.get(7)[1]); assertEquals(3, w.get(7), 1e-10);
    }

}