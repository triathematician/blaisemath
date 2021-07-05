package com.googlecode.blaisemath.graph.generate

import com.google.common.annotations.Beta
import com.google.common.graph.Graph

/*
* #%L
* BlaiseGraphTheory
* --
* Copyright (C) 2009 - 2021 Elisha Peterson
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
*/ /**
 * Graph query that operates with a "seedRule graph" and a "growRule rule".
 *
 * @author Elisha Peterson
 */
@Beta
class GraphSeedGrowthQuery
/**
 * Initialize query.
 * @param seedRule how to choose "seed graph"
 * @param growRule how to choose larger graph around the seed graph
 */(private val seedRule: GraphSeedRule?, private val growRule: GraphGrowthRule?) : GraphSeedRule {
    override fun getName(): String? {
        return seedRule.toString() + " + " + growRule
    }

    override fun <N> apply(graph: Graph<N?>?): MutableSet<N?>? {
        return growRule.grow(graph, seedRule.apply(graph))
    }
}