/**
 * SemanticArgumentNode.java
 * Created on Nov 30, 2009
 */

package org.blaise.parser;

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


/**
 * <p>
 *   This interface adds additional functionality to the <code>SemanticNode</code>
 *   interface in order to allow for arguments to functions of an underlying type.
 * </p>
 *
 * @author Elisha Peterson
 */
public interface SemanticArgumentNode extends SemanticNode {

    /**
     * Returns the argument types associated with the node.
     * @return list of argument types associated with the class.
     */
    public Class<?>[] getParameterTypes();

}
