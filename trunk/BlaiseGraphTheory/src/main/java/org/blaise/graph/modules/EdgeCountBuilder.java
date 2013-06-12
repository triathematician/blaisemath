/**
 * EdgeProbabilityBuilder.java
 * Created Aug 18, 2012
 */

package org.blaise.graph.modules;

import java.util.Comparator;
import java.util.TreeSet;
import org.blaise.graph.Graph;
import org.blaise.graph.GraphBuilder;
import org.blaise.graph.GraphBuilders;
import static org.blaise.graph.GraphBuilders.intList;
import org.blaise.graph.SparseGraph;

/**
 * Generate random graph with specified edge count.
 */
public class EdgeCountBuilder extends GraphBuilder.Support<Integer> {

    int edges;

    public EdgeCountBuilder() {
    }

    public EdgeCountBuilder(boolean directed, int nodes, int edges) {
        super(directed, nodes);
        if (nodes < 0 || edges < 0) {
            throw new IllegalArgumentException("Numbers must be positive! (n,e)=(" + nodes + "," + edges + ")");
        }
        if ((!directed && edges > nodes * (nodes - 1) / 2) || (directed && edges > nodes * nodes)) {
            throw new IllegalArgumentException("Too many edges! (n,e)=(" + nodes + "," + edges + ")");
        }
        this.edges = edges;
    }

    public int getEdges() {
        return edges;
    }

    public void setEdges(int edges) {
        this.edges = edges;
    }

    public Graph<Integer> createGraph() {
        TreeSet<Integer[]> edgeSet = new TreeSet<Integer[]>(directed ? PAIR_COMPARE : PAIR_COMPARE_UNDIRECTED);
        Integer[] potential;
        for (int i = 0; i < edges; i++) {
            do {
                potential = new Integer[]{(int) (nodes * Math.random()), (int) (nodes * Math.random())};
            } while ((!directed && potential[0] == potential[1]) || edgeSet.contains(potential));
            edgeSet.add(potential);
        }
        return new SparseGraph(directed, GraphBuilders.intList(nodes), edgeSet);
    }

    /**
     * Used to sort pairs of integers when order of the two matters.
     */
    static final Comparator<Integer[]> PAIR_COMPARE = new Comparator<Integer[]>() {
        public int compare(Integer[] o1, Integer[] o2) {
            if (o1.length != 2 || o2.length != 2) {
                throw new IllegalStateException("This object only compares integer pairs.");
            }
            return o1[0] == o2[0] ? o1[1] - o2[1] : o1[0] - o2[0];
        }
    };

    /**
     * Used to sort pairs of integers when order of the two does not matter.
     */
    static final Comparator<Integer[]> PAIR_COMPARE_UNDIRECTED = new Comparator<Integer[]>() {
        public int compare(Integer[] o1, Integer[] o2) {
            if (o1.length != 2 || o2.length != 2) {
                throw new IllegalStateException("This object only compares integer pairs.");
            }
            int min1 = Math.min(o1[0], o1[1]);
            int min2 = Math.min(o2[0], o2[1]);
            return min1 == min2 ? Math.max(o1[0], o1[1]) - Math.max(o2[0], o2[1]) : min1 - min2;
        }
    };
}
