package com.googlecode.blaisemath.svg.render;

/*-
 * #%L
 * blaise-svg
 * --
 * Copyright (C) 2014 - 2025 Elisha Peterson
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

import com.googlecode.blaisemath.svg.xml.SvgElement;
import com.googlecode.blaisemath.svg.xml.SvgGroup;
import com.googlecode.blaisemath.svg.xml.SvgRoot;

import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Builder object used for constructing a tree of SVG elements.
 * @author Elisha Peterson
 */
public class SvgTreeBuilder {

    private SvgRoot root = new SvgRoot();
    private LinkedList<SvgGroup> groups = new LinkedList<>();

    public SvgTreeBuilder() {
        groups.add(root);
    }

    public SvgRoot getRoot() {
        return root;
    }

    /**
     * Add target element to the tree, at the current group.
     * @param res to add
     * @throws NullPointerException if there is no current group
     */
    public void add(SvgElement res) {
        groups.peekLast().elements.add(res);
    }

    /**
     * Creates and returns a new group.
     * @return new group
     * @throws NullPointerException if there is no current group
     */
    public SvgGroup beginGroup() {
        SvgGroup g = new SvgGroup();
        add(g);
        groups.add(g);
        return g;
    }

    /**
     * Ends current group.
     * @throws NoSuchElementException if there is no current group
     */
    public void endGroup() {
        groups.removeLast();
    }

}
