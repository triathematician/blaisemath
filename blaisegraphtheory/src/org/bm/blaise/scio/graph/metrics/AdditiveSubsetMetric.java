/*
 * DerivedMetrics.java
 * Created May 18, 2010
 */

package org.bm.blaise.scio.graph.metrics;

import java.util.Collection;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.metrics.NodeMetric;

/**
 * Provides a <code>SubsetMetric</code> computed by adding together the
 * return values of a <code>NodeMetric</code> for each node in the subset.
 *
 * @author Elisha Peterson
 */
public class AdditiveSubsetMetric<N extends Number> implements SubsetMetric<N> {

    NodeMetric<N> baseMetric;

    /**
     * Constructs with provided base metric.
     * @param baseMetric the metric to use for computations on individual nodes.
     */
    public AdditiveSubsetMetric(NodeMetric<N> baseMetric) { 
        this.baseMetric = baseMetric;
    }

    public <V> N getValue(Graph<V> graph, Collection<V> vertices) {
        Double result = 0.0;
        Number val = null;
        for (V v : vertices) {
            val = (Number) baseMetric.value(graph, v);
            result += val.doubleValue();
        }
        if (val instanceof Integer)
            return (N) (Integer) result.intValue();
        else if (val instanceof Double)
            return (N) result;
        else if (val instanceof Float)
            return (N) (Float) result.floatValue();
        return null;
    }
}
