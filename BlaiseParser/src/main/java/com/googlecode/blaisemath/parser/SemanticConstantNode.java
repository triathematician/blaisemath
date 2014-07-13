/*
 * SemanticConstantNode.java
 * Created on Nov 26, 2009, 2:42:23 PM
 */
package com.googlecode.blaisemath.parser;

/*
 * #%L
 * BlaiseParser
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

import java.util.Collections;
import java.util.Map;

/**
 * <p>
 *    A node that has a constant value. This class ensures that the value is not null;
 *    attempts to set a null value result in an <code>IllegalArgumentException</code>.
 *    The name of the constant may be null, however. There is no support for changing
 *    the value once the class has been constructed.
 * </p>
 * @author elisha
 */
class SemanticConstantNode extends SemanticLeafNodeSupport {

    /** Construct the node with specified value. */
    public SemanticConstantNode(Object value) {
        this(null, value);
    }
    
    /** Construct node with specified value and name */
    public SemanticConstantNode(String name, Object value) {
        if (value == null)
            throw new IllegalArgumentException("Cannot construct a SemanticConstantNode with a null value!");
        this.name = name;
        this.value = value;
    }

    //
    // SemanticNode INTERFACE METHODS
    //
    
    public Object getValue() throws SemanticTreeEvaluationException {
        return value;
    }

    public Map<String, Class> getVariables() {
        return Collections.EMPTY_MAP;
    }

    public Class<?> getValueType() {
        return value.getClass();
    }

    /** Does nothing as this is a constant node. */
    public void assignVariables(Map<String, ? extends Object> valueTable) {
    }
}
