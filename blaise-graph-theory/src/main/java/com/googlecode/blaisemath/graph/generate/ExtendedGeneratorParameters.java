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

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Graph parameters with directed flag, node count, and edge count.
 * 
 * @author Elisha Peterson
 */
public final class ExtendedGeneratorParameters extends DefaultGeneratorParameters {
    
    protected int edgeCount = 0;

    public ExtendedGeneratorParameters() {
    }

    public ExtendedGeneratorParameters(boolean directed, int nodes, int edges) {
        super(directed, nodes);
        setEdgeCount(edges);
    }
    
    //region PROPERTIES

    public int getEdgeCount() {
        return edgeCount;
    }

    public void setEdgeCount(int edges) {
        checkArgument(edges >= 0);
        this.edgeCount = edges;
    }
    
    //endregion
    
    /**
     * Get the number of edges, limited to the maximum possible based on the current node count.
     * @return edge count
     */
    public int edgeCountBounded() {
        int max = directed ? nodeCount * (nodeCount - 1) / 2 : nodeCount * nodeCount;
        return Math.min(edgeCount, max);
    }

}
