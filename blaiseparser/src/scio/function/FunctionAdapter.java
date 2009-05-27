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
public abstract class FunctionAdapter<C,D> implements Function<C,D> {

    /** Returns an array of values of the function at an array of inputs. */
    public Vector<D> getValue(Vector<C> xx) throws FunctionValueException {
        Vector<D> result = new Vector<D>(xx.size());
        for (C x : xx) {
            result.add(getValue(x));
        }
        return result;
    }

}
