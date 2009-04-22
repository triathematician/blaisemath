/*
 * ErrorFunction.java
 * Created on Feb 27, 2008
 */

package scribo.tree;

/**
 * ErrorFunction contains more functions which can be added to a function tree.
 * @author Elisha Peterson
 */
public class ErrorFunction {
    public static class Erf extends FunctionTreeFunctionNode {
        public Erf(FunctionTreeNode argument){super(argument);}
        @Override
        public void initFunctionType(){setFunctionNames("erf",null,null,null);}    
        @Override
        public FunctionTreeNode derivativeTree(Variable v){return null;}
        public Double getValue(Double x){return null;}
    }
}
