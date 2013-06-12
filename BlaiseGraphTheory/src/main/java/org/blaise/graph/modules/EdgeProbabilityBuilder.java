/**
 * EdgeProbabilityBuilder.java
 * Created Aug 18, 2012
 */

package org.blaise.graph.modules;

import java.util.ArrayList;
import java.util.List;
import org.blaise.graph.Graph;
import org.blaise.graph.GraphBuilder;
import org.blaise.graph.GraphBuilders;
import org.blaise.graph.SparseGraph;

/**
 * Generate random graph with specified edge probability.
 */
public class EdgeProbabilityBuilder extends GraphBuilder.Support<Integer> {

    float probability;

    public EdgeProbabilityBuilder() {}

    public EdgeProbabilityBuilder(boolean directed, int nodes, float probability) {
        super(directed, nodes);
        checkProbability(probability);
        this.probability = probability;
    }

    public float getProbability() {
        return probability;
    }

    public void setProbability(float probability) {
        this.probability = probability;
    }

    public Graph<Integer> createGraph() {
        // integer pointers must be unique
        List<Integer> nn = GraphBuilders.intList(nodes);
        // create edges
        ArrayList<Integer[]> edges = new ArrayList<Integer[]>();
        for (int i = 0; i < nodes; i++) {
            for (int j = directed ? 0 : i + 1; j < nodes; j++) {
                if (Math.random() < probability) {
                    edges.add(new Integer[]{nn.get(i), nn.get(j)});
                }
            }
        }
        return new SparseGraph(directed, nn, edges);
    }

    /**
     * Ensures probability is valid.
     */
    static void checkProbability(float p) {
        if (p < 0 || p > 1) {
            throw new IllegalArgumentException("Probalities must be between 0 and 1: (" + p + " was used.");
        }
    }

}
