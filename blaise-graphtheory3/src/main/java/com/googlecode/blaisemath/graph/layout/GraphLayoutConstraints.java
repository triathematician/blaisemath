/*
 * Copyright 2016 elisha.
 *
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
 */
package com.googlecode.blaisemath.graph.layout;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collections;
import java.util.Set;

/*
 * #%L
 * BlaiseGraphTheory (v3)
 * --
 * Copyright (C) 2009 - 2018 Elisha Peterson
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
 * <p>
 *   A set of constraints for use in graph layouts. Allows nodes to be "pinned"
 *   during layout, constrained into boxes, etc.
 * </p>
 * @param <C> node type
 * @author elisha
 */
public class GraphLayoutConstraints<C> {
    
    private Set<C> pinnedNodes = Collections.emptySet();

    public Set<C> getPinnedNodes() {
        return pinnedNodes;
    }

    public void setPinnedNodes(Set<C> pinnedNodes) {
        this.pinnedNodes = checkNotNull(pinnedNodes);
    }
    
}
