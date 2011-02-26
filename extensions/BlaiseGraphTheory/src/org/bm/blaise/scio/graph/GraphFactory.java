/*
 * GraphFactory.java
 * Created Jul 14, 2010
 */

package org.bm.blaise.scio.graph;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Set;
import org.bm.blaise.scio.graph.time.IntervalTimeGraph;
import org.bm.blaise.scio.graph.time.TimeGraph;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Provides methods for creating and returning graphs of certain types.
 *
 * @author Elisha Peterson
 */
public class GraphFactory {

    /**
     * Creates an instance of either a matrix-based graph or a sparse-based graph, depending
     * upon the number of edges and vertices
     * @param directed whether graph is directed
     * @param vertices collection of vertices
     * @param edges collection of edges as vertex-pairings
     * @return graph with specified edges
     */
    public static <V> Graph<V> getGraph(boolean directed, Collection<V> vertices, Collection<V[]> edges) {
        int n = vertices.size();
        int nEdges = edges.size();
        return (n <= 50 || (n<=100 && nEdges > 1000) )
                ? MatrixGraph.getInstance(directed, vertices, edges)
                : SparseGraph.getInstance(directed, vertices, edges);
    }
       
    /**
     * Creates an instance of either a longitudinal graph, implemented using either
     * <code>IntervalTimeGraph</code> or <code>ListLongitudinalGraph</code>,
     * depending upon the time parameters.
     * 
     * @param directed whether graph is directed
     * @param vertices map associating vertices to time intervals
     * @param edges map associating edges to time intervals
     * @return graph with specified edges
     */
    public static <V> TimeGraph<V> getLongitudinalGraph(boolean directed, int timeSteps,
            Map<V, List<double[]>> vertices, Map<V, Map<V, List<double[]>>> edges) {

        // create master table of (sorted) key times
        TreeSet<Double> allTimes = new TreeSet<Double>();
        for (List<double[]> tt : vertices.values())
            for (double[] interval : tt)
                if (interval != null) for (double d : interval) allTimes.add(d);
        for (Map<V, List<double[]>> map : edges.values())
            for (List<double[]> tt : map.values())
                for (double[] interval : tt)
                    if (interval != null) for (double d : interval) allTimes.add(d);
                    
        System.out.println(" .. getLongitudinalGraph: " + allTimes.size() + " key times: " + allTimes);

        return IntervalTimeGraph.getInstance2(directed, allTimes.size(), vertices, edges);
    }


// <editor-fold defaultstate="collapsed" desc="Basic Graph Types">

    /**
     * Returns graph with n vertices and no edges
     * @param n number of vertices
     * @param directed whether graph is directed
     * @return empty graph with specified number of vertices
     */
    public static Graph<Integer> getEmptyGraph(final int n, boolean directed) {
        if (n < 0) throw new IllegalArgumentException("Numbers must be nonnegative! n="+n);
        return getGraph(directed, intList(n), (Collection) Collections.emptyList());
    }

    /**
     * Returns graph with all possible edges.
     * @param n number of vertices
     * @param directed whether graph is directed
     * @return complete graph
     */
    public static Graph<Integer> getCompleteGraph(final int n, boolean directed) {
        if (n < 0) throw new IllegalArgumentException("Numbers must be nonnegative! n="+n);
        List<Integer[]> edges = new ArrayList<Integer[]>();
        for(int i = 0; i < n; i++)
            for (int j = i+1; j < n; j++) {
                edges.add(new Integer[]{i, j});
                if (directed)
                    edges.add(new Integer[]{j, i});
            }
        return getGraph(directed, intList(n), edges);
    }

    /**
     * Returns graph with all edges connected in a loop.
     * @param n number of vertices
     * @param directed whether graph is directed
     * @return cycle graph
     */
    public static Graph<Integer> getCycleGraph(final int n, boolean directed) {
        if (n < 0) throw new IllegalArgumentException("Numbers must be nonnegative! n="+n);
        return getGraph(directed, intList(n),
            new AbstractList<Integer[]>() {
                @Override public Integer[] get(int index) { return new Integer[] { index, (index+1)%n }; }
                @Override public int size() { return n; }
            }
        );
    }

