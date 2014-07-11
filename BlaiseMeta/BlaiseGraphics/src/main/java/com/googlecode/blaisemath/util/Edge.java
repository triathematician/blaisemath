/**
 * Edge.java
 * Created Aug 5, 2012
 */

package com.googlecode.blaisemath.util;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
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

/**
 * Simple data structure representing an "edge" between two non-null vertices.
 * @param <V> type of node
 * 
 * @author elisha
 */
public class Edge<V> {
    
    private final V v1;
    private final V v2;

    /**
     * Initialize edge
     * @param v1 first vertex
     * @param v2 second vertex
     */
    public Edge(V v1, V v2) {
        if (v1 == null || v2 == null) {
            throw new IllegalArgumentException();
        }
        this.v1 = v1;
        this.v2 = v2;
    }

    @Override
    public String toString() {
        return String.format("%s:%s",v1,v2);
    }
    
    /**
     * Edge's first node
     * @return 
     */
    public V getNode1() {
        return v1;
    }
    
    /**
     * Edge's second node
     * @return 
     */
    public V getNode2() {
        return v2;
    }
    
    /**
     * Checks if either vertex equals provided vertex
     * @param v test vertex
     * @return true if v1==v or v2==v
     */
    public boolean adjacent(V v) {
        return v1.equals(v) || v2.equals(v);
    }
    
    /**
     * Return vertex opposite provided vertex
     * @param v test vertex
     * @return opposite vertex, or null if vertex is not part of this edge
     */
    public V opposite(V v) {
        return v1.equals(v) ? v2 : v2.equals(v) ? v1 : null;
    }

}
