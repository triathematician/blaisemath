package com.googlecode.blaisemath.graph.generate;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2025 Elisha Peterson
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

import com.google.common.annotations.Beta;
import com.google.common.graph.Graph;

import java.util.Set;

/**
 * Graph query that operates with a "seedRule graph" and a "growRule rule".
 *
 * @author Elisha Peterson
 */
@SuppressWarnings("UnstableApiUsage")
@Beta
public class GraphSeedGrowthQuery implements GraphSeedRule {

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
    public String getName() {
        return seedRule + " + " + growRule;
    }

    @Override
    public <N> Set<N> apply(Graph<N> graph) {
        return growRule.grow(graph, seedRule.apply(graph));
    }

}