    /**
     * Returns graph with all vertices connected to a central hub.
     * @param n number of vertices
     * @return star graph (undirected)
     */
    public static Graph<Integer> getStarGraph(final int n, boolean directed) {
        if (n < 0) throw new IllegalArgumentException("Numbers must be nonnegative! n="+n);
        return getGraph(directed, intList(n),
            new AbstractList<Integer[]>() {
                @Override public Integer[] get(int index) { return new Integer[] { 0, index+1 }; }
                @Override public int size() { return n==0 ? 0 : n-1; }
            }
        );
    }

    /**
     * Returns graph with all vertices connected to a central hub, and all other vertices
     * connected in a cyclic fashion.
     * @param n number of vertices
     * @param directed whether result is directed
     * @return star graph (undirected)
     */
    public static Graph<Integer> getWheelGraph(final int n, boolean directed) {
        if (n < 0) throw new IllegalArgumentException("Numbers must be nonnegative! n="+n);
        ArrayList<Integer[]> edges = new ArrayList<Integer[]>();
        for (int i = 1; i < n; i++)
            edges.add(new Integer[]{0,i});
        for (int i = 1; i < n-1; i++) {
            edges.add(new Integer[]{i,i+1});
            if (directed)
                edges.add(new Integer[]{i+1,i});
        }
        edges.add(new Integer[]{n-1,1});
        if (directed) 
            edges.add(new Integer[]{1,n-1});
        return getGraph(directed, intList(n), edges);
    }
// </editor-fold>


    /**
     * @param order number of vertices
     * @param x0 min x for bounding box
     * @param x1 max x for bounding box
     * @param y0 min y for bounding box
     * @param y1 max y for bounding box
     * @param dist max distance at which vertices are "linked"
     * @return random graph
     */
    public static ValuedGraph<Integer,Point2D.Double> getRandomProximityGraph(int order, double x0, double x1, double y0, double y1, double dist) {
        List<Point2D.Double> pts = new ArrayList<Point2D.Double>();
        for (int i = 0; i < order; i++)
            pts.add(new Point2D.Double(x0+(x1-x0)*Math.random(), y0+(y1-y0)*Math.random()));

        List<Integer> vertices = new ArrayList<Integer>();
        List<Integer[]> edges = new ArrayList<Integer[]>();
        for (int i0 = 0; i0 < pts.size(); i0++) {
            vertices.add(i0);
            for (int i1 = 0; i1 < pts.size(); i1++) {
                if (i0 == i1 || pts.get(i0).distance(pts.get(i1)) > dist)
                    continue;
                edges.add(new Integer[]{i0, i1});
            }
        }
        Graph<Integer> g = getGraph(false, vertices, edges);
        ValuedGraph<Integer,Point2D.Double> result = new ValuedGraphWrapper<Integer,Point2D.Double>(g);
        for (int i = 0; i < order; i++)
            result.setValue(i, pts.get(i));
        return result;
    }


// <editor-fold defaultstate="collapsed" desc="Prime Number Graphs">
    /**
     * Returns graph whose nodes are integers >= 2 and which are connected based on the number of prime factors.
     * @param n maximum integer (there will be n-1 vertices)
     * @param min minimum # of common factors to be considered "adjacent"
     */
    public static PrimeNumberGraph getPrimeNumberGraph(int n, int min) {
        if (n <= 1) throw new IllegalArgumentException("n must be > 1: " + n);
        return new PrimeNumberGraph(n, min);
    }


