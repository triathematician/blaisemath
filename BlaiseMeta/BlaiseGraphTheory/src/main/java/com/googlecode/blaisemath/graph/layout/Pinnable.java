/*
 * Pinnable.java
 * Created on Aug 27, 2012
 */

package com.googlecode.blaisemath.graph;

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


import java.util.Collection;
import java.util.Set;

/**
 * Interface for a layout algorithm that supports "pinning" nodes.
 *
 * @param <O> type of node
 *
 * @author petereb1
 */
public interface Pinnable<O> {

    /**
     * Get the pinned set.
     * @return pinned set
     */
    Set<O> getPinnedNodes();

    /**
     * Replace pinned nodes with given set.
     * @param nodes nodes to pin
     */
    void setPinnedNodes(Collection<? extends O> nodes);

    /**
     * Removes all nodes from pinned sets
     */
    void clearPinnedNodes();

    /**
     * Add to pinned nodes
     * @param nodes nodes to pin
     */
    void pinNodes(Collection<? extends O> nodes);

    /**
     * Remove nodes from pinned set
     * @param nodes nodes to unpin
     */
    void unpinNodes(Collection<? extends O> nodes);

}
