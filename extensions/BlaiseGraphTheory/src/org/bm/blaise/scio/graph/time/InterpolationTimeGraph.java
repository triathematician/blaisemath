/*
 * InterpolationTimeGraph.java
 * Created on Feb 11, 2011
 */

package org.bm.blaise.scio.graph.time;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.WeightedGraph;

/**
 * Provides a continuous interpolation view of a sliced time graph. Slices are
 * weighted graphs representing averages of adjacent graphs in the underlying slice.
 *
 * @param <V> the type of the nodes
 * @author petereb1
 */
public class InterpolationTimeGraph<V> implements TimeGraph<V> {

    /** Base graph */
    private final TimeGraph<V> base;
    /** New times */
    private final List<Double> times = new ArrayList<Double>();

    /** Construct interpolating dynamic graph with specified base */
    public InterpolationTimeGraph(TimeGraph<V> base, int steps) {
        this.base = base;
        double lastT = 0;
        for (Double d : base.getTimes()) {
            if (times.isEmpty())
                times.add(d);
            else {
                double ts = (d-lastT)/(steps+1.0);
                for (double t = lastT+ts; t < d-.5*ts; t += ts)
                    times.add(t);
            }
            lastT = d;
        }
        times.add(base.getMaximumTime());
    }

    public Collection<V> getAllNodes() { return base.getAllNodes(); }
    public boolean isDirected() { return base.isDirected(); }
    public List<double[]> getNodeIntervals(V v) { return base.getNodeIntervals(v); }
    public List<double[]> getEdgeIntervals(V v1, V v2) { return base.getEdgeIntervals(v1, v2); }
    public double getMinimumTime() { return base.getMinimumTime(); }
    public double getMaximumTime() { return base.getMaximumTime(); }
    public List<Double> getTimes() { return times; }

    public Graph<V> slice(double time, boolean exact) {
        Double dLast = -Double.MAX_VALUE;
        if (time <= getMinimumTime())
            return new InterpolatorGraph(getMinimumTime(), getMinimumTime(), getMinimumTime());
        else if (time >= getMaximumTime())
            return new InterpolatorGraph(getMaximumTime(), getMaximumTime(), getMaximumTime());
        for (Double d : getTimes()) {
            if (time == d)
                return base.slice(time, true);
            else if (time < d)
                return new InterpolatorGraph(dLast, time, d);
            dLast = d;
        }
        throw new IllegalStateException();
    }

    public class InterpolatorGraph implements WeightedGraph<V,Double> {
        private final Graph<V> slice0, slice1;
        private final double wt0, wt1;
        private final List<V> nodes = new ArrayList<V>();

        public InterpolatorGraph(double t0, double t, double t1) {
            slice0 = base.slice(t0, true);
            slice1 = base.slice(t1, true);
            wt0 = (t-t0)/(t1-t0);
            wt1 = 1-(t-t0)/(t1-t0);
            HashSet<V> n = new HashSet<V>();
            n.addAll(slice0.nodes());
            n.addAll(slice1.nodes());
            nodes.addAll(n);
        }

        public Double getWeight(V x, V y) {
            if (!adjacent(x,y))
                return 0.0;
            double w0 = 1, w1 = 1;
            if (slice0 instanceof WeightedGraph) {
                Object w0o = ((WeightedGraph)slice0).getWeight(x, y);
                if (w0o instanceof Number)
                    w0 = ((Number)w0o).doubleValue();
            }
            if (slice1 instanceof WeightedGraph) {
                Object w1o = ((WeightedGraph)slice1).getWeight(x, y);
                if (w1o instanceof Number)
                    w1 = ((Number)w1o).doubleValue();
            }
            return w0*wt0 + w1*wt1;
        }

        public void setWeight(V x, V y, Double value) { throw new UnsupportedOperationException("Not supported yet."); }
        public int order() { return nodes.size(); }
        public List<V> nodes() { return nodes; }
        public boolean contains(V x) { return nodes.contains(x); }
        public boolean isDirected() { return slice0.isDirected() || slice1.isDirected(); }
        public boolean adjacent(V x, V y) { return slice0.adjacent(x, y) || slice1.adjacent(x, y); }
        public int degree(V x) { return neighbors(x).size(); }
        public Set<V> neighbors(V x) {
            Set<V> nbrs = slice0.neighbors(x);
            nbrs.addAll(slice1.neighbors(x));
            return nbrs;
        }
        public int edgeNumber() {
            int count = 0;
            for (V v : nodes())
                count += degree(v);
            if (!isDirected())
                count /= 2;
            return count;
        }
    }
}
