/**
 * Edge.java
 * Created Aug 5, 2012
 */

package com.googlecode.blaisemath.util;

import com.google.common.base.Objects;
import static com.google.common.base.Preconditions.checkNotNull;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2014 - 2017 Elisha Peterson
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

/**
 * Simple data structure representing an "edge" between two non-null vertices.
 * @param <V> type of node
 * 
 * @author elisha
 */
@Immutable
public class Edge<V> {
    
    @Nonnull
    protected final V v1;
    @Nonnull
    protected final V v2;
    
    /**
     * Initialize edge, using provided edge's vertices.
     * @param edge the edge whose vertices to use
     */
    public Edge(Edge<? extends V> edge) {
        this(edge.getNode1(), edge.getNode2());
    }

    /**
     * Initialize edge.
     * @param v1 first vertex
     * @param v2 second vertex
     */
    public Edge(V v1, V v2) {
        this.v1 = checkNotNull(v1);
        this.v2 = checkNotNull(v2);
    }

    @Override
    public String toString() {
        return String.format("%s:%s",v1,v2);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != Edge.class) {
            return false;
        }
        final Edge<?> other = (Edge<?>) obj;
        return Objects.equal(v1, other.v1) && Objects.equal(v2, other.v2);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.v1.hashCode();
        hash = 97 * hash + this.v2.hashCode();
        return hash;
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //
    
    /**
     * Edge's first node
     * @return node 1
     */
    public V getNode1() {
        return v1;
    }
    
    /**
     * Edge's second node
     * @return node 2
     */
    public V getNode2() {
        return v2;
    }
    
    //</editor-fold>
    
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
     * @return opposite vertex
     * @throws IllegalArgumentException if argument is not one of the vertices
     */
    public V opposite(V v) {
        if (v1.equals(v)) {
            return v2;
        } else if (v2.equals(v)) {
            return v1;
        } else {
            throw new IllegalArgumentException(v+" is not a vertex of this edge.");
        }
    }
    
    
    /** 
     * Undirected version of an edge.
     * @param <V> type of node
     */
    @Immutable
    public static final class UndirectedEdge<V> extends Edge<V> {

        public UndirectedEdge(V v1, V v2) {
            super(v1, v2);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 97 * hash + this.v1.hashCode() + this.v2.hashCode();
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || obj.getClass() != UndirectedEdge.class) {
                return false;
            }
            final UndirectedEdge<?> other = (UndirectedEdge<?>) obj;
            return (Objects.equal(v1, other.v1) && Objects.equal(v2, other.v2))
                    || (Objects.equal(v1, other.v2) && Objects.equal(v2, other.v1));
        }
        
    }

}
