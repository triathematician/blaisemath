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
import org.bm.blaise.scio.graph.io.PajekGraphIO.ImportMode;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author elisha
 */
public class PajekGraphIOTest {

    static Graph<Integer>
            SAMPLE_AIRPORT,
            SAMPLE_ERDOS991, SAMPLE_ERDOS992,
            SAMPLE_FRAT,
            SAMPLE_INTERNET,
            SAMPLE_PADGETT1, SAMPLE_PADGETT2,
            SAMPLE_S50_D01,
            SAMPLE_TINAMATR;
    static Graph<Integer>
            X_PADGETT,
            X_AIRPORTS;

    public static Graph<Integer> sampleAirport() {
        if (SAMPLE_AIRPORT == null) SAMPLE_AIRPORT = PajekGraphIO.getInstance().importGraph(new HashMap<Integer,double[]>(), PajekGraphIO.class.getResource("data/USAirport500.net"));
        return SAMPLE_AIRPORT; }
    public static Graph<Integer> sampleErdos991() {
        if (SAMPLE_ERDOS991 == null) SAMPLE_ERDOS991 = PajekGraphIO.getInstance().importGraph(new HashMap<Integer,double[]>(), PajekGraphIO.class.getResource("data/ERDOS991.net"));
        return SAMPLE_ERDOS991; }
    public static Graph<Integer> sampleErdos992() {
        if (SAMPLE_ERDOS992 == null) SAMPLE_ERDOS992 = PajekGraphIO.getInstance().importGraph(new HashMap<Integer,double[]>(), PajekGraphIO.class.getResource("data/ERDOS992.net"));
        return SAMPLE_ERDOS992; }
    public static Graph<Integer> sampleFrat() {
        if (SAMPLE_FRAT == null) SAMPLE_FRAT = PajekGraphIO.getInstance().importGraph(new HashMap<Integer,double[]>(), PajekGraphIO.class.getResource("data/newfrat.net"));
        return SAMPLE_FRAT; }
    public static Graph<Integer> sampleInternet() {
        if (SAMPLE_INTERNET == null) SAMPLE_INTERNET = PajekGraphIO.getInstance().importGraph(new HashMap<Integer,double[]>(), PajekGraphIO.class.getResource("data/InternetISP.net"));
        return SAMPLE_INTERNET; }
    public static Graph<Integer> samplePadgett1() {
        if (SAMPLE_PADGETT1 == null) SAMPLE_PADGETT1 = PajekGraphIO.getInstance().importGraph(new HashMap<Integer,double[]>(), PajekGraphIO.class.getResource("data/padgett.net"));
        return SAMPLE_PADGETT1; }
//    public static Graph<Integer> samplePadgett2() {
//        if (SAMPLE_PADGETT2 == null) SAMPLE_PADGETT2 = PajekGraphIO.importGraph(PajekGraphIO.class.getResource("data/padgett2.net"));
//        return SAMPLE_PADGETT2; }
    public static Graph<Integer> sampleS50D01() {
        if (SAMPLE_S50_D01 == null) SAMPLE_S50_D01 = PajekGraphIO.getInstance().importGraph(new HashMap<Integer,double[]>(), PajekGraphIO.class.getResource("data/s50_d01.net"));
        return SAMPLE_S50_D01; }
    public static Graph<Integer> sampleTinamatr() {
        if (SAMPLE_TINAMATR == null) SAMPLE_TINAMATR = PajekGraphIO.getInstance().importGraph(new HashMap<Integer,double[]>(), PajekGraphIO.class.getResource("data/tinamatr.net"));
        return SAMPLE_TINAMATR; }

    public static Graph<Integer> sampleXPadgett() {
        if (X_PADGETT == null) X_PADGETT = PajekGraphIO.getExtendedInstance().importGraph(new HashMap<Integer,double[]>(), PajekGraphIO.class.getResource("data/padgett.txt"));
        return X_PADGETT; }
    public static Graph<Integer> sampleXAirport() {
        if (X_AIRPORTS == null) X_AIRPORTS = PajekGraphIO.getExtendedInstance().importGraph(new HashMap<Integer,double[]>(), PajekGraphIO.class.getResource("data/USAirport500.txt"));
        return X_AIRPORTS; }

