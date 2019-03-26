/**
 * ExtendedGeneratorParameters.java
 * Created Mar 28, 2015
 */
package com.googlecode.blaisemath.graph.mod.generators;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2019 Elisha Peterson
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
 * @author elisha
 */
public final class ExtendedGeneratorParameters extends DefaultGeneratorParameters {
    
    protected int edgeCount = 0;

    public ExtendedGeneratorParameters() {
    }

    public ExtendedGeneratorParameters(boolean directed, int nodes, int edges) {
        super(directed, nodes);
        setEdgeCount(edges);
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">

    public int getEdgeCount() {
        return edgeCount;
    }

    public void setEdgeCount(int edges) {
        checkArgument(edges >= 0);
        this.edgeCount = edges;
    }
    
    //</editor-fold>
    
    /**
     * Get the number of edges, limited to the maximum possible based on the current node count.
     * @return edge count
     */
    public int getCheckedEdgeCount() {
        if (!directed && edgeCount > (nodeCount * (nodeCount - 1)) / 2) {
            return (nodeCount*(nodeCount-1))/2;
        }
        if (directed && edgeCount > nodeCount * nodeCount) {
            return nodeCount*nodeCount;
        }
        return edgeCount;
    }

}
