/*
 * LonGraphGenerator.java
 * Created May 21, 2010
 */
package com.googlecode.blaisemath.graph.lon;

import com.googlecode.blaisemath.graph.Graph;


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

/**
 * Generates a longitudinal graph based on provided settings.
 * @param <P> parameters type
 * @param <V> graph vertex type
 * @author elisha
 */
public interface LonGraphGenerator<P,V> {

    /**
     * Create instance of parameters object
     * @return parameters instance
     */
    P createParameters();
    
    /**
     * Generate graph based on given parameters.
     * @param parm the parameters
     * @return generated graph
     */
    LonGraph<V> generate(P parm);
    
}
