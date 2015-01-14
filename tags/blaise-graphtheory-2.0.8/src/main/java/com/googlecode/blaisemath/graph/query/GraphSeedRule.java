/*
 * GraphSeedRule.java
 * Created on Jul 3, 2012
 */
package com.googlecode.blaisemath.graph.query;

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

import com.google.common.base.Function;
import java.util.Set;
import com.googlecode.blaisemath.graph.Graph;

/**
 * A "seed rule" to select a (small) portion of a (large) graph for analysis.
 *
 * @author Elisha Peterson
 */
public interface GraphSeedRule extends Function<Graph,Set> {

    /**
     * Name of rule for display
     * @return name
     */
    String getName();

}
