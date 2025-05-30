package com.googlecode.blaisemath.graph.layout;

/*
 * #%L
 * BlaiseGraphTheory (v3)
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

import java.util.Collections;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * A set of constraints for use in graph layouts. Allows nodes to be "pinned" during layout, constrained into boxes, etc.
 * @param <N> node type
 * @author Elisha Peterson
 */
public class GraphLayoutConstraints<N> {
    
    private Set<N> pinnedNodes = Collections.emptySet();

    public Set<N> getPinnedNodes() {
        return pinnedNodes;
    }

    public void setPinnedNodes(Set<N> pinnedNodes) {
        this.pinnedNodes = requireNonNull(pinnedNodes);
    }
    
}