    @Test
    public void testImportGraph() {
        System.out.println("-- PajekGraphIOTest --");
        Graph<Integer> graph;

        graph = sampleAirport();
          assertEquals(500, graph.order()); assertEquals(2980, graph.edgeNumber()); assertFalse(graph.isDirected());
        graph = sampleErdos991();
          assertEquals(492, graph.order()); assertEquals(1417, graph.edgeNumber()); assertFalse(graph.isDirected());
        graph = sampleErdos992();
          assertEquals(6100, graph.order()); assertEquals(9939, graph.edgeNumber()); assertFalse(graph.isDirected());
        graph = sampleFrat();
          System.out.println("importGraph frat: MANUALLY CHECK FOR DESIRED OUTPUT: " + graph);
          assertEquals(17, graph.order()); assertEquals(26, graph.edgeNumber()); assertFalse(graph.isDirected());
        graph = sampleInternet();
          assertEquals(11174, graph.order()); assertEquals(23409, graph.edgeNumber()); assertFalse(graph.isDirected());
        graph = samplePadgett1();
          System.out.println("importGraph padgett: MANUALLY CHECK FOR DESIRED OUTPUT: " + graph);
          assertEquals(16, graph.order()); assertEquals(20, graph.edgeNumber()); assertFalse(graph.isDirected());
          // graph = samplePadgett2();
          //   assertEquals(17, graph.order()); assertEquals(25, graph.edgeNumber()); assertFalse(graph.isDirected());
          //   System.out.println("importGraph padgett2: MANUALLY CHECK FOR DESIRED OUTPUT: " + graph);
        graph = sampleS50D01();
          System.out.println("importGraph s50_d01: MANUALLY CHECK FOR DESIRED OUTPUT: " + graph);
          assertEquals(47, graph.order()); assertEquals(113, graph.edgeNumber()); assertTrue(graph.isDirected());
        graph = sampleTinamatr();
          System.out.println("importGraph tinamatr: MANUALLY CHECK FOR DESIRED OUTPUT: " + graph);
          assertEquals(11, graph.order()); assertEquals(41, graph.edgeNumber()); assertTrue(graph.isDirected());
    }

    @Test
    public void testImportGraph_extended() {
        System.out.println("-- PajekGraphIOTest (extended) --");
        Graph<Integer> graph;

        graph = sampleXAirport();
          assertEquals(500, graph.order()); assertEquals(2980, graph.edgeNumber()); assertFalse(graph.isDirected());
        graph = sampleXPadgett();
          System.out.println("importGraph padgett: MANUALLY CHECK FOR DESIRED OUTPUT: " + graph);
          assertEquals(16, graph.order()); assertEquals(20, graph.edgeNumber()); assertFalse(graph.isDirected());
    }

    @Test
    public void testParseInputMode() {
        System.out.println("parseInputMode");
        assertEquals(ImportMode.ARCS, PajekGraphIO.parseInputMode("*Arcs", false));
        assertEquals(ImportMode.EDGES, PajekGraphIO.parseInputMode("* EDGES blah blah blah", false));
        assertEquals(ImportMode.VERTICES, PajekGraphIO.parseInputMode("*vertices 5", false));
        assertEquals(ImportMode.UNKNOWN, PajekGraphIO.parseInputMode("*stuff", false));
        assertEquals(ImportMode.UNKNOWN, PajekGraphIO.parseInputMode("*DEscriPTION", false));
        assertEquals(ImportMode.DESCRIPTION, PajekGraphIO.parseInputMode("*DEscriPTION", true));
    }

    @Test
    public void testParseVerticesModeNumber() {
        System.out.println("parseVerticesModeNumber");
        assertEquals(-1, PajekGraphIO.parseVerticesModeNumber("*Vertices"));
        assertEquals(1, PajekGraphIO.parseVerticesModeNumber("*Vertices 1"));
        assertEquals(52, PajekGraphIO.parseVerticesModeNumber("*Vertices 52"));
        try {
            PajekGraphIO.parseVerticesModeNumber("* vertices blah blah 5");
            fail("Decode should fail here.");
        } catch (Exception ex) {}
    }

