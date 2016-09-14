/**
 * AbstractDefaultGraphGenerator.java
 * Created May 19, 2016
 */
package com.googlecode.blaisemath.graph.mod.generators;

/*
 * #%L
 * blaise-graphtheory3
 * --
 * Copyright (C) 2009 - 2016 Elisha Peterson
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


import com.googlecode.blaisemath.graph.GraphGenerator;

/**
 * Graph generator that uses {@link DefaultGeneratorParameters}.
 * 
 * @author elisha
 */
public abstract class AbstractDefaultGraphGenerator implements GraphGenerator<DefaultGeneratorParameters,Integer> {

    protected final String name;

    public AbstractDefaultGraphGenerator(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return name;
    }

    @Override
    public DefaultGeneratorParameters createParameters() {
        return new DefaultGeneratorParameters();
    }
    
}
