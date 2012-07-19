/*
 * GraphSeedGrowthQuery.java
 * Created on Jun 8, 2012
 */
package org.bm.blaise.scio.graph.query;

import org.bm.blaise.scio.graph.GAInstrument;
import java.util.Set;
import org.bm.blaise.scio.graph.Graph;

/**
 * Graph query that operates with a "seedRule graph" and a "growRule rule".
 *
 * @author Elisha Peterson
 */
public class GraphSeedGrowthQuery implements GraphQuery {

    private final GraphSeedRule seedRule;
    private final GraphGrowthRule growRule;

    /**
     * Initialize query.
     * @param seedRule how to choose "seed graph"
     * @param growRule how to choose larger graph around the seed graph
     */
    public GraphSeedGrowthQuery(GraphSeedRule seedRule, GraphGrowthRule growRule) {
        this.seedRule = seedRule;
        this.growRule = growRule;
    }

    private static final String EXEC_ALGO_NAME = GraphSeedGrowthQuery.class.getName()+"#execute";

    public Set execute(Graph graph, SeedCallback callback) {
        int id = GAInstrument.start(EXEC_ALGO_NAME);
        Set seedNodes = seedRule.execute(graph);
        GAInstrument.middle(id, "seeded");
        if (callback != null)
            callback.seedSubset(seedNodes);
        Set result = growRule.grow(graph, seedNodes);
        GAInstrument.end(id);
        return result;
    }

    public Set execute(Graph graph) {
        return execute(graph, null);
    }



    /** Provides intermediate results */
    public static interface SeedCallback {
        /** Called with intermediate results when the graph seed is obtained */
        public void seedSubset(Set seedNodes);
    }

}