    @Test
    public void testImportLine_vertex() {
        System.out.println("importLine_vertex");

        Map<Integer,String> v = new HashMap<Integer,String>();
        Map<Integer,double[]> pp = new HashMap<Integer,double[]>();
        Map<Integer,List<double[]>> tt = new HashMap<Integer,List<double[]>>();

        // basic vertices
        PajekGraphIO.importLine_vertex(0,"1", v, pp, tt);
          assertNull(v.get(1));
        PajekGraphIO.importLine_vertex(1,"1 abc", v, pp, tt);
          assertEquals("abc", v.get(1));
          assertFalse(pp.containsKey(1));
        PajekGraphIO.importLine_vertex(2,"2 a b c", v, pp, tt);
          assertEquals("a", v.get(2));
          assertFalse(pp.containsKey(2));
        PajekGraphIO.importLine_vertex(3,"3 \"abc\"", v, pp, tt);
          assertEquals("abc", v.get(3));
        PajekGraphIO.importLine_vertex(4,"4 \"a b c\"", v, pp, tt);
          assertEquals("a b c", v.get(4));
        PajekGraphIO.importLine_vertex(5,"5 \"a b c", v, pp, tt);
          assertNull(v.get(5));
          assertFalse(pp.containsKey(5));

        // positions
        PajekGraphIO.importLine_vertex(6, "6 \"a b c\" 3.2 2.6", v, pp, tt);
          assertTrue(pp.containsKey(6));
          assertEquals(2, pp.get(6).length);
          assertEquals(3.2, pp.get(6)[0], 1e-10);
          assertEquals(2.6, pp.get(6)[1], 1e-10);
        PajekGraphIO.importLine_vertex(7, "7 \"abc\" 1 2 3", v, pp, tt);
          assertTrue(pp.containsKey(7));
          assertEquals(3, pp.get(7).length);
          assertEquals(1, pp.get(7)[0], 1e-10);
          assertEquals(2, pp.get(7)[1], 1e-10);
          assertEquals(3, pp.get(7)[2], 1e-10);
        PajekGraphIO.importLine_vertex(8, "8 abc 3", v, pp, tt);
          assertFalse(pp.containsKey(8));

        // times
        List<double[]> t;
        PajekGraphIO.importLine_vertex(9, "9 abc 3 4 [2]", v, pp, tt);
          assertTrue(tt.containsKey(9)); t = tt.get(9);
          assertEquals(1, t.size());
          assertEquals(2, t.get(0)[0], 1e-10); assertEquals(2, t.get(0)[1], 1e-10);
        PajekGraphIO.importLine_vertex(10, "10 \"a b c\" 3 4 5 [2-6]", v, pp, tt);
          assertTrue(tt.containsKey(10)); t = tt.get(10);
          assertEquals(1, t.size());
          assertEquals(2, t.get(0)[0], 1e-10); assertEquals(6, t.get(0)[1], 1e-10);
        PajekGraphIO.importLine_vertex(11, "11 abc [2-6,3]", v, pp, tt);
          assertTrue(tt.containsKey(11)); t = tt.get(11);
          assertEquals(2, t.size());
          assertEquals(2, t.get(0)[0], 1e-10); assertEquals(6, t.get(0)[1], 1e-10);
          assertEquals(3, t.get(1)[0], 1e-10); assertEquals(3, t.get(1)[1], 1e-10);
        PajekGraphIO.importLine_vertex(12, "12 \"abc\" [2-6,3-4,5-6]", v, pp, tt);
          assertTrue(tt.containsKey(12)); t = tt.get(12);
          assertEquals(3, t.size());
          assertEquals(2, t.get(0)[0], 1e-10); assertEquals(6, t.get(0)[1], 1e-10);
          assertEquals(3, t.get(1)[0], 1e-10); assertEquals(4, t.get(1)[1], 1e-10);
          assertEquals(5, t.get(2)[0], 1e-10); assertEquals(6, t.get(2)[1], 1e-10);
        PajekGraphIO.importLine_vertex(13, "13 abc [2-6,*-4,5-*]", v, pp, tt);
          assertTrue(tt.containsKey(13)); t = tt.get(13);
          assertEquals(3, t.size());
          assertEquals(2, t.get(0)[0], 1e-10); assertEquals(6, t.get(0)[1], 1e-10);
          assertEquals(Double.NEGATIVE_INFINITY, t.get(1)[0], 1e-10); assertEquals(4, t.get(1)[1], 1e-10);
          assertEquals(5, t.get(2)[0], 1e-10); assertEquals(Double.POSITIVE_INFINITY, t.get(2)[1], 1e-10);

        // failures
        try {
            PajekGraphIO.importLine_vertex(99,"1ab c", v, pp, tt);
            fail("Line '1ab c' should fire an exception");
        } catch (Exception ex) {}
    }

