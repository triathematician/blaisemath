package com.googlecode.blaisemath.graph.query;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2018 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import com.google.common.graph.Graph;
import com.googlecode.blaisemath.util.Instrument;

import java.util.Set;
import java.util.function.Function;

/**
 * Graph query that operates with a "seedRule graph" and a "growRule rule".
 *
 * @author Elisha Peterson
 */
public class GraphSeedGrowthQuery implements Function<Graph, Set> {

    private static final String EXEC_ALGO_NAME = GraphSeedGrowthQuery.class.getName()+"#execute";

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

    @Override
    public Set apply(Graph graph) {
        int id = Instrument.start(EXEC_ALGO_NAME);
        Set seedNodes = seedRule.apply(graph);
        Instrument.middle(id, "seeded");
        Set result = growRule.grow(graph, seedNodes);
        Instrument.end(id);
        return result;
    }

}
