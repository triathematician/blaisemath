/**
 * SemanticLeafNode.java
 * Created on Dec 27, 2009
 */

package com.googlecode.blaisemath.parser;

/*
 * #%L
 * BlaiseParser
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

import java.util.Collections;
import java.util.Enumeration;
import javax.swing.tree.TreeNode;

/**
 * <p>
 *    This abstract class implements methods as appropriate for a leaf node, which
 *    does not permit any arguments. Sub-classes must still implement the
 *    methods in the <code>SemanticNode</code> interface, meaning the <code>value()</code>
 *    and <code>unknowns()</code> methods.
 * </p>
 * @author Elisha Peterson
 */
abstract class SemanticLeafNodeSupport implements SemanticNode {

    /** The parent object of the node */
    SemanticNode parent;
    /** The name of the node. */
    String name;
    /** The value of the node. */
    Object value;

    /** Basic constructor for the leaf. */
    protected SemanticLeafNodeSupport() {
        this.parent = null;
    }

    /** Constructs the leaf with a name and a value. */
    protected SemanticLeafNodeSupport(String name, Object value) {
        this.parent = null;
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        if (name == null)
            return "" + value;
        else
            return name + (value == null ? "" : "=" + value);
    }


    //
    // SemanticNode METHODS
    //

    public Class[] getParameterTypes() {
        return new Class[]{};
    }


    //
    // TreeNode METHODS
    //

    public TreeNode getParent() {
        return parent;
    }

    public boolean isLeaf() {
        return true;
    }

    public boolean getAllowsChildren() {
        return false;
    }

    public int getChildCount() {
        return 0;
    }

    public int getIndex(TreeNode node) {
        return -1;
    }

    public Enumeration children() {
        return (Enumeration) Collections.emptyList();
    }

    public TreeNode getChildAt(int childIndex) {
        throw new ArrayIndexOutOfBoundsException("SemanticLeafNode does not support children.");
    }
}
