package com.googlecode.blaisemath.graph.generate;

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
 */

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Encapsulate number of nodes and directed flag.
 * 
 * @author Elisha Peterson
 */
public class DefaultGeneratorParameters {
    
    protected boolean directed = false;
    protected int nodeCount = 1;

    public DefaultGeneratorParameters() {
    }

    public DefaultGeneratorParameters(boolean directed, int nodes) {
        setDirected(directed);
        setNodeCount(nodes);
    }
    
    //region PROPERTIES

    public final boolean isDirected() {
        return directed;
    }

    public final void setDirected(boolean directed) {
        this.directed = directed;
    }

    public final int getNodeCount() {
        return nodeCount;
    }

    public final void setNodeCount(int nodes) {
        checkArgument(nodes >= 0);
        this.nodeCount = nodes;
    }
    
    //endregion

}