    @Test
    public void testImportLine_edge() {
        System.out.println("importLine_edge");

        Map<Integer,String> v = new HashMap<Integer,String>();
        List<Integer[]> e = new ArrayList<Integer[]>();
        List<Double> w = new ArrayList<Double>();
        List<List<double[]>> tt = new ArrayList<List<double[]>>();

        // basic edges
        PajekGraphIO.importLine_edge(0, "1 2", v, e, w, tt);
        assertEquals(1, (int)e.get(0)[0]);
        assertEquals(2, (int)e.get(0)[1]);
        assertEquals(1.0, w.get(0), 1e-10);

        // weights
        PajekGraphIO.importLine_edge(1, "3 4 .1", v, e, w, tt);
          assertEquals(3, (int)e.get(1)[0]);
          assertEquals(4, (int)e.get(1)[1]);
          assertEquals(0.1, w.get(1), 1e-10);
        PajekGraphIO.importLine_edge(2, "5 6 1 a b c", v, e, w, tt);
          assertEquals(5, (int)e.get(2)[0]);
          assertEquals(6, (int)e.get(2)[1]);
          assertEquals(1.0, w.get(2), 1e-10);
        PajekGraphIO.importLine_edge(3, "7 8 abc", v, e, w, tt);
          assertEquals(7, (int)e.get(3)[0]);
          assertEquals(8, (int)e.get(3)[1]);
          assertEquals(1.0, w.get(3), 1e-10);
        for (int i = 1; i <= 8; i++) assertTrue(v.containsKey(i));

        // times
        List<double[]> t;
        PajekGraphIO.importLine_edge(4, "4 3 4 [2]", v, e, w, tt);
          t = tt.get(0);
          assertEquals(1, t.size());
          assertEquals(2, t.get(0)[0], 1e-10); assertEquals(2, t.get(0)[1], 1e-10);
        PajekGraphIO.importLine_edge(5, "5 2 abc [2-6,*-4,5-*]", v, e, w, tt);
          t = tt.get(1);
          assertEquals(3, t.size());
          assertEquals(2, t.get(0)[0], 1e-10); assertEquals(6, t.get(0)[1], 1e-10);
          assertEquals(Double.NEGATIVE_INFINITY, t.get(1)[0], 1e-10); assertEquals(4, t.get(1)[1], 1e-10);
          assertEquals(5, t.get(2)[0], 1e-10); assertEquals(Double.POSITIVE_INFINITY, t.get(2)[1], 1e-10);

        // failures
        int num = e.size();
        PajekGraphIO.importLine_edge(4, "1 2b 3", v, e, w, tt);
        assertEquals(num, e.size());
    }

    @Test
    public void testImportLine_edgelist() {
        System.out.println("importLine_edgelist");

        Map<Integer,String> v = new HashMap<Integer,String>();
        List<Integer[]> e = new ArrayList<Integer[]>();
        List<Double> w = new ArrayList<Double>();

        PajekGraphIO.importLine_edgelist(0, "1", v, e, w);
        assertEquals(0, e.size());
        assertEquals(0, w.size());
        PajekGraphIO.importLine_edgelist(1, "2 3", v, e, w);
        assertEquals(2, (int)e.get(0)[0]);
        assertEquals(3, (int)e.get(0)[1]);
        PajekGraphIO.importLine_edgelist(2, "3 1 4 5", v, e, w);
        assertEquals(3, (int)e.get(1)[0]);
        assertEquals(1, (int)e.get(1)[1]);
        assertEquals(3, (int)e.get(2)[0]);
        assertEquals(4, (int)e.get(2)[1]);
        assertEquals(3, (int)e.get(3)[0]);
        assertEquals(5, (int)e.get(3)[1]);
        for (int i = 1; i <= 5; i++) assertTrue(v.containsKey(i));
        try {
            PajekGraphIO.importLine_edgelist(4, "1 2.1 3", v, e, w);
            fail("Numeric failure expected");
        } catch (Exception ex) {}
    }

    @Test
    public void testImportLine_matrix() {
        System.out.println("importLine_matrix");

        Map<Integer,String> v = new HashMap<Integer,String>();
        List<Integer[]> e = new ArrayList<Integer[]>();
        List<Double> w = new ArrayList<Double>();
        
        PajekGraphIO.importLine_matrix(1, "1 0 1 1", 1, v, e, w);
        PajekGraphIO.importLine_matrix(2, "0 0 0 .5", 2, v, e, w);
        PajekGraphIO.importLine_matrix(3, "1 1 0 -1", 3, v, e, w);
        PajekGraphIO.importLine_matrix(4, "0 0 3 0", 4, v, e, w);
        for (int i = 1; i <= 4; i++) assertTrue(v.containsKey(i));
        assertEquals(1, (int)e.get(0)[0]);
        assertEquals(1, (int)e.get(0)[1]);
        assertEquals(1, w.get(0), 1e-10);
        assertEquals(1, (int)e.get(1)[0]);
        assertEquals(3, (int)e.get(1)[1]);
        assertEquals(1, w.get(1), 1e-10);
        assertEquals(2, (int)e.get(3)[0]);
        assertEquals(4, (int)e.get(3)[1]);
        assertEquals(.5, w.get(3), 1e-10);
        assertEquals(4, (int)e.get(7)[0]);
        assertEquals(3, (int)e.get(7)[1]);
        assertEquals(3, w.get(7), 1e-10);
    }
}