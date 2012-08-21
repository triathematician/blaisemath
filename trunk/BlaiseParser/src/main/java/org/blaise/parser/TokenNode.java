/*
 * TokenNode.java
 * Created Nov 2009
 */
package org.blaise.parser;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.tree.TreeNode;

/**
 * <code>
 *      This is a node in a parsed expression tree that represents a single token.
 * </code>
 * @author elisha
 */
abstract class TokenNode implements TreeNode {

    String name;
    TokenNode parent;
    TokenType type;
    List<TokenNode> children;

    /** Construction requires setting the parent node and providing a node name. */
    public TokenNode(TokenNode parent, TokenType type, String name) {
        this.name = name;
        this.type = type;
        this.parent = parent;
        children = new ArrayList<TokenNode>();
    }

    /** Returns true if the node supports adding children of the specified type. */
    abstract boolean canAdd(TokenType child);

    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /** Adds a child at this node, and returns that child. */
    TokenNode addNode(TokenNode child) {
        children.add(child);
        child.parent = this;
        return child;
    }

    /** Removes a child from this node. */
    boolean removeNode(TokenNode node) {
        if (children.remove(node)) {
            node.parent = null;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return name + (children.isEmpty() ? "" : children.toString());
    }

    /** Returns the last child of the node. */
    TokenNode lastChild() {
        return children.get(children.size() - 1);
    }

    /** Returns the parent group node for this node, that is the first group node higher up in the tree that represents an open group. */
    TokenNode groupParent() {
        if (parent == null) {
            return this;
        }
        if (parent instanceof GroupNode && !(parent instanceof FunctionNode)) {
            return parent;
        }
        return parent.groupParent();
    }

    /**
     * Looks through children to find operator of appropriate depth.
     * This is intended to trace the tree of last operators... any child node
     * reached that is not an operator should result in returning that node.
     * Otherwise, the node returned is the first child at depth at least that
     * provided.
     */
    TokenNode findChildAtMaximumDepth(int depth) {
        // if this has no children, should always return the leaf
        if (isLeaf() || this instanceof BasicNode) {
            return this;
        }
        // returns this node if it's an operator node at requisite depth
        if (this instanceof OperatorNode && ((OperatorNode) this).depth() >= depth) {
            return this;
        }
        // if the last child is not an operator, return it
        if (!(lastChild() instanceof OperatorNode)) {
            return lastChild();
        }
        // if child is an operator node, make a recursive call
        return lastChild().findChildAtMaximumDepth(depth);
    }
    
    /** Searches through to find the last descendant of the node. */
    TokenNode getLastDescendant() {
        return children.size()==0 ? this : children.get(children.size()-1).getLastDescendant();
    }

    //
    // TreeNode Interface methods
    //

    public TreeNode firstChild() {
        return children.get(0);
    }

    public TreeNode getChildAt(int childIndex) {
        return children.get(childIndex);
    }

    public int getChildCount() {
        return children.size();
    }

    public TreeNode getParent() {
        return parent;
    }

    public int getIndex(TreeNode node) {
        return children.indexOf(node);
    }

    public boolean getAllowsChildren() {
        return true;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public Enumeration children() {
        return (Enumeration) children.iterator();
    }


    /** Represents a "leaf" node... e.g. a number or identifier. */
    public static class BasicNode extends TokenNode {

        public BasicNode(TokenNode parent, TokenType type, String name) {
            super(parent, type, name);
        }

        @Override
        boolean canAdd(TokenType child) {
            return false;
        }

        @Override
        public boolean getAllowsChildren() {
            return false;
        }

        @Override
        public boolean isLeaf() {
            return true;
        }
    }

    /** Represents a function, a parenthetical group, or the entire expression. */
    public static class GroupNode extends TokenNode {

        String closeToken;

        public GroupNode(TokenNode parent, TokenType type, String name, String closeToken) {
            super(parent, type, name);
            this.closeToken = closeToken;
        }

        @Override
        boolean canAdd(TokenType child) {
            return (children.isEmpty() && (child.canStartPhrase || child==TokenType.PARENTHETICAL_CLOSE)) || !children.isEmpty();
        }

        boolean closesWith(String token) {
            return closeToken.equals(token);
        }
    }

    /** Represents a function. */
    public static class FunctionNode extends GroupNode {

        public FunctionNode(TokenNode parent, TokenType type, String name) {
            super(parent, type, name, "");
        }

        @Override
        boolean closesWith(String token) {
            return token.length() == 0;
        }
    }

    /** Represents an operator node. */
    public static class OperatorNode extends TokenNode {
        int depth;

        public OperatorNode(TokenNode parent, TokenType type, String name, int depth) {
            super(parent, type, name);
            this.depth = depth;
        }

        @Override
        boolean canAdd(TokenType child) {
            return child.canStartPhrase;
        }

        /**
         * Returns the depth of the operator. Unary nodes should return Integer.MAX_VALUE or another very large value.
         * @return
         */
        int depth() {
            return depth;
        }
    }
}
