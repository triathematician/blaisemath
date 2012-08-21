/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package update2.token;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.tree.TreeNode;

/**
 *
 * @author elisha
 */
public abstract class TokenNode implements TreeNode {
    String name;
    TokenNode parent;
    TokenType type;
    List<TokenNode> children;
    /** Construction requires setting the parent node and providing a node name. */
    public TokenNode(TokenNode parent, TokenType type, String name) { this.name = name; this.type = type; this.parent = parent; children = new ArrayList<TokenNode>(); }
    /** Returns true if the node supports adding children of the specified type. */
    abstract boolean canAdd(TokenType child);
    /** Adds a child at this node, and returns that child. */
    TokenNode addNode(TokenNode child) { children.add(child); return child; }
    /** Removes a child from this node. */
    boolean removeNode(TokenNode node) { if (node.parent == this) { node.parent = null; } return children.remove(node); }
    @Override public String toString() { return name + ( children.isEmpty() ? "" : children.toString() ); }
    /** Returns the last child of the node. */
    TokenNode lastChild() { return children.get(children.size() - 1); }
    /** Returns the parent group node for this node, that is the first group node higher up in the tree that represents an open group. */
    TokenNode groupParent() {
        if (parent == null) return this;
        if (parent instanceof GroupNode && ! (parent instanceof FunctionNode) ) return parent;
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
        if (Parser.VERBOSE) System.out.println("Finding child @ depth " + depth + " from node " + this);
        // if this has no children, should always return the leaf
        if (isLeaf() || this instanceof BasicNode) { return this; }
        // returns this node if it's an operator node at requisite depth
        if (this instanceof OperatorNode && ((OperatorNode) this).depth() >= depth) { return this; }
        // if the last child is not an operator, return it
        if ( ! (lastChild() instanceof OperatorNode) )  { return lastChild(); }
        // if child is an operator node, make a recursive call
        return lastChild().findChildAtMaximumDepth(depth);
    }
    //
    // TreeNode Interface methods
    //
    public TreeNode getChildAt(int childIndex) { return children.get(childIndex); }
    public int getChildCount() { return children.size(); }
    public TreeNode getParent() { return parent; }
    public int getIndex(TreeNode node) { return children.indexOf(node); }
    public boolean getAllowsChildren() { return true; }
    public boolean isLeaf() { return children.isEmpty(); }
    public Enumeration children() { return (Enumeration) children.iterator(); }
}


/** Represents a "leaf" node... e.g. a number or identifier. */
class BasicNode extends TokenNode {
    public BasicNode(TokenNode parent, TokenType type, String name) { super(parent, type, name); }
    @Override boolean canAdd(TokenType child) { return false; }
    @Override public boolean getAllowsChildren() { return false; }
    @Override public boolean isLeaf() { return true; }
}
/** Represents a function, a parenthetical group, or the entire expression. */
class GroupNode extends TokenNode {
    public GroupNode(TokenNode parent, TokenType type, String name) { super(parent, type, name); }
    @Override boolean canAdd(TokenType child) { return (children.isEmpty() && child.canStartPhrase) || ! children.isEmpty(); }
    boolean closesWith(String token) { return Parentheticals.match(name, token); }
}

/** Represents a function. */
class FunctionNode extends GroupNode {
    public FunctionNode(TokenNode parent, TokenType type, String name) { super(parent, type, name); }
    @Override boolean closesWith(String token) { return token.length()==0; }
}

/** Represents an operator node. */
class OperatorNode extends TokenNode {
    public OperatorNode(TokenNode parent, TokenType type, String name) { super(parent, type, name); }
    @Override boolean canAdd(TokenType child) { return child.canStartPhrase; }
    int depth() { return Operators.getDepth(name); }
}
