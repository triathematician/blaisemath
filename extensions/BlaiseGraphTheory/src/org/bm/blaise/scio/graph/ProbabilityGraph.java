/**
 * ProbabilityGraph.java
 * Created Feb 8, 2011
 */

package org.bm.blaise.scio.graph;

import java.util.Map;
import java.util.TreeMap;

/**
 * Provides methods for generating graphs with edges annotated by probabilities.
 * @author elisha
 */
public class ProbabilityGraph {

    public static WeightedGraph<Integer,Double> getInstance(int nv, int ne) {
        return new WeightedGraphWrapper<Integer,Double>(RandomGraph.getInstance(nv, ne, false)) {
            Map<Integer,Map<Integer,Double>> weights = new TreeMap<Integer,Map<Integer,Double>>();
            @Override
            public Double getWeight(Integer x, Integer y) {
                if (!adjacent(x,y)) return 0.0;
                if (weights.containsKey(x) && weights.get(x).containsKey(y))
                    return weights.get(x).get(y);
                double prob = Math.random();
                if (!weights.containsKey(x))
                    weights.put(x, new TreeMap<Integer,Double>());
                weights.get(x).put(y, prob);
                return prob;
            }
            
        };
    }

}
