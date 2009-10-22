/**
 * FunctionAdapter.java
 * Created on Apr 28, 2009
 */

package scio.function;

import java.util.Vector;

/**
 * <p>
 * Adapts the <code>Function</code> interface, particularly to implement the vector valued
 * getValue method.
 * </p>
 * @author Elisha Peterson
 */
@Deprecated
public abstract class FunctionAdapter<C,D> implements Function<C,D> {

    /** Returns an array of values of the function at an array of inputs. */
    @Deprecated
    public Vector<D> getValue(Vector<C> xx) throws org.apache.commons.math.FunctionEvaluationException {
        Vector<D> result = new Vector<D>(xx.size());
        for (C x : xx) {
            result.add(getValue(x));
        }
        return result;
    }

}
