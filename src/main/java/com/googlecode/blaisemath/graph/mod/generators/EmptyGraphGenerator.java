/**
 * EmptyGraphSupplier.java
 * Created 2012
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

import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.GraphGenerator;
import java.util.Collections;

/** 
 * Constructs graph with n vertices.
 * @author Elisha Peterson
 */
public final class EmptyGraphGenerator implements GraphGenerator<DefaultGeneratorParameters,Integer> {

    @Override
    public String toString() {
        return "Empty Graph";
    }

    @Override
    public DefaultGeneratorParameters createParameters() {
        return new DefaultGeneratorParameters();
    }

    @Override
    public Graph<Integer> generate(DefaultGeneratorParameters parm) {
        return DefaultGeneratorParameters.createGraphWithEdges(parm, Collections.EMPTY_SET);
    }
    
}