    public static class PrimeNumberGraph implements WeightedGraph<Integer,Integer>, ValuedGraph<Integer,Integer> {
        int n, min;
        public PrimeNumberGraph(int n, int min) { this.n = n; this.min = min;}
        public Color getColor(Integer x) {
            List<Integer> pp = factors(x);
            int min = pp.isEmpty() ? -1 : pp.get(0);
            switch (min) {
                case -1: return Color.magenta;
                case 2: return Color.black;
                case 3: return Color.blue;
                case 5: return Color.green;
                case 7: return Color.orange;
                case 11: return Color.red;
                case 13: return Color.black;
                default: return Color.gray;
            }
        }
        public Integer getValue(Integer x) { return factors(x).size(); }
        public void setValue(Integer x, Integer value) { }
        public Integer getWeight(Integer x, Integer y) {
            List<Integer> c = common(x,y);
            return c.size() < min ? 0 : c.get(min-1);
        }
        public void setWeight(Integer x, Integer y, Integer value) { }
        public int order() { return n-1; }
        public List<Integer> nodes() {
            return new AbstractList<Integer>() {
                @Override public Integer get(int index) { return index+2; }
                @Override public int size() { return n-1; }
            };
        }
        public boolean contains(Integer x) { return x != null && x > 1 && x <= n; }
        public boolean isDirected() { return false; }
        public boolean adjacent(Integer x, Integer y) { return nCommon(x,y) >= min; }
        public int degree(Integer x) { return neighbors(x).size(); }
        public Set<Integer> neighbors(Integer x) {
            Set<Integer> result = new TreeSet<Integer>();
            for (Integer i : nodes())
                if (adjacent(i,x)) result.add(i);
            return result;
        }
        public int edgeNumber() {
            int sum = 0;
            for (Integer i : nodes())
                sum += degree(i);
            return sum/2;
        }
        /** Cached list of no common factors */
        static TreeMap<Integer,TreeSet<Integer>> noCommon = new TreeMap<Integer,TreeSet<Integer>>();
        /** Cached list of common factors */
        static TreeMap<Integer,TreeMap<Integer,Integer>> inCommon = new TreeMap<Integer,TreeMap<Integer,Integer>>();

        /** Comptues number of common factors */
        public static int nCommon(int n1, int n2) {
            int min = Math.min(n1,n2), max = Math.max(n1,n2);
            if (noCommon.containsKey(min) && noCommon.get(min).contains(max))
                return 0;
            else if (inCommon.containsKey(min) && inCommon.get(min).containsKey(max))
                return inCommon.get(min).get(max);
            else
                return common(n1, n2).size();
        }
        /** Computes common factors */
        public static List<Integer> common(int n1, int n2) {
            int min = Math.min(n1,n2), max = Math.max(n1,n2);
            if (noCommon.containsKey(min) && noCommon.get(min).contains(max))
                return Collections.emptyList();
            List<Integer> l1 = new ArrayList<Integer>(factors(n1));
            l1.retainAll(factors(n2));
            if (l1.isEmpty()) {
                if (!noCommon.containsKey(min))
                    noCommon.put(min, new TreeSet<Integer>());
                noCommon.get(min).add(max);
            } else {
                if (!inCommon.containsKey(min))
                    inCommon.put(min, new TreeMap<Integer,Integer>());
                inCommon.get(min).put(max, l1.size());
            }
            return l1;
        }
        /** Cached list of factors */
        static TreeMap<Integer,List<Integer>> factors = new TreeMap<Integer,List<Integer>>();
        /** Computes prime factors of an integer */
        public static List<Integer> factors(Integer n) {
            if (factors.containsKey(n))
                return factors.get(n);
            List<Integer> result = new ArrayList<Integer>();
            for (int i = 2; i <= n/2; i++)
                if (isPrime(i) && n % i == 0) result.add(i);
            factors.put(n, result);
            return result;
        }
        /** Cached list of primes */
        static TreeSet<Integer> primes = new TreeSet<Integer>();
        /** Prime number test */
        public static boolean isPrime(Integer n) {
            if (primes.contains(n)) return true;
            for (int i = 2; i <= n/2; i++)
                if (n % i == 0) return false;
            primes.add(n);
            return true;
        }
    }
// </editor-fold>



    //
    // UTILITY METHODS
    //

    /** Returns abstract list of integers 0,...,n-1 */
    static List<Integer> intList(final int n) {
        return new AbstractList<Integer>() {
            @Override public Integer get(int index) { return index; }
            @Override public int size() { return n; }
        };
    }

} // CLASS GraphFactory
