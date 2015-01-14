/**
 * LongitudinalGraphSupplierSupport.java
 * Created Feb 13, 2014
 */
package com.googlecode.blaisemath.graph.longitudinal;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
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

import com.google.common.base.Supplier;

/**
 * Builds graphs with requisite number of nodes
 */
public abstract class LongitudinalGraphSupplierSupport<V> implements Supplier<LongitudinalGraph<V>> {
    
    protected boolean directed = false;
    protected int nodes = 1;

    public LongitudinalGraphSupplierSupport() {}

    public LongitudinalGraphSupplierSupport(boolean directed, int nodes) {
        if (nodes < 0) {
            throw new IllegalArgumentException("Graphs must have a non-negative number of nodes: " + nodes);
        }
        this.directed = directed;
        this.nodes = nodes;
    }

    public boolean isDirected() {
        return directed;
    }

    public void setDirected(boolean directed) {
        this.directed = directed;
    }

    public int getNodes() {
        return nodes;
    }

    public void setNodes(int nodes) {
        this.nodes = nodes;
    }

}
