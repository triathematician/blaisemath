/**
 * ExtendedGeneratorParameters.java
 * Created Mar 28, 2015
 */
package com.googlecode.blaisemath.graph.mod.generators;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2015 Elisha Peterson
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Adds edge count to {@link BasicGeneratorParameters}.
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

    @Override
    public void setDirected(boolean directed) {
        super.setDirected(directed);
        checkEdgeCount();
    }

    @Override
    public void setNodeCount(int nodes) {
        super.setNodeCount(nodes);
        checkEdgeCount();
    }

    public int getEdgeCount() {
        return edgeCount;
    }

    public void setEdgeCount(int edges) {
        checkArgument(edges >= 0);
        this.edgeCount = edges;
        checkEdgeCount();
    }
    
    //</editor-fold>
    
    private void checkEdgeCount() {
        if (!directed && edgeCount > (nodeCount * (nodeCount - 1)) / 2) {
            Logger.getLogger(EdgeCountGenerator.class.getName()).log(Level.WARNING, 
                    "Too many edges! (n,e)=({0},{1})", new Object[]{nodeCount, edgeCount});
            edgeCount = (nodeCount*(nodeCount-1))/2;
        }
        if (directed && edgeCount > nodeCount * nodeCount) {
            Logger.getLogger(EdgeCountGenerator.class.getName()).log(Level.WARNING, 
                    "Too many edges! (n,e)=({0},{1})", new Object[]{nodeCount, edgeCount});
            edgeCount = nodeCount*nodeCount;
        }
    }

}
