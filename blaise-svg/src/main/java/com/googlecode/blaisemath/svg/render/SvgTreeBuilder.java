package com.googlecode.blaisemath.svg.render;

import com.googlecode.blaisemath.svg.xml.SvgElement;
import com.googlecode.blaisemath.svg.xml.SvgGroup;
import com.googlecode.blaisemath.svg.xml.SvgRoot;

import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Builder object used for constructing a tree of SVG elements.
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
