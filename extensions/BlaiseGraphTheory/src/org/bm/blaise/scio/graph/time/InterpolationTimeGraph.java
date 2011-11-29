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
import org.bm.blaise.scio.graph.GraphSupport;
import org.bm.blaise.scio.graph.GraphUtils;
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
            if (!times.isEmpty()) {
                double ts = (d-lastT)/(steps+1.0);
                for (double t = lastT+ts; t < d-.5*ts; t += ts)
                    times.add(t);
            }
            times.add(d);
            lastT = d;
        }
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
        double t0 = 0, t1 = 0;
        if (time <= getMinimumTime())
            t0 = t1 = getMinimumTime();
        else if (time >= getMaximumTime())
            t0 = t1 = getMaximumTime();
        else
            for (Double d : base.getTimes()) {
                if (time == d) {
                    t0 = t1 = time;
                    break;
                } else if (time < d) {
                    t0 = dLast;
                    t1 = d;
                    break;
                }
                dLast = d;
            }
        return new InterpolatorGraph(base.slice(t0, true), t0, time, base.slice(t1, true), t1);
    }



    /** Interpolator graph interpolates between two adjacent graph slices. */
    public static class InterpolatorGraph<V> extends GraphSupport<V> implements WeightedGraph<V,Double> {

        private final Graph<V> slice0, slice1;
        private final double wt0, wt1;
        private final Collection<Set<V>> compts;

        public InterpolatorGraph(Graph<V> slice0, double t0, double t, Graph<V> slice1, double t1) {
            super(union(slice0.nodes(), slice1.nodes()), slice0.isDirected() || slice1.isDirected());
            this.slice0 = slice0;
            this.slice1 = slice1;
            if (t0 == t1) {
                wt0 = 1;
                wt1 = 0;
            } else {
                wt0 = 1-(t-t0)/(t1-t0);
                wt1 = (t-t0)/(t1-t0);
            }
            compts = GraphUtils.components(this, this.nodes);
        }

        public Collection<Set<V>> components() {
            return compts;
        }

        public Double getWeight(V x, V y) {
            if (!adjacent(x,y))
                return 0.0;
            double w0 = 1, w1 = 1;
            if (slice0 instanceof WeightedGraph) {
                Object w0o = ((WeightedGraph)slice0).getWeight(x, y);
                if (w0o instanceof Number)
                    w0 = ((Number)w0o).doubleValue();
            } else if (!slice0.adjacent(x,y))
                w0 = 0;
            if (slice1 instanceof WeightedGraph) {
                Object w1o = ((WeightedGraph)slice1).getWeight(x, y);
                if (w1o instanceof Number)
                    w1 = ((Number)w1o).doubleValue();
            } else if (!slice1.adjacent(x,y))
                w1 = 0;
            return w0*wt0 + w1*wt1;
        }

        public void setWeight(V x, V y, Double value) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean adjacent(V x, V y) {
            return slice0.adjacent(x, y) || slice1.adjacent(x, y);
        }

        public Set<V> neighbors(V x) {
            Set<V> nbrs = new HashSet<V>(slice0.neighbors(x));
            nbrs.addAll(slice1.neighbors(x));
            return nbrs;
        }

        public int edgeCount() {
            int count = 0;
            for (V v : nodes())
                count += degree(v);
            if (!isDirected())
                count /= 2;
            return count;
        }
    }




    private static <V> List<V> union(Collection<V> c1, Collection<V> c2) {
        Set<V> result = new HashSet<V>(c1);
        result.addAll(c2);
        return new ArrayList<V>(result);
    }
}
