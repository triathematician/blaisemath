/*
 * DerivedMetrics.java
 * Created May 18, 2010
 */

package org.blaise.graph.metrics;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Set;
import org.blaise.graph.Graph;

/**
 * Provides a <code>GraphSubsetMetric</code> computed by adding together the
 * return values of a <code>GraphNodeMetric</code> for each node in the subset.
 *
 * @author Elisha Peterson
 */
public class AdditiveSubsetMetric<N extends Number> implements GraphSubsetMetric<N> {

    GraphNodeMetric<N> baseMetric;

    /**
     * Constructs with provided base metric.
     * @param baseMetric the metric to use for computations on individual nodes.
     */
    public AdditiveSubsetMetric(GraphNodeMetric<N> baseMetric) { 
        this.baseMetric = baseMetric;
    }

    public <V> N getValue(Graph<V> graph, Set<V> vertices) {
        Double result = 0.0;
        Number val = null;
        for (V v : vertices) {
            val = (Number) baseMetric.value(graph, v);
            result += val.doubleValue();
        }
        if (val instanceof Integer) {
            return (N) (Integer) result.intValue();
        }
        else if (val instanceof Double) {
            return (N) result;
        }
        else if (val instanceof Float) {
            return (N) (Float) result.floatValue();
        }
        return null;
    }
}
